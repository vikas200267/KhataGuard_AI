from app.models.schemas import AnalyzeResponse, CaseResponse, ExtractionOutput, FeeEstimateResponse, Finding, PropertyCase, Severity
from app.reports.pdf_report import generate_case_report


def test_pdf_generation():
    case = PropertyCase(
        case_id="c1",
        user_id="u1",
        property_name="Demo",
        state="Karnataka",
        created_at=__import__("datetime").datetime.now(__import__("datetime").timezone.utc),
        documents=[],
    )
    ext = ExtractionOutput(document_id="d1", document_type="Sale", state="Karnataka", language="English")
    analysis = AnalyzeResponse(
        case_id="c1",
        extracted_documents=[ext],
        findings=[
            Finding(
                finding_type="missing_required_fields",
                severity=Severity.low,
                explanation="demo",
                source_references=["d1"],
                recommended_next_step="review",
            )
        ],
    )
    fee = FeeEstimateResponse(status="unavailable", message="Estimate unavailable — official rate verification required")
    content = generate_case_report(case, analysis, fee)
    assert content.startswith(b"%PDF")
