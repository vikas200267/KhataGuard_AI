from __future__ import annotations

from app.models.schemas import AnalyzeResponse, ExtractionOutput
from app.rules.discrepancy import detect_discrepancies


def run_agent_workflow(case_id: str, extractions: list[ExtractionOutput]) -> AnalyzeResponse:
    # ADK-compatible deterministic orchestration scaffold.
    findings = detect_discrepancies(extractions)
    return AnalyzeResponse(case_id=case_id, findings=findings, extracted_documents=extractions)
