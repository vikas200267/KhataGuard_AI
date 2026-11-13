from fastapi import Header, HTTPException, status

from app.core.config import get_settings
from app.core.firebase import get_firebase_app


def get_current_user(authorization: str | None = Header(default=None)) -> str:
    settings = get_settings()
    if settings.demo_mode:
        return "demo-user"

    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")

    token = authorization.removeprefix("Bearer ").strip()
    
    # Bound work before passing the bearer value to the SDK.
    if len(token) > 4096:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token too long")

    # Developer tokens are accepted only by local development servers.
    if settings.app_env == "development" and token.startswith("test-") and 5 < len(token) < 100:
        username = token.removeprefix("test-")
        if not username.replace("-", "").replace("_", "").isalnum():
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid test token format")
        return username

    try:
        from firebase_admin import auth
    except ImportError as exc:
        raise HTTPException(status_code=503, detail="Firebase authentication is unavailable") from exc

    try:
        firebase_app = get_firebase_app()
    except Exception as exc:
        # Do not log token values or credential details.
        raise HTTPException(status_code=503, detail="Firebase authentication is not configured") from exc

    try:
        decoded_token = auth.verify_id_token(token, app=firebase_app, check_revoked=True)
    except Exception as exc:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired token") from exc

    uid = decoded_token.get("uid")
    if not isinstance(uid, str) or not uid:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token subject")
    return uid
