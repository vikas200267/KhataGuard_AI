from __future__ import annotations

from app.models.schemas import AnalyzeResponse, ExtractionOutput


def build_property_identity_graph(case_id: str, extractions: list[ExtractionOutput], analysis: AnalyzeResponse) -> dict:
    nodes = [{"id": case_id, "type": "property_case"}]
    edges: list[dict] = []

    for extraction in extractions:
        doc_node = {"id": extraction.document_id, "type": "document", "document_type": extraction.document_type}
        nodes.append(doc_node)
        edges.append({"from": case_id, "to": extraction.document_id, "relation": "has_document"})

        if extraction.owner_name:
            owner_id = f"owner:{extraction.owner_name.lower()}"
            nodes.append({"id": owner_id, "type": "owner", "name": extraction.owner_name})
            edges.append({"from": extraction.document_id, "to": owner_id, "relation": "mentions_owner"})

        if extraction.survey_number:
            property_id = f"survey:{extraction.survey_number}"
            nodes.append({"id": property_id, "type": "property_identifier", "value": extraction.survey_number})
            edges.append({"from": extraction.document_id, "to": property_id, "relation": "mentions_property_identifier"})

    for finding in analysis.findings:
        finding_id = f"finding:{finding.finding_type}:{len(nodes)}"
        nodes.append({"id": finding_id, "type": "finding", "severity": finding.severity.value})
        for ref in finding.source_references:
            edges.append({"from": finding_id, "to": ref, "relation": "supported_by"})

    return {"nodes": nodes, "edges": edges}
