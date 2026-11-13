import pytest
from pydantic import ValidationError

from app.core.config import Settings


def test_production_cannot_start_in_demo_mode():
    with pytest.raises(ValidationError, match="DEMO_MODE must be false"):
        Settings(_env_file=None, app_env="production", demo_mode=True)


def test_firestore_requires_project_id():
    with pytest.raises(ValidationError, match="FIREBASE_PROJECT_ID is required"):
        Settings(_env_file=None, use_firestore=True, firebase_project_id=None)


def test_production_mode_accepts_non_demo_configuration():
    settings = Settings(_env_file=None, app_env="production", demo_mode=False, firebase_project_id="synthetic-test-project")
    assert settings.demo_mode is False
