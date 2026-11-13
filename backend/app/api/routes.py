from __future__ import annotations

from datetime import datetime, timezone
from pathlib import Path
from tempfile import gettempdir
from uuid import uuid4, UUID

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

# Use system temp directory (cross-platform)
UPLOAD_DIR = Path(gettempdir()) / "khataguard_uploads"
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
    # Validate case_id format (should be UUID)
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")
    
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

    # Validate filename to prevent path traversal
    if not file.filename:
        raise HTTPException(status_code=400, detail="Filename is required")
    
    # Sanitize filename - remove path components and dangerous characters
    safe_filename = Path(file.filename).name
    if not safe_filename or safe_filename.startswith(".") or "/" in safe_filename or "\\" in safe_filename:
        raise HTTPException(status_code=400, detail="Invalid filename")
    
    # Limit filename length
    if len(safe_filename) > 255:
        raise HTTPException(status_code=400, detail="Filename too long")

    # Read at most one byte beyond the limit so an oversized request is rejected
    # without buffering an unbounded upload in memory.
    content = await file.read(settings.max_upload_size_bytes + 1)
    if len(content) > settings.max_upload_size_bytes:
        raise HTTPException(status_code=400, detail="File exceeds size limit")
    
    # Require minimum file size (prevent empty file uploads)
    if len(content) < 10:
        raise HTTPException(status_code=400, detail="File is too small or empty")

    allowed = {"application/pdf", "image/jpeg", "image/png"}
    if file.content_type not in allowed:
        raise HTTPException(status_code=400, detail="Unsupported file type")
    
    # Verify actual file content matches declared content type (magic byte validation)
    if len(content) >= 4:
        # PDF signature
        if file.content_type == "application/pdf" and not content.startswith(b"%PDF"):
            raise HTTPException(status_code=400, detail="File content does not match PDF format")
        # JPEG signature
        elif file.content_type == "image/jpeg" and not content.startswith(b"\xff\xd8\xff"):
            raise HTTPException(status_code=400, detail="File content does not match JPEG format")
        # PNG signature
        elif file.content_type == "image/png" and not content.startswith(b"\x89PNG\r\n\x1a\n"):
            raise HTTPException(status_code=400, detail="File content does not match PNG format")

    document_id = str(uuid4())
    # Use only document_id for filename to prevent any path traversal
    file_extension = Path(safe_filename).suffix.lower()
    if file_extension not in {".pdf", ".jpg", ".jpeg", ".png"}:
        file_extension = ".bin"
    
    safe_stored_filename = f"{document_id}{file_extension}"
    destination = UPLOAD_DIR / safe_stored_filename
    
    # Verify destination is within UPLOAD_DIR (prevent symlink attacks)
    try:
        destination.resolve().relative_to(UPLOAD_DIR.resolve())
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid file path")
    
    destination.write_bytes(content)

    doc = DocumentRecord(
        document_id=document_id,
        filename=safe_filename,  # Store sanitized filename for display
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
    # Validate case_id format
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")
    
    case = repo.get_case(case_id, user_id)
    if not case:
        raise HTTPException(status_code=404, detail="Case not found")

    if len(case.documents) < 2:
        raise HTTPException(status_code=400, detail="Upload at least two documents before analysis")
    
    # Limit maximum documents to prevent DoS
    if len(case.documents) > 10:
        raise HTTPException(status_code=400, detail="Maximum 10 documents allowed per case")

    extractor = get_extraction_provider()
    extractions = []
    for document in case.documents:
        extraction = extractor.extract(document)
        repo.save_extraction(case.case_id, user_id, extraction)
        extractions.append(extraction)

    adapter = get_state_adapter(case.state)
    _ = adapter.required_checks()

    analysis = run_agent_workflow(case.case_id, extractions, state=case.state)
    repo.save_analysis(case.case_id, user_id, analysis)

    # Simulated registry lookup extension point
    registry = SyntheticRegistryConnector()
    if extractions and extractions[0].survey_number:
        # Validate survey number format (basic sanitization)
        survey_num = extractions[0].survey_number
        if len(survey_num) > 50 or not survey_num.replace("/", "").replace("-", "").replace(" ", "").isalnum():
            pass  # Skip invalid survey numbers silently
        else:
            registry.lookup(survey_num)

    return analysis


@router.get("/cases/{case_id}/findings", response_model=AnalyzeResponse)
def get_findings(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> AnalyzeResponse:
    # Validate case_id format
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")
    
    analysis = repo.get_analysis(case_id, user_id)
    if not analysis:
        raise HTTPException(status_code=404, detail="Analysis not found")
    return analysis


@router.get("/cases/{case_id}/report")
def get_report(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> Response:
    # Validate case_id format
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")
    
    case = repo.get_case(case_id, user_id)
    analysis = repo.get_analysis(case_id, user_id)
    if not case or not analysis:
        raise HTTPException(status_code=404, detail="Case report not found")

    fee = FeeEstimator().estimate(case.state, "sale")
    _graph = build_property_identity_graph(case.case_id, analysis.extracted_documents, analysis)

    pdf_bytes = generate_case_report(case, analysis, fee)
    return Response(content=pdf_bytes, media_type="application/pdf")


@router.get("/cases", response_model=list[CaseResponse])
def list_cases(
    user_id: str = Depends(get_current_user),
    repo: CaseRepository = Depends(get_case_repository),
) -> list[CaseResponse]:
    cases = repo.list_cases(user_id)
    return [
        CaseResponse(
            case_id=c.case_id,
            user_id=c.user_id,
            property_name=c.property_name,
            state=SupportedState(c.state),
            created_at=c.created_at,
        )
        for c in cases
    ]


@router.get("/cases/{case_id}/graph")
def get_graph(
    case_id: str,
    user_id: str = Depends(get_current_user),
    repo: CaseRepository = Depends(get_case_repository),
) -> dict:
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")

    case = repo.get_case(case_id, user_id)
    analysis = repo.get_analysis(case_id, user_id)
    if not case or not analysis:
        raise HTTPException(status_code=404, detail="Case or analysis not found")

    return build_property_identity_graph(case.case_id, analysis.extracted_documents, analysis)


@router.delete("/cases/{case_id}")
def delete_case(case_id: str, user_id: str = Depends(get_current_user), repo: CaseRepository = Depends(get_case_repository)) -> dict[str, str]:
    # Validate case_id format
    try:
        UUID(case_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid case ID format")
    
    case = repo.get_case(case_id, user_id)
    if not case:
        raise HTTPException(status_code=404, detail="Case not found")
    deleted = repo.delete_case(case_id, user_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Case not found")
    for document in case.documents:
        try:
            Path(document.source_uri).unlink(missing_ok=True)
        except OSError:
            # Deletion of the case record must not be undone by a stale temp file.
            pass
    return {"status": "deleted"}
