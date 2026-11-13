"""Firestore repository scoped under a Firebase UID for every operation."""

from __future__ import annotations

from typing import Any

from app.core.firebase import get_firebase_app
from app.models.schemas import AnalyzeResponse, DocumentRecord, ExtractionOutput, PropertyCase
from app.repositories.base import CaseRepository


class FirestoreCaseRepository(CaseRepository):
    def __init__(self, client: Any | None = None) -> None:
        if client is None:
            from firebase_admin import firestore

            client = firestore.client(app=get_firebase_app())
        self._db = client

    def _case_ref(self, case_id: str, user_id: str):
        return self._db.collection("users").document(user_id).collection("cases").document(case_id)

    def create_case(self, case: PropertyCase) -> PropertyCase:
        self._case_ref(case.case_id, case.user_id).set(case.model_dump(mode="json"))
        return case

    def get_case(self, case_id: str, user_id: str) -> PropertyCase | None:
        snapshot = self._case_ref(case_id, user_id).get()
        if not snapshot.exists:
            return None
        return PropertyCase.model_validate(snapshot.to_dict())

    def list_cases(self, user_id: str) -> list[PropertyCase]:
        cases = self._db.collection("users").document(user_id).collection("cases")
        return [PropertyCase.model_validate(snapshot.to_dict()) for snapshot in cases.stream()]

    def delete_case(self, case_id: str, user_id: str) -> bool:
        case_ref = self._case_ref(case_id, user_id)
        if not case_ref.get().exists:
            return False

        # These are the only subcollections created by this service. Delete each
        # child document before its parent; Firestore does not cascade deletes.
        for subcollection in ("analyses", "extractions"):
            for snapshot in case_ref.collection(subcollection).stream():
                snapshot.reference.delete()
        case_ref.delete()
        return True

    def add_document(self, case_id: str, user_id: str, document: DocumentRecord) -> None:
        from google.cloud.firestore_v1 import ArrayUnion

        case_ref = self._case_ref(case_id, user_id)
        if case_ref.get().exists:
            case_ref.update({"documents": ArrayUnion([document.model_dump(mode="json")])})

    def save_analysis(self, case_id: str, user_id: str, result: AnalyzeResponse) -> None:
        case = self._case_ref(case_id, user_id)
        if case.get().exists:
            case.collection("analyses").document("latest").set(result.model_dump(mode="json"))

    def get_analysis(self, case_id: str, user_id: str) -> AnalyzeResponse | None:
        snapshot = self._case_ref(case_id, user_id).collection("analyses").document("latest").get()
        if not snapshot.exists:
            return None
        return AnalyzeResponse.model_validate(snapshot.to_dict())

    def save_extraction(self, case_id: str, user_id: str, extraction: ExtractionOutput) -> None:
        case = self._case_ref(case_id, user_id)
        if case.get().exists:
            case.collection("extractions").document(extraction.document_id).set(extraction.model_dump(mode="json"))

    def list_extractions(self, case_id: str, user_id: str) -> list[ExtractionOutput]:
        case = self._case_ref(case_id, user_id)
        if not case.get().exists:
            return []
        return [
            ExtractionOutput.model_validate(snapshot.to_dict())
            for snapshot in case.collection("extractions").stream()
        ]
