from __future__ import annotations

from datetime import datetime
from enum import Enum
from typing import Any
from pydantic import BaseModel, Field, field_validator


class Severity(str, Enum):
    low = "low"
    medium = "medium"
    high = "high"


class SupportedState(str, Enum):
    karnataka = "Karnataka"
    maharashtra = "Maharashtra"
    tamil_nadu = "Tamil Nadu"


class CaseCreateRequest(BaseModel):
    property_name: str = Field(min_length=2, max_length=200)
    state: SupportedState


class CaseResponse(BaseModel):
    case_id: str
    user_id: str
    property_name: str
    state: SupportedState
    created_at: datetime


class DocumentType(BaseModel):
    code: str
    label: str


class PropertyCase(BaseModel):
    case_id: str
    user_id: str
    property_name: str
    state: str
    created_at: datetime
    documents: list[DocumentRecord] = Field(default_factory=list)


class DocumentRecord(BaseModel):
    document_id: str
    filename: str
    document_type: str
    content_type: str
    size_bytes: int
    source_uri: str
    uploaded_at: datetime


class ExtractedField(BaseModel):
    key: str
    value: str | None
    missing: bool = False


class ExtractionOutput(BaseModel):
    document_id: str
    document_type: str
    state: str
    language: str
    owner_name: str | None = None
    survey_number: str | None = None
    area_value: float | None = None
    area_unit: str | None = None
    location: str | None = None
    document_date: str | None = None
    document_identifiers: list[str] = Field(default_factory=list)
    boundary_description: str | None = None
    missing_or_unreadable_fields: list[str] = Field(default_factory=list)
    source_references: list[dict[str, Any]] = Field(default_factory=list)


class Finding(BaseModel):
    finding_type: str
    severity: Severity
    explanation: str
    source_references: list[str]
    recommended_next_step: str


class AnalyzeResponse(BaseModel):
    case_id: str
    findings: list[Finding]
    extracted_documents: list[ExtractionOutput]


class FeeEstimateResponse(BaseModel):
    status: str
    message: str
    value: float | None = None
    currency: str | None = None
    source_url: str | None = None


class ReportSummary(BaseModel):
    case_id: str
    disclaimer: str
    findings_count: int


class ErrorResponse(BaseModel):
    detail: str


class AnalyzeRequest(BaseModel):
    transaction_type: str = "sale"


class RegistryResult(BaseModel):
    warning: str
    matches: list[dict[str, str]]


class AreaValue(BaseModel):
    value: float
    unit: str

    @field_validator("unit")
    @classmethod
    def valid_unit(cls, v: str) -> str:
        allowed = {"sq_ft", "sq_m", "sq_yd"}
        if v not in allowed:
            raise ValueError(f"Unsupported area unit: {v}")
        return v
