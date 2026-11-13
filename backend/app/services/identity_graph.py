from __future__ import annotations

from app.models.schemas import AnalyzeResponse, ExtractionOutput


def build_property_identity_graph(case_id: str, extractions: list[ExtractionOutput], analysis: AnalyzeResponse) -> dict:
    nodes = [{"id": case_id, "type": "property_case", "label": "Property Case", "metadata": {"case_id": case_id}}]
    edges: list[dict] = []
    seen_nodes: set[str] = {case_id}

    for extraction in extractions:
        doc_id = extraction.document_id
        if doc_id not in seen_nodes:
            nodes.append({
                "id": doc_id,
                "type": "document",
                "label": extraction.document_type or "Document",
                "document_type": extraction.document_type,
                "metadata": {
                    "language": extraction.language,
                    "state": extraction.state,
                }
            })
            seen_nodes.add(doc_id)
        edges.append({"from": case_id, "to": doc_id, "relation": "has_document"})

        if extraction.owner_name:
            owner_id = f"owner:{extraction.owner_name.strip().lower()}"
            if owner_id not in seen_nodes:
                nodes.append({
                    "id": owner_id,
                    "type": "owner",
                    "name": extraction.owner_name,
                    "label": f"Owner: {extraction.owner_name}",
                    "metadata": {"name": extraction.owner_name}
                })
                seen_nodes.add(owner_id)
            edges.append({"from": doc_id, "to": owner_id, "relation": "mentions_owner"})

        if extraction.survey_number:
            property_id = f"survey:{extraction.survey_number.strip()}"
            if property_id not in seen_nodes:
                nodes.append({
                    "id": property_id,
                    "type": "property_identifier",
                    "value": extraction.survey_number,
                    "label": f"Survey #{extraction.survey_number}",
                    "metadata": {"survey_number": extraction.survey_number}
                })
                seen_nodes.add(property_id)
            edges.append({"from": doc_id, "to": property_id, "relation": "mentions_property_identifier"})

        if extraction.area_value is not None and extraction.area_unit:
            area_id = f"area:{doc_id}:{extraction.area_value}_{extraction.area_unit}"
            if area_id not in seen_nodes:
                nodes.append({
                    "id": area_id,
                    "type": "area",
                    "value": f"{extraction.area_value} {extraction.area_unit}",
                    "label": f"Area: {extraction.area_value} {extraction.area_unit}",
                    "metadata": {"value": extraction.area_value, "unit": extraction.area_unit}
                })
                seen_nodes.add(area_id)
            edges.append({"from": doc_id, "to": area_id, "relation": "specifies_area"})

        if extraction.document_date:
            date_id = f"date:{extraction.document_date}"
            if date_id not in seen_nodes:
                nodes.append({
                    "id": date_id,
                    "type": "date",
                    "value": extraction.document_date,
                    "label": f"Date: {extraction.document_date}",
                    "metadata": {"date": extraction.document_date}
                })
                seen_nodes.add(date_id)
            edges.append({"from": doc_id, "to": date_id, "relation": "dated_on"})

    for idx, finding in enumerate(analysis.findings):
        finding_id = f"finding:{finding.finding_type}:{idx}"
        nodes.append({
            "id": finding_id,
            "type": "finding",
            "severity": finding.severity.value,
            "label": finding.finding_type.replace("_", " ").title(),
            "metadata": {
                "explanation": finding.explanation,
                "recommended_next_step": finding.recommended_next_step,
            }
        })
        for ref in finding.source_references:
            edges.append({"from": finding_id, "to": ref, "relation": "supported_by"})

    return {"case_id": case_id, "nodes": nodes, "edges": edges}
