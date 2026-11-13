from fastapi import Header, HTTPException, status

from app.core.config import get_settings


def get_current_user(authorization: str | None = Header(default=None)) -> str:
    settings = get_settings()
    if settings.demo_mode:
        return "demo-user"

    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")

    token = authorization.removeprefix("Bearer ").strip()

    # Production deployments should validate Firebase ID tokens.
    # For local secured tests we accept deterministic token format.
    if token.startswith("test-") and len(token) > 5:
        return token.removeprefix("test-")

    raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
