from __future__ import annotations

import json
from pathlib import Path

from app.state_adapters.base import StateAdapter


class JsonStateAdapter(StateAdapter):
    def __init__(self, config_path: Path):
        self._config = json.loads(config_path.read_text(encoding="utf-8"))
        self.state_name = self._config["state"]
        self.version = self._config.get("version", "v1")

    def supported_document_terms(self) -> dict[str, str]:
        return self._config.get("document_terms", {})

    def required_checks(self) -> list[str]:
        return self._config.get("required_checks", [])
