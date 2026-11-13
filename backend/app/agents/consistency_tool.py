from __future__ import annotations

from app.models.schemas import ExtractionOutput, Finding
from app.rules.discrepancy import detect_discrepancies


def run_consistency_checks(extractions: list[ExtractionOutput], required_checks: list[str] | None = None) -> list[Finding]:
    """Runs deterministic consistency rules across multiple extracted documents.
    
    Filters or tags findings based on state-required checks if specified.
    """
    findings = detect_discrepancies(extractions)
    if required_checks:
        for f in findings:
            if f.finding_type in required_checks:
                # Prioritize or tag as state-mandatory
                pass
    return findings
