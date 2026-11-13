from __future__ import annotations

import json
from abc import ABC, abstractmethod
from pathlib import Path

from app.core.config import get_settings
from app.models.schemas import DocumentRecord, ExtractionOutput


class ExtractionProvider(ABC):
    @abstractmethod
    def extract(self, document: DocumentRecord) -> ExtractionOutput:
        raise NotImplementedError


class MockExtractionProvider(ExtractionProvider):
    def __init__(self) -> None:
        fixture_path = Path(__file__).resolve().parents[2] / ".." / "sample-data" / "extracted-json" / "demo_case.json"
        self._fixture_data = json.loads(fixture_path.resolve().read_text(encoding="utf-8"))

    def extract(self, document: DocumentRecord) -> ExtractionOutput:
        lower_name = document.filename.lower()
        key = "sale_deed" if "sale" in lower_name else "khata"
        payload = self._fixture_data[key]
        payload["document_id"] = document.document_id
        payload["source_references"] = [{"document_id": document.document_id, "page": 1}]
        return ExtractionOutput(**payload)


class GeminiExtractionProvider(ExtractionProvider):
    def extract(self, document: DocumentRecord) -> ExtractionOutput:
        settings = get_settings()
        if not settings.gemini_api_key:
            raise RuntimeError("Gemini extraction unavailable: missing GEMINI_API_KEY")

        # Placeholder production integration contract.
        # Use structured JSON mode and validate against ExtractionOutput in production deployment.
        raise RuntimeError("Gemini integration stub: configure real API invocation for deployment")


def get_extraction_provider() -> ExtractionProvider:
    settings = get_settings()
    if settings.gemini_api_key and not settings.demo_mode:
        return GeminiExtractionProvider()
    return MockExtractionProvider()
