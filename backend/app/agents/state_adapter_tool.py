from __future__ import annotations

from typing import Any
from app.state_adapters.base import StateAdapter
from app.state_adapters.selector import get_state_adapter


def get_state_rules(state: str) -> dict[str, Any]:
    """Retrieves document terminology and required checks for the specified state."""
    adapter: StateAdapter = get_state_adapter(state)
    return {
        "state_name": adapter.state_name,
        "version": adapter.version,
        "terms": adapter.supported_document_terms(),
        "required_checks": adapter.required_checks(),
    }
