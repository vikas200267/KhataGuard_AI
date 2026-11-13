from __future__ import annotations

import json
from pathlib import Path

from app.models.schemas import RegistryResult

WARNING_TEXT = "SIMULATED REGISTRY DATA — NOT OFFICIAL GOVERNMENT VERIFICATION."


class SyntheticRegistryConnector:
    def __init__(self) -> None:
        path = Path(__file__).resolve().parents[3] / "sample-data" / "synthetic-registry" / "registry.json"
        self._data = json.loads(path.read_text(encoding="utf-8"))

    def lookup(self, survey_number: str) -> RegistryResult:
        matches = [item for item in self._data if item.get("survey_number") == survey_number]
        return RegistryResult(warning=WARNING_TEXT, matches=matches)
