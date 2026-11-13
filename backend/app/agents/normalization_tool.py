from __future__ import annotations

import re
from datetime import datetime
from app.models.schemas import AreaValue, ExtractionOutput
from app.rules.area import to_sq_ft


HONORIFICS = {"mr.", "mr", "mrs.", "mrs", "ms.", "ms", "sri", "sree", "smt", "smt.", "dr.", "dr", "late"}


def normalize_person_name(name: str | None) -> str | None:
    """Normalizes person names by trimming, removing honorifics, and normalizing whitespace."""
    if not name:
        return None
    cleaned = re.sub(r"[^\w\s\.]", "", name.strip())
    tokens = cleaned.split()
    filtered = [t for t in tokens if t.lower().rstrip(".") not in HONORIFICS]
    normalized = " ".join(filtered) if filtered else cleaned
    return normalized.title()


def normalize_date_str(date_str: str | None) -> str | None:
    """Normalizes Indian and standard date formats into ISO YYYY-MM-DD."""
    if not date_str:
        return None
    date_str = date_str.strip()
    for fmt in ("%Y-%m-%d", "%d/%m/%Y", "%d-%m-%Y", "%d.%m.%Y", "%Y/%m/%d", "%d %b %Y", "%d %B %Y"):
        try:
            dt = datetime.strptime(date_str, fmt)
            return dt.strftime("%Y-%m-%d")
        except ValueError:
            continue
    return date_str


def normalize_identifier(identifier: str | None) -> str | None:
    """Normalizes property identifiers like survey numbers or deed numbers."""
    if not identifier:
        return None
    return identifier.strip().upper().replace(" ", "")


def normalize_extraction(extraction: ExtractionOutput) -> ExtractionOutput:
    """Normalizes all fields within an extraction output."""
    norm_owner = normalize_person_name(extraction.owner_name)
    norm_date = normalize_date_str(extraction.document_date)
    norm_survey = normalize_identifier(extraction.survey_number)
    norm_identifiers = [normalize_identifier(i) for i in extraction.document_identifiers if i]

    # Normalize area to sq_ft if possible
    area_val = extraction.area_value
    area_unit = extraction.area_unit
    if area_val is not None and area_unit and area_unit != "sq_ft":
        try:
            area_val = round(to_sq_ft(AreaValue(value=area_val, unit=area_unit)), 2)
            area_unit = "sq_ft"
        except Exception:
            pass

    return extraction.model_copy(
        update={
            "owner_name": norm_owner,
            "document_date": norm_date,
            "survey_number": norm_survey,
            "document_identifiers": [i for i in norm_identifiers if i],
            "area_value": area_val,
            "area_unit": area_unit,
        }
    )
