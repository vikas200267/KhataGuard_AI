from __future__ import annotations

from abc import ABC, abstractmethod


class StateAdapter(ABC):
    state_name: str
    version: str

    @abstractmethod
    def supported_document_terms(self) -> dict[str, str]:
        raise NotImplementedError

    @abstractmethod
    def required_checks(self) -> list[str]:
        raise NotImplementedError
