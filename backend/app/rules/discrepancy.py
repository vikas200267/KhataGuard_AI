from __future__ import annotations

from difflib import SequenceMatcher

from app.models.schemas import AreaValue, ExtractionOutput, Finding, Severity
from app.rules.area import absolute_difference_sq_ft


AREA_DISCREPANCY_THRESHOLD = 50.0


def _name_similarity(a: str, b: str) -> float:
    return SequenceMatcher(None, a.strip().lower(), b.strip().lower()).ratio()


def detect_discrepancies(extractions: list[ExtractionOutput]) -> list[Finding]:
    findings: list[Finding] = []

    if len(extractions) < 2:
        return findings

    first, second = extractions[0], extractions[1]

    if first.survey_number and second.survey_number and first.survey_number != second.survey_number:
        findings.append(
            Finding(
                finding_type="survey_number_mismatch",
                severity=Severity.high,
                explanation=f"Survey numbers differ: {first.survey_number} vs {second.survey_number}",
                source_references=[first.document_id, second.document_id],
                recommended_next_step="Verify survey number with official registry and encumbrance records.",
            )
        )

    if first.area_value and first.area_unit and second.area_value and second.area_unit:
        diff = absolute_difference_sq_ft(
            AreaValue(value=first.area_value, unit=first.area_unit),
            AreaValue(value=second.area_value, unit=second.area_unit),
        )
        if diff >= AREA_DISCREPANCY_THRESHOLD:
            findings.append(
                Finding(
                    finding_type="property_area_difference",
                    severity=Severity.medium,
                    explanation=f"Area differs by {diff:.0f} sq. ft. across documents.",
                    source_references=[first.document_id, second.document_id],
                    recommended_next_step="Request latest survey sketch and verify built-up vs site area basis.",
                )
            )

    owners = [e.owner_name for e in extractions if e.owner_name]
    if len(owners) == 1:
        findings.append(
            Finding(
                finding_type="missing_owner",
                severity=Severity.medium,
                explanation="Owner name is missing in one or more documents.",
                source_references=[e.document_id for e in extractions],
                recommended_next_step="Collect ownership proof in all required records.",
            )
        )
    elif len(owners) >= 2:
        similarity = _name_similarity(owners[0], owners[1])
        if 0.65 <= similarity < 0.95:
            findings.append(
                Finding(
                    finding_type="owner_name_variation",
                    severity=Severity.low,
                    explanation=f"Owner names appear similar but not identical ({owners[0]} vs {owners[1]}).",
                    source_references=[e.document_id for e in extractions],
                    recommended_next_step="Manual review required for spelling/initial variations.",
                )
            )

    dates = {e.document_date for e in extractions if e.document_date}
    if len(dates) > 1:
        findings.append(
            Finding(
                finding_type="document_date_inconsistency",
                severity=Severity.low,
                explanation="Document dates are inconsistent and require timeline validation.",
                source_references=[e.document_id for e in extractions],
                recommended_next_step="Check registration and mutation chronology.",
            )
        )

    for e in extractions:
        if e.missing_or_unreadable_fields:
            findings.append(
                Finding(
                    finding_type="missing_required_fields",
                    severity=Severity.medium,
                    explanation=f"{e.document_type} has missing/unreadable fields: {', '.join(e.missing_or_unreadable_fields)}",
                    source_references=[e.document_id],
                    recommended_next_step="Upload a clearer copy or provide the missing details.",
                )
            )

    identifiers = [tuple(sorted(e.document_identifiers)) for e in extractions if e.document_identifiers]
    if len(identifiers) >= 2 and identifiers[0] != identifiers[1]:
        findings.append(
            Finding(
                finding_type="conflicting_property_identifiers",
                severity=Severity.high,
                explanation="Property identifiers conflict across records.",
                source_references=[e.document_id for e in extractions],
                recommended_next_step="Obtain updated RTC/Khata extract and reconcile identifiers.",
            )
        )

    return findings
