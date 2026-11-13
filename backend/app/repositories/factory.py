from functools import lru_cache

from app.core.config import get_settings
from app.repositories.base import CaseRepository
from app.repositories.firestore_repo import FirestoreCaseRepository
from app.repositories.local_repo import LocalCaseRepository


@lru_cache
def get_case_repository() -> CaseRepository:
    settings = get_settings()
    if settings.use_firestore:
        return FirestoreCaseRepository()
    return LocalCaseRepository()
