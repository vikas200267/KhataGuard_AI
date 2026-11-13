from __future__ import annotations

from app.models.schemas import Finding, Severity


EXPLANATION_TEMPLATES = {
    "property_area_difference": (
        "A variance between the deeded land extent and municipal khata record is common due to "
        "road widening deductions, setback requirements, or measurement conversions (e.g. guntas to sq ft). "
        "However, unexplained differences could indicate boundary encroachment or unapproved layout deviations."
    ),
    "survey_number_mismatch": (
        "Survey number mismatches can arise from subdivision (hissa), podi consolidation, or clerical errors. "
        "Unreconciled survey numbers prevent clear mutation in revenue databases."
    ),
    "owner_name_variation": (
        "Spelling or initial differences in property titles often occur due to transliteration between local scripts "
        "and English. An affidavit or rectification deed may be required to clear title during resale or mortgage."
    ),
    "missing_owner": (
        "One or more records lack primary owner identification, preventing end-to-end chain of custody validation."
    ),
    "missing_required_fields": (
        "Key statutory boundaries or registration details are absent. Boundary descriptions (North, South, East, West) "
        "are essential under the Registration Act to uniquely establish physical property identity."
    ),
    "conflicting_property_identifiers": (
        "Conflicting property schedule numbers (e.g., PID vs Assessment number vs Khata number) indicate potential "
        "record-keeping divergence between local municipality and revenue department."
    ),
    "document_date_inconsistency": (
        "Document timeline sequence does not match expected execution and registration flow."
    ),
}


def explain_findings(findings: list[Finding]) -> list[Finding]:
    """Enriches findings with contextual risk explanations and official verification pathways."""
    enriched: list[Finding] = []
    for f in findings:
        context = EXPLANATION_TEMPLATES.get(f.finding_type)
        if context and context not in f.explanation:
            detailed_explanation = f"{f.explanation} Context: {context}"
        else:
            detailed_explanation = f.explanation

        enriched.append(
            f.model_copy(
                update={
                    "explanation": detailed_explanation,
                }
            )
        )
    return enriched
