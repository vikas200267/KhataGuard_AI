from functools import lru_cache
from pydantic import model_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    app_name: str = "KhataGuard AI API"
    app_env: str = "development"
    demo_mode: bool = True
    cors_origins: str = "http://localhost:3000,http://localhost:8080"
    max_upload_size_bytes: int = 10 * 1024 * 1024  # 10MB
    gemini_api_key: str | None = None
    gemini_model: str = "gemini-3.6-flash"
    use_firestore: bool = False
    firebase_project_id: str | None = None
    
    # Rate limiting (requests per minute per user)
    rate_limit_per_minute: int = 60
    
    # Maximum property name length
    max_property_name_length: int = 200

    @model_validator(mode="after")
    def validate_deployment_mode(self) -> "Settings":
        if self.app_env.lower() == "production" and self.demo_mode:
            raise ValueError("DEMO_MODE must be false when APP_ENV=production")
        if self.use_firestore and not self.firebase_project_id:
            raise ValueError("FIREBASE_PROJECT_ID is required when USE_FIRESTORE=true")
        return self


@lru_cache
def get_settings() -> Settings:
    return Settings()
