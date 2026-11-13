from __future__ import annotations

import json
import copy
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
        payload = copy.deepcopy(self._fixture_data[key])
        payload["document_id"] = document.document_id
        payload["source_references"] = [{"document_id": document.document_id, "page": 1}]
        return ExtractionOutput(**payload)


class GeminiExtractionProvider(ExtractionProvider):
    def extract(self, document: DocumentRecord) -> ExtractionOutput:
        from google import genai  # ponytail: lazy import
        from google.genai import types

        settings = get_settings()
        if not settings.gemini_api_key:
            raise RuntimeError("Gemini extraction unavailable: missing GEMINI_API_KEY")

        client = genai.Client(api_key=settings.gemini_api_key)
        
        try:
            document_bytes = Path(document.source_uri).read_bytes()
        except OSError as exc:
            raise RuntimeError("Uploaded document is unavailable for extraction") from exc

        prompt = f"""Extract property information from the attached {document.document_type} document.
Return one JSON object with keys: document_type, state, language, owner_name,
survey_number, area_value, area_unit, location, document_date,
document_identifiers, boundary_description, missing_or_unreadable_fields,
source_references. Use null for any absent or unreadable scalar; use [] for
absent lists. Do not infer or fill gaps. Keep area_value in the unit shown and
set area_unit to sq_ft, sq_m, sq_yd, or the printed unit. Dates must be ISO
YYYY-MM-DD only when unambiguous. source_references must include page numbers
only when the page is identifiable from the document; otherwise return [].
The upload filename is {document.filename!r}. The declared MIME type is
{document.content_type!r}. Return JSON only."""

        response = client.models.generate_content(
            model=settings.gemini_model or "gemini-3.6-flash",
            contents=[prompt, types.Part.from_bytes(data=document_bytes, mime_type=document.content_type)],
            config=types.GenerateContentConfig(response_mime_type="application/json"),
        )
        raw_text = response.text
        if not raw_text:
            raise ValueError("Gemini returned an empty extraction response")
        payload = json.loads(raw_text)
        payload["document_id"] = document.document_id
        payload.setdefault("document_type", document.document_type)
        payload.setdefault("state", "Unknown")
        payload.setdefault("language", "Unknown")
        payload.setdefault("source_references", [])
        for reference in payload["source_references"]:
            if isinstance(reference, dict):
                reference["document_id"] = document.document_id
        return ExtractionOutput(**payload)


def get_extraction_provider() -> ExtractionProvider:
    settings = get_settings()
    if settings.gemini_api_key and not settings.demo_mode:
        return GeminiExtractionProvider()
    return MockExtractionProvider()
