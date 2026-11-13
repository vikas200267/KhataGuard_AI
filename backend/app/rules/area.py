from __future__ import annotations

from app.models.schemas import AreaValue

SQ_M_TO_SQ_FT = 10.7639
SQ_YD_TO_SQ_FT = 9.0


def to_sq_ft(area: AreaValue) -> float:
    if area.unit == "sq_ft":
        return area.value
    if area.unit == "sq_m":
        return area.value * SQ_M_TO_SQ_FT
    if area.unit == "sq_yd":
        return area.value * SQ_YD_TO_SQ_FT
    raise ValueError(f"Unsupported area unit: {area.unit}")


def absolute_difference_sq_ft(first: AreaValue, second: AreaValue) -> float:
    return abs(to_sq_ft(first) - to_sq_ft(second))
