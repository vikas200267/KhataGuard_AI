from types import SimpleNamespace
from unittest.mock import Mock

import pytest
from fastapi import HTTPException

from app.core import security


def test_firebase_id_token_resolves_uid(monkeypatch):
    verify = Mock(return_value={"uid": "firebase-user-1"})
    monkeypatch.setattr(security, "get_settings", lambda: SimpleNamespace(demo_mode=False, app_env="production"))
    monkeypatch.setattr(security, "get_firebase_app", lambda: "firebase-app")
    monkeypatch.setitem(__import__("sys").modules, "firebase_admin", SimpleNamespace(auth=SimpleNamespace(verify_id_token=verify)))

    assert security.get_current_user("Bearer signed-client-id-token") == "firebase-user-1"
    verify.assert_called_once_with("signed-client-id-token", app="firebase-app", check_revoked=True)


def test_development_token_is_not_a_production_bypass(monkeypatch):
    verify = Mock(side_effect=ValueError("invalid token"))
    monkeypatch.setattr(security, "get_settings", lambda: SimpleNamespace(demo_mode=False, app_env="production"))
    monkeypatch.setattr(security, "get_firebase_app", lambda: "firebase-app")
    monkeypatch.setitem(__import__("sys").modules, "firebase_admin", SimpleNamespace(auth=SimpleNamespace(verify_id_token=verify)))

    with pytest.raises(HTTPException) as exc:
        security.get_current_user("Bearer test-user")
    assert exc.value.status_code == 401
    verify.assert_called_once()


def test_auth_returns_service_unavailable_without_firebase_sdk(monkeypatch):
    monkeypatch.setattr(security, "get_settings", lambda: SimpleNamespace(demo_mode=False, app_env="production"))
    monkeypatch.delitem(__import__("sys").modules, "firebase_admin", raising=False)
    monkeypatch.setattr("builtins.__import__", lambda name, *args, **kwargs: (_ for _ in ()).throw(ImportError()) if name == "firebase_admin" else original_import(name, *args, **kwargs))

    original_import = __import__
    with pytest.raises(HTTPException) as exc:
        security.get_current_user("Bearer signed-client-id-token")
    assert exc.value.status_code == 503
