from functools import lru_cache
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    app_name: str = "KhataGuard AI API"
    app_env: str = "development"
    demo_mode: bool = True
    cors_origins: str = "http://localhost:3000,http://localhost:8080"
    max_upload_size_bytes: int = 10 * 1024 * 1024
    gemini_api_key: str | None = None
    gemini_model: str = "gemini-1.5-flash"
    use_firestore: bool = False
    firebase_project_id: str | None = None


@lru_cache
def get_settings() -> Settings:
    return Settings()
