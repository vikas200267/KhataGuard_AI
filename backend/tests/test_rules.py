from app.models.schemas import AreaValue, ExtractionOutput
from app.rules.area import absolute_difference_sq_ft, to_sq_ft
from app.rules.discrepancy import detect_discrepancies


def test_area_normalization_sq_m():
    converted = to_sq_ft(AreaValue(value=100, unit="sq_m"))
    assert round(converted, 2) == 1076.39


def test_area_difference_120_sq_ft():
    diff = absolute_difference_sq_ft(AreaValue(value=1200, unit="sq_ft"), AreaValue(value=1080, unit="sq_ft"))
    assert diff == 120


def test_discrepancy_detection():
    e1 = ExtractionOutput(
        document_id="d1",
        document_type="Sale Deed",
        state="Karnataka",
        language="English",
        owner_name="Ravi Kumar",
        survey_number="42/3",
        area_value=1200,
        area_unit="sq_ft",
        document_date="2021-01-01",
        document_identifiers=["A"],
    )
    e2 = ExtractionOutput(
        document_id="d2",
        document_type="Khata",
        state="Karnataka",
        language="Kannada",
        owner_name="Ravi K.",
        survey_number="42/3",
        area_value=1080,
        area_unit="sq_ft",
        document_date="2022-01-01",
        document_identifiers=["B"],
        missing_or_unreadable_fields=["boundary_description"],
    )

    findings = detect_discrepancies([e1, e2])
    types = {f.finding_type for f in findings}
    assert "property_area_difference" in types
    assert "owner_name_variation" in types
    assert "missing_required_fields" in types
