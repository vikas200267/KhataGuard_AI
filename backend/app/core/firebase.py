"""Lazy Firebase Admin initialization using Application Default Credentials."""

from __future__ import annotations

from threading import Lock

from app.core.config import get_settings

_lock = Lock()


def get_firebase_app():
    import firebase_admin

    try:
        return firebase_admin.get_app()
    except ValueError:
        pass

    with _lock:
        try:
            return firebase_admin.get_app()
        except ValueError:
            project_id = get_settings().firebase_project_id
            options = {"projectId": project_id} if project_id else None
            return firebase_admin.initialize_app(options=options)
