def test_case_validation_error(client):
    response = client.post("/api/v1/cases", json={"property_name": "A", "state": "Karnataka"})
    assert response.status_code == 422


def test_upload_file_type_validation(client):
    case = client.post("/api/v1/cases", json={"property_name": "Demo Property", "state": "Karnataka"})
    case_id = case.json()["case_id"]

    bad = client.post(
        f"/api/v1/cases/{case_id}/documents",
        files={"file": ("notes.txt", b"hello", "text/plain")},
    )
    assert bad.status_code == 400
