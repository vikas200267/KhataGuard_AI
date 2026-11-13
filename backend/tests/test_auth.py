def test_secure_mode_requires_token(secure_client):
    response = secure_client.post("/api/v1/cases", json={"property_name": "Valid Name", "state": "Karnataka"})
    assert response.status_code == 401


def test_secure_mode_accepts_test_token(secure_client):
    response = secure_client.post(
        "/api/v1/cases",
        json={"property_name": "Demo Plot", "state": "Karnataka"},
        headers={"Authorization": "Bearer " + "test-user1"},
    )
    assert response.status_code == 200
    assert response.json()["user_id"] == "user1"
