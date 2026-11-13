from __future__ import annotations

import json
from pathlib import Path

from app.models.schemas import FeeEstimateResponse


class FeeEstimator:
    def __init__(self) -> None:
        path = Path(__file__).resolve().parents[2] / "app" / "tools" / "fee_rules.v1.json"
        self._rules = json.loads(path.read_text(encoding="utf-8"))["rules"]

    def estimate(self, state: str, transaction_type: str) -> FeeEstimateResponse:
        for rule in self._rules:
            if rule["jurisdiction"] == state and rule["transaction_type"] == transaction_type:
                if not rule.get("verified"):
                    return FeeEstimateResponse(
                        status="unavailable",
                        message="Estimate unavailable — official rate verification required",
                        source_url=rule.get("source_url"),
                    )
                return FeeEstimateResponse(
                    status="ok",
                    message="Indicative estimate",
                    value=rule["flat_amount"],
                    currency="INR",
                    source_url=rule.get("source_url"),
                )
        return FeeEstimateResponse(
            status="unavailable",
            message="Estimate unavailable — official rate verification required",
        )
