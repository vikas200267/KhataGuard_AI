from app.services.registry import WARNING_TEXT

from tests.conftest import create_case, upload_doc


def test_health(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_states_and_document_types(client):
    states = client.get("/api/v1/states")
    assert states.status_code == 200
    assert "Karnataka" in states.text

    doc_types = client.get("/api/v1/document-types")
    assert doc_types.status_code == 200
    assert len(doc_types.json()) >= 2


def test_demo_analysis_and_findings(client):
    case_id = create_case(client)
    upload_doc(client, case_id, "sale_deed.pdf")
    upload_doc(client, case_id, "khata_record.pdf")

    analysis = client.post(f"/api/v1/cases/{case_id}/analyze", json={"transaction_type": "sale"})
    assert analysis.status_code == 200
    data = analysis.json()
    assert data["case_id"] == case_id
    assert any(f["finding_type"] == "property_area_difference" for f in data["findings"])
    assert any("120 sq. ft" in f["explanation"] for f in data["findings"])

    findings = client.get(f"/api/v1/cases/{case_id}/findings")
    assert findings.status_code == 200

    report = client.get(f"/api/v1/cases/{case_id}/report")
    assert report.status_code == 200
    assert report.headers["content-type"].startswith("application/pdf")

    # ensure warning constant remains exact
    assert WARNING_TEXT == "SIMULATED REGISTRY DATA — NOT OFFICIAL GOVERNMENT VERIFICATION."


def test_case_deletion(client):
    case_id = create_case(client)
    document = upload_doc(client, case_id, "private.pdf").json()
    from pathlib import Path
    stored_path = Path(document["source_uri"])
    assert stored_path.exists()
    deleted = client.delete(f"/api/v1/cases/{case_id}")
    assert deleted.status_code == 200
    assert not stored_path.exists()

    missing = client.get(f"/api/v1/cases/{case_id}")
    assert missing.status_code == 404
