from __future__ import annotations

from io import BytesIO

from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas

from app.models.schemas import AnalyzeResponse, FeeEstimateResponse, PropertyCase


DISCLAIMER = (
    "Preliminary risk screening only. This report does not confirm fraud or provide legal title certification."
)


def generate_case_report(case: PropertyCase, analysis: AnalyzeResponse, fee: FeeEstimateResponse) -> bytes:
    buffer = BytesIO()
    pdf = canvas.Canvas(buffer, pagesize=A4)
    y = 800

    pdf.setFont("Helvetica-Bold", 14)
    pdf.drawString(40, y, f"KhataGuard AI — Case Report ({case.case_id})")
    y -= 24

    pdf.setFont("Helvetica", 10)
    pdf.drawString(40, y, f"Property: {case.property_name} | State: {case.state}")
    y -= 20

    pdf.drawString(40, y, "Documents analyzed:")
    y -= 16
    for ext in analysis.extracted_documents:
        pdf.drawString(50, y, f"- {ext.document_type}: owner={ext.owner_name or 'N/A'}, survey={ext.survey_number or 'N/A'}")
        y -= 14

    y -= 8
    pdf.drawString(40, y, "Findings:")
    y -= 16
    for finding in analysis.findings:
        pdf.drawString(50, y, f"- [{finding.severity.value}] {finding.finding_type}: {finding.explanation}")
        y -= 14
        if y < 120:
            pdf.showPage()
            y = 800
            pdf.setFont("Helvetica", 10)

    y -= 8
    pdf.drawString(40, y, f"Fee estimate status: {fee.message}")
    y -= 16
    if fee.value is not None:
        pdf.drawString(50, y, f"Indicative value: {fee.currency} {fee.value}")
        y -= 14

    pdf.drawString(40, y, "Official verification checklist:")
    y -= 16
    for item in [
        "Verify registration record with official portal",
        "Cross-check mutation and tax records",
        "Confirm encumbrance certificate period",
    ]:
        pdf.drawString(50, y, f"- {item}")
        y -= 14

    y -= 8
    pdf.drawString(40, y, DISCLAIMER)

    pdf.showPage()
    pdf.save()
    return buffer.getvalue()
