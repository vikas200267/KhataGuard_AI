# KhataGuard AI — Demonstration Walkthrough Guide

**Target Audience**: Buildathon Evaluators, Technical Judges, and Product Reviewers  
**Operating Mode**: Synthetic Demonstration Mode (Deterministic, zero cloud configuration required)

---

## 1. Demonstration Scenario

A buyer in Bengaluru is evaluating a residential plot located in **Bellandur (Ward 11)**.  
The seller presents a registered **Sale Deed** executed in 2021. The buyer also obtains a **BBMP e-Khata Extract** issued in 2022.

### Documents Ingested:
1. **Document 1 (Sale Deed)**:
   - Document Type: Registered Conveyance Deed
   - Jurisdiction: Karnataka (Kaveri 2.0)
   - Language: English
   - Owner: Sri Ravi Kumar
   - Survey Identifier: Sy No. 42/3
   - Stated Land Extent: **1,200 sq. ft.**
   - Boundary Schedule: North: 30ft Road, South: Site 12, East: Site 43, West: Private Property
2. **Document 2 (BBMP e-Khata Record)**:
   - Document Type: Municipal Property Tax Assessment Extract
   - Jurisdiction: Karnataka (BBMP e-Khata)
   - Language: Kannada
   - Owner: Ravi K.
   - Survey Identifier: Sy No. 42/3
   - Stated Land Extent: **1,080 sq. ft.**
   - Boundary Schedule: Unrecorded / Missing

### Expected Intelligence Output:
- **Discrepancy 1**: **120 sq. ft. area variance** (1,200 sq. ft. vs 1,080 sq. ft. exceeds 50 sq. ft. risk threshold).
- **Discrepancy 2**: Owner name variation (`Ravi Kumar` vs `Ravi K.` transliteration).
- **Discrepancy 3**: Missing statutory boundary description in the municipal record.
- **Property Identity Graph**: Connected topology linking both records to survey 42/3.
- **Actionable Advice**: Verify road widening setback deductions with BBMP ARO and inspect survey podi sketch.

---

## 2. Interactive Android Demo Steps

1. **Launch App**: Open KhataGuard AI on an Android emulator or physical device.
2. **Welcome Screen**: Tap **"Start Synthetic Live Demo"**.
3. **Dashboard**: View active cases and tap **"Create New Case"**.
4. **State Selection**: Choose **"Karnataka"** and enter `"Plot 42, Green Glen Layout"`. Tap Proceed.
5. **Upload Screen**: Tap **"+ Sale Deed (1200 sq ft)"** and **"+ e-Khata (1080 sq ft)"**.
6. **Processing Screen**: Watch the multi-agent orchestration pipeline progress through OCR Ingestion -> Normalization -> State Adapter -> Graph Generation.
7. **Extracted Details Screen**: Review the side-by-side extracted data cards.
8. **Property Identity Graph**: Tap **"Inspect Property Identity Graph"** to visualize connected nodes (Case, Deeds, Owners, Survey 42/3, Area Extents, and Discrepancies).
9. **Discrepancy Screen**: Inspect the highlighted findings with severity color codes.
10. **Evidence Viewer**: Tap **"Inspect Side-by-Side Evidence References"** to review the exact clauses triggering the 120 sq. ft. discrepancy.
11. **PDF Report Screen**: Tap **"Download Property Pre-Check PDF Report"** to generate the evidence-linked summary.

---

## 3. End-to-End Terminal / cURL Demo

Generate the clearly watermarked, fictional PDF inputs before starting the API:

```bash
python sample-data/synthetic-documents/generate_demo_documents.py
```

You can then run the flow against the FastAPI backend using standard CLI tools:

### Step 1: Health Check
```bash
curl -X GET http://localhost:8000/health
```
*Output: `{"status": "ok", "mode": "demo"}`*

### Step 2: Create Property Case
```bash
curl -X POST http://localhost:8000/api/v1/cases \
  -H "Content-Type: application/json" \
  -d '{"property_name": "Plot 42, Green Glen Layout", "state": "Karnataka"}'
```
*Returns `case_id`.*

### Step 3: Upload Two Sample Documents
```bash
# Upload Sale Deed
curl -X POST http://localhost:8000/api/v1/cases/{case_id}/documents \
  -F "file=@sample-data/synthetic-documents/sample_sale_deed.pdf"

# Upload Khata Extract
curl -X POST http://localhost:8000/api/v1/cases/{case_id}/documents \
  -F "file=@sample-data/synthetic-documents/sample_khata.pdf"
```

### Step 4: Execute Agentic Analysis
```bash
curl -X POST http://localhost:8000/api/v1/cases/{case_id}/analyze \
  -H "Content-Type: application/json" \
  -d '{"transaction_type": "sale"}'
```

### Step 5: Fetch Property Identity Graph
```bash
curl -X GET http://localhost:8000/api/v1/cases/{case_id}/graph
```

### Step 6: Download Pre-Check PDF Report
```bash
curl -X GET http://localhost:8000/api/v1/cases/{case_id}/report \
  --output PreCheck_Report_Case42.pdf
```
