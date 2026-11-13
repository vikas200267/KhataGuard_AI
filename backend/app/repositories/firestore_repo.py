"""Firestore-backed repository skeleton for production use.

This implementation intentionally remains lightweight for local/demo setup.
"""

from app.models.schemas import AnalyzeResponse, ExtractionOutput, PropertyCase
from app.repositories.base import CaseRepository


class FirestoreCaseRepository(CaseRepository):
    def create_case(self, case: PropertyCase) -> PropertyCase:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def get_case(self, case_id: str, user_id: str) -> PropertyCase | None:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def delete_case(self, case_id: str, user_id: str) -> bool:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def add_document(self, case_id: str, user_id: str, document_id: str) -> None:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def save_analysis(self, case_id: str, user_id: str, result: AnalyzeResponse) -> None:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def get_analysis(self, case_id: str, user_id: str) -> AnalyzeResponse | None:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def save_extraction(self, case_id: str, user_id: str, extraction: ExtractionOutput) -> None:
        raise NotImplementedError("Firestore repository wiring required for production deployment")

    def list_extractions(self, case_id: str, user_id: str) -> list[ExtractionOutput]:
        raise NotImplementedError("Firestore repository wiring required for production deployment")
