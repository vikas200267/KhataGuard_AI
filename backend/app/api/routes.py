from __future__ import annotations

from datetime import datetime, timezone
from pathlib import Path
from uuid import uuid4

from fastapi import APIRouter, Depends, File, HTTPException, UploadFile
from fastapi.responses import Response

from app.agents.orchestrator import run_agent_workflow
from app.core.config import get_settings
from app.core.security import get_current_user
from app.models.schemas import (
    AnalyzeRequest,
    AnalyzeResponse,
    CaseCreateRequest,
    CaseResponse,
    DocumentRecord,
    DocumentType,
    PropertyCase,
    SupportedState,
)
from app.reports.pdf_report import generate_case_report
from app.repositories.base import CaseRepository
from app.repositories.factory import get_case_repository
from app.services.extraction import get_extraction_provider
from app.services.fee_estimator import FeeEstimator
from app.services.identity_graph import build_property_identity_graph
from app.services.registry import SyntheticRegistryConnector
from app.state_adapters.selector import get_state_adapter

router = APIRouter(prefix="/api/v1", tags=["api"])

UPLOAD_DIR = Path("/tmp/khataguard_uploads")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

DOCUMENT_TYPES = [
    DocumentType(code="sale_deed", label="Sale Deed"),
    DocumentType(code="khata_record", label="Khata Record"),
    DocumentType(code="tax_receipt", label="Tax Receipt"),
]


@router.get("/states", response_model=list[SupportedState])
def list_states() -> list[SupportedState]:
    return [SupportedState.karnataka, SupportedState.maharashtra, SupportedState.tamil_nadu]


@router.get("/document-types", response_model=list[DocumentType])
def list_document_types() -> list[DocumentType]:
    return DOCUMENT_TYPES


@router.post("/cases", response_model=CaseResponse)
def create_case(
    payload: CaseCreateRequest,
    user_id: str = Depends(get_current_user),
    repo: CaseRepository = Depends(get_case_repository),
) -> CaseResponse:
    case = PropertyCase(
        case_id=str(uuid4()),
        user_id=user_id,
        property_name=payload.property_name,
        state=payload.state.value,
        created_at=datetime.now(timezone.utc),
        documents=[],
    )
    repo.create_case(case)
    return CaseResponse(
        case_id=case.case_id,
        user_id=case.user_id,
        property_name=case.property_name,
        state=payload.state,
        created_at=case.created_at,
    )


@router.get("/cases/{case_id}", response_model=PropertyCase)
def get_case(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> PropertyCase:
    case = repo.get_case(case_id, user_id)
    if not case:
        raise HTTPException(status_code=404, detail="Case not found")
    return case


@router.post("/cases/{case_id}/documents", response_model=DocumentRecord)
async def upload_document(
    case_id: str,
    file: UploadFile = File(...),
    user_id: str = Depends(get_current_user),
    repo: CaseRepository = Depends(get_case_repository),
) -> DocumentRecord:
    settings = get_settings()
    case = repo.get_case(case_id, user_id)
    if not case:
        raise HTTPException(status_code=404, detail="Case not found")

    content = await file.read()
    if len(content) > settings.max_upload_size_bytes:
        raise HTTPException(status_code=400, detail="File exceeds size limit")

    allowed = {"application/pdf", "image/jpeg", "image/png"}
    if file.content_type not in allowed:
        raise HTTPException(status_code=400, detail="Unsupported file type")

    document_id = str(uuid4())
    destination = UPLOAD_DIR / f"{document_id}-{file.filename}"
    destination.write_bytes(content)

    doc = DocumentRecord(
        document_id=document_id,
        filename=file.filename,
        document_type="unknown",
        content_type=file.content_type or "application/octet-stream",
        size_bytes=len(content),
        source_uri=str(destination),
        uploaded_at=datetime.now(timezone.utc),
    )
    case.documents.append(doc)
    repo.add_document(case_id, user_id, document_id)
    return doc


@router.post("/cases/{case_id}/analyze", response_model=AnalyzeResponse)
def analyze_case(
    case_id: str,
    payload: AnalyzeRequest,
    user_id: str = Depends(get_current_user),
    repo: CaseRepository = Depends(get_case_repository),
) -> AnalyzeResponse:
    case = repo.get_case(case_id, user_id)
    if not case:
        raise HTTPException(status_code=404, detail="Case not found")

    if len(case.documents) < 2:
        raise HTTPException(status_code=400, detail="Upload at least two documents before analysis")

    extractor = get_extraction_provider()
    extractions = []
    for document in case.documents:
        extraction = extractor.extract(document)
        repo.save_extraction(case.case_id, user_id, extraction)
        extractions.append(extraction)

    adapter = get_state_adapter(case.state)
    _ = adapter.required_checks()

    analysis = run_agent_workflow(case.case_id, extractions)
    repo.save_analysis(case.case_id, user_id, analysis)

    # Simulated registry lookup extension point
    registry = SyntheticRegistryConnector()
    if extractions and extractions[0].survey_number:
        registry.lookup(extractions[0].survey_number)

    return analysis


@router.get("/cases/{case_id}/findings", response_model=AnalyzeResponse)
def get_findings(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> AnalyzeResponse:
    analysis = repo.get_analysis(case_id, user_id)
    if not analysis:
        raise HTTPException(status_code=404, detail="Analysis not found")
    return analysis


@router.get("/cases/{case_id}/report")
def get_report(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> Response:
    case = repo.get_case(case_id, user_id)
    analysis = repo.get_analysis(case_id, user_id)
    if not case or not analysis:
        raise HTTPException(status_code=404, detail="Case report not found")

    fee = FeeEstimator().estimate(case.state, "sale")
    _graph = build_property_identity_graph(case.case_id, analysis.extracted_documents, analysis)

    pdf_bytes = generate_case_report(case, analysis, fee)
    return Response(content=pdf_bytes, media_type="application/pdf")


@router.delete("/cases/{case_id}")
def delete_case(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> dict[str, str]:
    deleted = repo.delete_case(case_id, user_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Case not found")
    return {"status": "deleted"}
