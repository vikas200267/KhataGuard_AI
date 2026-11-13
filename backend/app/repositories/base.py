from __future__ import annotations

from abc import ABC, abstractmethod

from app.models.schemas import AnalyzeResponse, DocumentRecord, ExtractionOutput, PropertyCase


class CaseRepository(ABC):
    @abstractmethod
    def create_case(self, case: PropertyCase) -> PropertyCase:
        raise NotImplementedError

    @abstractmethod
    def get_case(self, case_id: str, user_id: str) -> PropertyCase | None:
        raise NotImplementedError

    @abstractmethod
    def list_cases(self, user_id: str) -> list[PropertyCase]:
        raise NotImplementedError

    @abstractmethod
    def delete_case(self, case_id: str, user_id: str) -> bool:
        raise NotImplementedError

    @abstractmethod
    def add_document(self, case_id: str, user_id: str, document: DocumentRecord) -> None:
        raise NotImplementedError

    @abstractmethod
    def save_analysis(self, case_id: str, user_id: str, result: AnalyzeResponse) -> None:
        raise NotImplementedError

    @abstractmethod
    def get_analysis(self, case_id: str, user_id: str) -> AnalyzeResponse | None:
        raise NotImplementedError

    @abstractmethod
    def save_extraction(self, case_id: str, user_id: str, extraction: ExtractionOutput) -> None:
        raise NotImplementedError

    @abstractmethod
    def list_extractions(self, case_id: str, user_id: str) -> list[ExtractionOutput]:
        raise NotImplementedError
