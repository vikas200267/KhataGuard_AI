from __future__ import annotations

import logging
from app.models.schemas import AnalyzeResponse, ExtractionOutput
from app.agents.normalization_tool import normalize_extraction
from app.agents.state_adapter_tool import get_state_rules
from app.agents.consistency_tool import run_consistency_checks
from app.agents.report_agent import explain_findings

logger = logging.getLogger(__name__)


def run_agent_workflow(
    case_id: str,
    extractions: list[ExtractionOutput],
    state: str = "Karnataka",
) -> AnalyzeResponse:
    """Executes the KhataGuard multi-agent pipeline:
    
    1. Normalization Agent: Standardizes names, dates, units, and identifiers.
    2. State Adapter Tool: Ingests jurisdiction-specific rule configurations.
    3. Consistency Checking Tool: Deterministically compares property facts.
    4. Report Explanation Agent: Adds clear, evidence-linked consumer explanations.
    """
    logger.info(f"Starting agent workflow for case {case_id} ({len(extractions)} docs, state={state})")

    # Step 1: Normalization Tool
    normalized_extractions = [normalize_extraction(e) for e in extractions]

    # Step 2: State Adapter Tool
    state_rules = get_state_rules(state)
    required_checks = state_rules.get("required_checks", [])

    # Step 3: Consistency Checking Tool
    raw_findings = run_consistency_checks(normalized_extractions, required_checks=required_checks)

    # Step 4: Report Explanation Agent
    explained_findings = explain_findings(raw_findings)

    logger.info(f"Agent workflow complete for case {case_id}: {len(explained_findings)} findings identified.")

    return AnalyzeResponse(
        case_id=case_id,
        findings=explained_findings,
        extracted_documents=normalized_extractions,
    )
