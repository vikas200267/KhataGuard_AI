from __future__ import annotations

from collections import defaultdict
from threading import Lock

from app.models.schemas import AnalyzeResponse, ExtractionOutput, PropertyCase
from app.repositories.base import CaseRepository


class LocalCaseRepository(CaseRepository):
    def __init__(self) -> None:
        self._cases: dict[str, PropertyCase] = {}
        self._analysis: dict[str, AnalyzeResponse] = {}
        self._docs: dict[str, list[str]] = defaultdict(list)
        self._extractions: dict[str, list[ExtractionOutput]] = defaultdict(list)
        self._lock = Lock()

    def create_case(self, case: PropertyCase) -> PropertyCase:
        with self._lock:
            self._cases[case.case_id] = case
        return case

    def get_case(self, case_id: str, user_id: str) -> PropertyCase | None:
        case = self._cases.get(case_id)
        if not case or case.user_id != user_id:
            return None
        return case

    def delete_case(self, case_id: str, user_id: str) -> bool:
        with self._lock:
            case = self._cases.get(case_id)
            if not case or case.user_id != user_id:
                return False
            self._cases.pop(case_id, None)
            self._analysis.pop(case_id, None)
            self._docs.pop(case_id, None)
            self._extractions.pop(case_id, None)
            return True

    def add_document(self, case_id: str, user_id: str, document_id: str) -> None:
        case = self.get_case(case_id, user_id)
        if not case:
            return
        self._docs[case_id].append(document_id)

    def save_analysis(self, case_id: str, user_id: str, result: AnalyzeResponse) -> None:
        if self.get_case(case_id, user_id):
            self._analysis[case_id] = result

    def get_analysis(self, case_id: str, user_id: str) -> AnalyzeResponse | None:
        case = self.get_case(case_id, user_id)
        if not case:
            return None
        return self._analysis.get(case_id)

    def save_extraction(self, case_id: str, user_id: str, extraction: ExtractionOutput) -> None:
        if self.get_case(case_id, user_id):
            self._extractions[case_id].append(extraction)

    def list_extractions(self, case_id: str, user_id: str) -> list[ExtractionOutput]:
        if not self.get_case(case_id, user_id):
            return []
        return self._extractions.get(case_id, [])
