import pytest
from app.agents.normalization_tool import (
    normalize_person_name,
    normalize_date_str,
    normalize_identifier,
    normalize_extraction,
)
from app.agents.orchestrator import run_agent_workflow
from app.models.schemas import ExtractionOutput
from app.state_adapters.selector import get_state_adapter
from tests.conftest import create_case, upload_doc


def test_normalization_person_name():
    assert normalize_person_name("Sri Ravi Kumar") == "Ravi Kumar"
    assert normalize_person_name("Smt. Lakshmi Devi") == "Lakshmi Devi"
    assert normalize_person_name("Late Dr. Ramesh Rao") == "Ramesh Rao"
    assert normalize_person_name("  anand  shetty  ") == "Anand Shetty"


def test_normalization_dates():
    assert normalize_date_str("15/08/2021") == "2021-08-15"
    assert normalize_date_str("15-08-2021") == "2021-08-15"
    assert normalize_date_str("2021-08-15") == "2021-08-15"


def test_normalization_identifier():
    assert normalize_identifier("  42 / 3  ") == "42/3"
    assert normalize_identifier("sd-2021-9482") == "SD-2021-9482"


def test_normalization_extraction_area_conversion():
    raw = ExtractionOutput(
        document_id="doc-1",
        document_type="Sale Deed",
        state="Karnataka",
        language="English",
        owner_name="Sri Ravi Kumar",
        survey_number=" 42/3 ",
        area_value=100.0,
        area_unit="sq_m",
        document_date="15/08/2021",
    )
    norm = normalize_extraction(raw)
    assert norm.owner_name == "Ravi Kumar"
    assert norm.survey_number == "42/3"
    assert norm.document_date == "2021-08-15"
    assert norm.area_unit == "sq_ft"
    assert round(norm.area_value, 1) == 1076.4


def test_state_adapters_all_three():
    ka = get_state_adapter("Karnataka")
    assert ka.state_name == "Karnataka"
    assert len(ka.required_checks()) >= 4

    mh = get_state_adapter("Maharashtra")
    assert mh.state_name == "Maharashtra"
    assert "7/12 Extract" in str(mh.supported_document_terms())

    tn = get_state_adapter("Tamil Nadu")
    assert tn.state_name == "Tamil Nadu"
    assert "Patta" in str(mh.supported_document_terms()) or "Patta" in str(tn.supported_document_terms())


def test_list_cases_and_graph_endpoint(client):
    case_id_1 = create_case(client)
    case_id_2 = create_case(client)

    # Test list cases
    cases_resp = client.get("/api/v1/cases")
    assert cases_resp.status_code == 200
    case_ids = [c["case_id"] for c in cases_resp.json()]
    assert case_id_1 in case_ids
    assert case_id_2 in case_ids

    # Upload docs and analyze case 1
    upload_doc(client, case_id_1, "sale_deed.pdf")
    upload_doc(client, case_id_1, "khata_record.pdf")
    analyze_resp = client.post(f"/api/v1/cases/{case_id_1}/analyze", json={"transaction_type": "sale"})
    assert analyze_resp.status_code == 200

    # Test graph endpoint
    graph_resp = client.get(f"/api/v1/cases/{case_id_1}/graph")
    assert graph_resp.status_code == 200
    graph_data = graph_resp.json()
    assert "nodes" in graph_data
    assert "edges" in graph_data
    node_types = {n["type"] for n in graph_data["nodes"]}
    assert "property_case" in node_types
    assert "document" in node_types
    assert "finding" in node_types


def test_analyze_insufficient_documents(client):
    case_id = create_case(client)
    upload_doc(client, case_id, "only_one_doc.pdf")
    # Trying to analyze with only 1 document should fail with 400
    resp = client.post(f"/api/v1/cases/{case_id}/analyze", json={"transaction_type": "sale"})
    assert resp.status_code == 400
    assert "at least two documents" in resp.json()["detail"].lower()
