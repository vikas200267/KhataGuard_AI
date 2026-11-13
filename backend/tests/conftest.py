import os

import pytest
from fastapi.testclient import TestClient


@pytest.fixture(autouse=True)
def reset_singletons():
    from app.core.config import get_settings
    from app.repositories.factory import get_case_repository

    get_settings.cache_clear()
    get_case_repository.cache_clear()
    yield
    get_settings.cache_clear()
    get_case_repository.cache_clear()


@pytest.fixture
def client(monkeypatch):
    monkeypatch.setenv("DEMO_MODE", "true")
    from app.main import app

    with TestClient(app) as test_client:
        yield test_client


@pytest.fixture
def secure_client(monkeypatch):
    monkeypatch.setenv("DEMO_MODE", "false")
    from app.main import app

    with TestClient(app) as test_client:
        yield test_client


def create_case(client: TestClient) -> str:
    response = client.post("/api/v1/cases", json={"property_name": "Demo Property", "state": "Karnataka"})
    assert response.status_code == 200
    return response.json()["case_id"]


def upload_doc(client: TestClient, case_id: str, filename: str):
    files = {"file": (filename, b"fake-pdf", "application/pdf")}
    response = client.post(f"/api/v1/cases/{case_id}/documents", files=files)
    assert response.status_code == 200
