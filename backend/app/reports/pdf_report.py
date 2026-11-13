from __future__ import annotations

from io import BytesIO

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_RIGHT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

from app.models.schemas import AnalyzeResponse, FeeEstimateResponse, PropertyCase

DISCLAIMER = (
    "Preliminary risk screening only. This report does not confirm fraud or provide legal title certification. "
    "It is not a substitute for professional legal advice or official government verification. "
    "KhataGuard AI assumes no liability for decisions made based on this report."
)

# Brand colours
BRAND_PRIMARY = colors.HexColor("#1A237E")  # deep indigo
BRAND_ACCENT = colors.HexColor("#00897B")   # teal
SEVERITY_COLORS = {
    "high": colors.HexColor("#C62828"),
    "medium": colors.HexColor("#EF6C00"),
    "low": colors.HexColor("#2E7D32"),
}
LIGHT_BG = colors.HexColor("#F5F5F5")


def _styles():
    ss = getSampleStyleSheet()
    ss.add(ParagraphStyle("BrandTitle", parent=ss["Title"], textColor=BRAND_PRIMARY, fontSize=18, spaceAfter=4))
    ss.add(ParagraphStyle("SectionHead", parent=ss["Heading2"], textColor=BRAND_PRIMARY, fontSize=13, spaceBefore=14, spaceAfter=6))
    ss.add(ParagraphStyle("Body", parent=ss["BodyText"], fontSize=10, leading=14))
    ss.add(ParagraphStyle("Small", parent=ss["BodyText"], fontSize=8, leading=10, textColor=colors.grey))
    ss.add(ParagraphStyle("Center", parent=ss["BodyText"], alignment=TA_CENTER, fontSize=9))
    ss.add(ParagraphStyle("Right", parent=ss["BodyText"], alignment=TA_RIGHT, fontSize=8, textColor=colors.grey))
    return ss


def _footer(canvas_obj, doc):
    canvas_obj.saveState()
    canvas_obj.setFont("Helvetica", 7)
    canvas_obj.setFillColor(colors.grey)
    canvas_obj.drawString(40, 25, "KhataGuard AI — Confidential")
    canvas_obj.drawRightString(A4[0] - 40, 25, f"Page {doc.page}")
    canvas_obj.restoreState()


def generate_case_report(case: PropertyCase, analysis: AnalyzeResponse, fee: FeeEstimateResponse) -> bytes:
    buffer = BytesIO()
    doc = SimpleDocTemplate(buffer, pagesize=A4, topMargin=30 * mm, bottomMargin=20 * mm, leftMargin=20 * mm, rightMargin=20 * mm)
    ss = _styles()
    story: list = []

    # ── Header ──
    story.append(Paragraph("KhataGuard AI", ss["BrandTitle"]))
    story.append(Paragraph(f"Case Report — {case.case_id}", ss["Body"]))
    story.append(Spacer(1, 6))

    # Property summary table
    info = [
        ["Property", case.property_name],
        ["State", case.state],
        ["Created", str(case.created_at.strftime("%Y-%m-%d"))],
        ["Documents", str(len(analysis.extracted_documents))],
    ]
    t = Table(info, colWidths=[90, 360])
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (0, -1), LIGHT_BG),
        ("FONTNAME", (0, 0), (0, -1), "Helvetica-Bold"),
        ("FONTSIZE", (0, 0), (-1, -1), 10),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
    ]))
    story.append(t)
    story.append(Spacer(1, 10))

    # ── Property Identity Graph summary ──
    story.append(Paragraph("Property Identity Graph", ss["SectionHead"]))
    owners = {e.owner_name for e in analysis.extracted_documents if e.owner_name}
    surveys = {e.survey_number for e in analysis.extracted_documents if e.survey_number}
    graph_rows = [
        ["Unique owners identified", ", ".join(sorted(owners)) or "N/A"],
        ["Survey / property identifiers", ", ".join(sorted(surveys)) or "N/A"],
        ["Total graph nodes", str(len(analysis.extracted_documents) + len(owners) + len(surveys) + 1)],
    ]
    gt = Table(graph_rows, colWidths=[180, 270])
    gt.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (0, -1), LIGHT_BG),
        ("FONTNAME", (0, 0), (0, -1), "Helvetica-Bold"),
        ("FONTSIZE", (0, 0), (-1, -1), 9),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]))
    story.append(gt)
    story.append(Spacer(1, 10))

    # ── Evidence-linked findings ──
    story.append(Paragraph("Findings", ss["SectionHead"]))
    if not analysis.findings:
        story.append(Paragraph("No discrepancies detected.", ss["Body"]))
    for f in analysis.findings:
        sev_color = SEVERITY_COLORS.get(f.severity.value, colors.black)
        story.append(Paragraph(
            f'<font color="{sev_color.hexval()}">[{f.severity.value.upper()}]</font> '
            f'<b>{f.finding_type}</b>: {f.explanation}',
            ss["Body"],
        ))
        if f.source_references:
            story.append(Paragraph(f"Evidence: {', '.join(f.source_references)}", ss["Small"]))
        story.append(Spacer(1, 4))
    story.append(Spacer(1, 6))

    # ── Missing information ──
    missing_docs = [e for e in analysis.extracted_documents if e.missing_or_unreadable_fields]
    if missing_docs:
        story.append(Paragraph("Missing / Unreadable Information", ss["SectionHead"]))
        for e in missing_docs:
            story.append(Paragraph(
                f"{e.document_type}: {', '.join(e.missing_or_unreadable_fields)}",
                ss["Body"],
            ))
        story.append(Spacer(1, 6))

    # ── Fee estimate ──
    story.append(Paragraph("Fee Estimate", ss["SectionHead"]))
    fee_text = fee.message
    if fee.value is not None:
        fee_text += f" — Indicative value: {fee.currency} {fee.value}"
    story.append(Paragraph(fee_text, ss["Body"]))
    story.append(Spacer(1, 6))

    # ── Recommended next steps ──
    story.append(Paragraph("Recommended Next Steps", ss["SectionHead"]))
    steps_seen: set[str] = set()
    for f in analysis.findings:
        if f.recommended_next_step not in steps_seen:
            steps_seen.add(f.recommended_next_step)
            story.append(Paragraph(f"• {f.recommended_next_step}", ss["Body"]))
    if not steps_seen:
        story.append(Paragraph("• No specific remediation steps required.", ss["Body"]))
    story.append(Spacer(1, 6))

    # ── Official verification guidance ──
    story.append(Paragraph("Official Verification Guidance", ss["SectionHead"]))
    for item in [
        "Verify registration record with the official state portal.",
        "Cross-check mutation and property tax records.",
        "Confirm encumbrance certificate covers the required period.",
        "Validate survey sketch against the latest approved layout.",
    ]:
        story.append(Paragraph(f"• {item}", ss["Body"]))
    story.append(Spacer(1, 10))

    # ── Legal disclaimer box ──
    disclaimer_data = [[Paragraph(f"<b>Disclaimer:</b> {DISCLAIMER}", ss["Body"])]]
    dt = Table(disclaimer_data, colWidths=[450])
    dt.setStyle(TableStyle([
        ("BOX", (0, 0), (-1, -1), 1, colors.grey),
        ("BACKGROUND", (0, 0), (-1, -1), LIGHT_BG),
        ("TOPPADDING", (0, 0), (-1, -1), 8),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
        ("LEFTPADDING", (0, 0), (-1, -1), 10),
        ("RIGHTPADDING", (0, 0), (-1, -1), 10),
    ]))
    story.append(dt)

    doc.build(story, onFirstPage=_footer, onLaterPages=_footer)
    return buffer.getvalue()
