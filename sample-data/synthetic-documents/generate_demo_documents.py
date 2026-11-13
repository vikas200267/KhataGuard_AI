"""Generate clearly fictional PDFs for the local KhataGuard demo."""

from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfgen.canvas import Canvas
from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle


OUTPUT = Path(__file__).resolve().parent


def add_watermark(canvas: Canvas, document: object) -> None:
    canvas.saveState()
    canvas.setFillColor(colors.HexColor("#C62828"))
    canvas.setFont("Helvetica-Bold", 18)
    canvas.drawCentredString(A4[0] / 2, 14 * mm, "SYNTHETIC SAMPLE — NOT AN OFFICIAL RECORD")
    canvas.restoreState()


def make_document(filename: str, title: str, fields: list[tuple[str, str]]) -> None:
    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(name="SampleTitle", parent=styles["Title"], alignment=TA_CENTER, textColor=colors.HexColor("#173B6C")))
    story = [
        Paragraph("SYNTHETIC DEMONSTRATION DOCUMENT", styles["Heading2"]),
        Paragraph(title, styles["SampleTitle"]),
        Spacer(1, 8 * mm),
        Paragraph("Every name, identifier, address, and measurement on this page is fictional and exists only to demonstrate document comparison.", styles["BodyText"]),
        Spacer(1, 7 * mm),
    ]
    table = Table([[Paragraph(f"<b>{key}</b>", styles["BodyText"]), Paragraph(value, styles["BodyText"])] for key, value in fields], colWidths=[48 * mm, 108 * mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#EAF1FB")),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#B8C6D9")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 7),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    story.extend([table, Spacer(1, 8 * mm), Paragraph("This is not a deed, khata, title opinion, or government record. Do not use it for a property transaction.", styles["Italic"])])
    SimpleDocTemplate(str(OUTPUT / filename), pagesize=A4, bottomMargin=25 * mm).build(story, onFirstPage=add_watermark, onLaterPages=add_watermark)


if __name__ == "__main__":
    make_document("sample_sale_deed.pdf", "Sample Sale Deed Extract", [
        ("Fictional owner", "Ravi Kumar"),
        ("Fictional survey number", "42/3"),
        ("Fictional site area", "1,200 sq. ft."),
        ("Fictional location", "Plot 42, Green Glen Layout, Bengaluru"),
        ("Fictional document date", "2021-08-01"),
        ("Fictional boundaries", "North: Road; South: Site 12; East: Site 43; West: Private Property"),
    ])
    make_document("sample_khata.pdf", "Sample Khata Record Extract", [
        ("Fictional owner", "Ravi K."),
        ("Fictional survey number", "42/3"),
        ("Fictional site area", "1,080 sq. ft."),
        ("Fictional location", "Plot 42, Green Glen Layout, Bengaluru"),
        ("Fictional document date", "2022-02-13"),
        ("Fictional boundaries", "Not shown in this sample"),
    ])
