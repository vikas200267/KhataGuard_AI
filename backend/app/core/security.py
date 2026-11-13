from fastapi import Header, HTTPException, status

from app.core.config import get_settings


def get_current_user(authorization: str | None = Header(default=None)) -> str:
    settings = get_settings()
    if settings.demo_mode:
        return "demo-user"

    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")

    token = authorization.removeprefix("Bearer ").strip()
    
    # Validate token length to prevent DoS
    if len(token) > 4096:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token too long")

    # Production deployments should validate Firebase ID tokens using firebase-admin SDK.
    # For local secured tests we accept deterministic token format.
    if token.startswith("test-") and len(token) > 5 and len(token) < 100:
        # Additional validation: only allow alphanumeric and dash
        username = token.removeprefix("test-")
        if not username.replace("-", "").replace("_", "").isalnum():
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid test token format")
        return username

    # TODO: Implement Firebase ID token validation here for production:
    # from firebase_admin import auth
    # try:
    #     decoded_token = auth.verify_id_token(token)
    #     return decoded_token['uid']
    # except Exception:
    #     raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")

    raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
