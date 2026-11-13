from __future__ import annotations

from app.models.schemas import DocumentRecord, ExtractionOutput
from app.services.extraction import get_extraction_provider
from app.agents.normalization_tool import normalize_extraction


class ExtractionAgent:
    """Agent responsible for document understanding and schema validation."""

    def __init__(self) -> None:
        self.provider = get_extraction_provider()

    def process_document(self, document: DocumentRecord) -> ExtractionOutput:
        raw_output = self.provider.extract(document)
        # Normalize fields immediately after extraction
        normalized = normalize_extraction(raw_output)
        return normalized

    def process_batch(self, documents: list[DocumentRecord]) -> list[ExtractionOutput]:
        return [self.process_document(doc) for doc in documents]
