# KhataGuard AI — India's State-Adaptive Property Intelligence Copilot

Developed by **Team SwagCoders** for the **Google DeepMind × Google Cloud Startup Buildathon India 2026**.

[![Backend](https://img.shields.io/badge/Backend-FastAPI-blue.svg)](#)
[![Android](https://img.shields.io/badge/Android-Jetpack%20Compose%20%7C%20Material%203-blue.svg)](#)
[![AI Engine](https://img.shields.io/badge/AI-Google%20Gemini-orange.svg)](#)
[![Cloud Ready](https://img.shields.io/badge/Cloud-Google%20Cloud%20Run%20%7C%20Firebase-yellow.svg)](#)

---

## 1. Product Objective

**KhataGuard AI** is an autonomous property pre-check copilot tailored to the nuanced statutory and linguistic realities of real estate transactions across India.

Buying land or an apartment in India requires navigating diverse regional document registries:
- In **Karnataka**: Registered Sale Deeds (Kaveri 2.0), BBMP e-Khata Certificates, Rural RTC/Pahani (Bhoomi), and e-Swathu records.
- In **Maharashtra**: Registered Conveyance Deeds, 7/12 Extracts (Satbara Utara), 8A Holding Records, and Malmatta Patrak (Property Cards).
- In **Tamil Nadu**: Sale Deeds (TNREGINET), Patta/Chitta Extracts, and TSLR records.

Buyers often face costly disputes because of subtle discrepancies:
- Variations in survey numbers and subdivision hissas.
- Land area variances between registered sale deeds and local municipal khata assessments (e.g. road widening deductions, boundary encroachments, or unapproved layouts).
- Name variations due to transliteration between local scripts (Kannada, Marathi, Tamil) and English.
- Missing statutory boundary descriptions (North, South, East, West).

**KhataGuard AI** allows buyers to upload deed and khata records, automatically extracts multilingual property fields using Google Gemini, executes deterministic comparison rules, builds a connected **Property Identity Graph**, and generates an evidence-linked **Property Pre-Check Report**.

> **IMPORTANT LEGAL NOTICE**: KhataGuard AI is a preliminary risk screening and document intelligence tool. It is **NOT** a certified government title verification agency and does **NOT** claim live or unauthorized integration with Kaveri 2.0, e-Swathu, Bhoomi, or NGDRS.

---

## 2. System Architecture

```text
[ Android Frontend (Jetpack Compose, MVVM, M3, Hilt) ]
                  │
                  │ REST / JSON (Retrofit + OkHttp)
                  ▼
[ FastAPI Backend (Google Cloud Run / Local Uvicorn) ]
   ├── Python Orchestration Pipeline (not Google ADK runtime)
   │     ├── 1. Extraction Provider (Gemini multimodal / synthetic mock)
   │     ├── 2. Normalization Tool (Names, Dates, Area conversions)
   │     ├── 3. State Adapter Tool (Karnataka v1 / MH & TN placeholders)
   │     ├── 4. Consistency Engine (Deterministic Comparison Rules)
   │     └── 5. Report Explanation Agent (Risk & Remediation Context)
   │
   ├── Property Identity Graph Service
   │     └── Connects Cases, Deeds, Owners, Surveys, Area, Dates & Findings
   │
   ├── Pre-Check Report Service
   │     └── ReportLab Platypus PDF Generator (Official verification checklist)
   │
   └── Security & Repository Layer
         ├── In-Memory Thread-Safe Repository (Demo / Local)
         ├── In-memory local repository (active)
         └── Firebase/Firestore integration (not wired)
```

---

## 3. Technology Stack

### Android Frontend
- **Language**: Kotlin 1.9.25
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: MVVM with unidirectional data flow
- **Navigation**: Navigation Compose (14 connected routes with argument passing)
- **Dependency Injection**: Dagger Hilt
- **Asynchronous Execution**: Coroutines and StateFlow
- **Networking**: Retrofit 2.11 + Moshi + OkHttp Logging Interceptor

### Python Backend
- **Framework**: FastAPI 0.116 + Starlette
- **Data Validation**: Pydantic v2
- **AI Multimodal Ingestion**: Google Gemini API (`google-genai` SDK)
- **Agent Workflow**: Modular Python orchestration pipeline; Google ADK runtime is not installed
- **Deterministic Rules Engine**: Python stdlib (exact arithmetic, sequence matching)
- **PDF Report Engine**: ReportLab 4.4 Platypus
- **Testing**: pytest + pytest-asyncio (see current results in `docs/IMPLEMENTATION_STATUS.md`)

### Google Cloud Infrastructure
- **Deployment**: Google Cloud Run (Containerized via Docker)
- **Database**: In-memory local repository; Firestore is not wired
- **Authentication**: Demo bypass or developer test tokens; Firebase verification is not wired
- **Storage**: Local temporary files; Cloud Storage is not wired
- **Configuration & Secrets**: Secret Manager / Pydantic Settings

---

## 4. Dual Operating Modes

### DEMO MODE (Offline / Self-Contained)
- Operates out-of-the-box with **zero cloud credentials or API keys required**.
- Uses synthetic demonstration fixtures:
  - **Sale Deed**: Plot 42/3, Area: 1,200 sq. ft., Owner: Ravi Kumar (English).
  - **BBMP e-Khata Extract**: Survey 42/3, Area: 1,080 sq. ft., Owner: Ravi K. (Kannada).
  - **Detected Discrepancy**: Exact **120 sq. ft.** variance + name transliteration + missing boundary schedule.
- Explicitly watermarked with `DEMO MODE — synthetic data only`.

### LIVE AI MODE (backend extraction)

The backend can send uploaded PDF/JPEG/PNG bytes to the configured Google GenAI model when `GEMINI_API_KEY` is set and `DEMO_MODE=false`. Returned JSON is validated as a Pydantic extraction model. No real Gemini call, Firebase login, Firestore persistence, or Cloud Storage has been configured or verified here. Do not use live mode with private documents until production authentication, storage, and deletion controls are configured.

---

## 5. Quick Start Guide

### Prerequisites
- Python 3.11+
- Android Studio Ladybug / Meerkat (with JDK 17+ or 21)
- Git

### Backend Setup & Execution

```bash
# 1. Clone repository
git clone https://github.com/SwagCoders/KhataGuard_AI.git
cd KhataGuard_AI/backend

# 2. Configure environment
cp ../.env.example .env

# 3. Install dependencies
pip install -r requirements.txt

# 4. Run the backend test suite
python -m pytest tests/ -v

# 5. Launch FastAPI development server
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

The interactive API documentation is available at `http://localhost:8000/docs` in development mode.

### Android Application Setup

```bash
# 1. Navigate to android directory
cd ../android-app

# 2. Build and verify Kotlin compilation
./gradlew compileDebugKotlin

# 3. Run Android unit tests
./gradlew testDebugUnitTest

# 4. Assemble Debug APK
./gradlew assembleDebug
```

---

## 6. End-to-End User Flow

```mermaid
graph TD
    A[Welcome Screen] --> B[Login / Demo Mode]
    B --> C[Home Dashboard]
    C --> D[Create Property Case]
    D --> E[Select State: Karnataka / MH / TN]
    E --> F[Upload Deeds & Khata Records]
    F --> G[Processing: Multimodal OCR & Normalization]
    G --> H[Extracted Property Details]
    H --> I[Property Identity Graph Viewer]
    I --> J[Discrepancy Analysis (120 sq ft Variance)]
    J --> K[Side-by-Side Evidence Citations]
    K --> L[Generate & Download Pre-Check PDF Report]
```

---

## 7. State Adapter Architecture

KhataGuard AI adapts its terminology and validation requirements to each Indian state:

| State | Status | Primary Registries | Statutory Records |
|---|---|---|---|
| **Karnataka** | **Implemented configuration v1** (not legally validated) | Kaveri 2.0, Bhoomi, e-Swathu | Sale Deed, BBMP e-Khata, RTC (Pahani), EC Form 15 |
| **Maharashtra** | **Placeholder v1** | IGR Maharashtra, Mahabhulekh | Kharedi Khat, Index II, 7/12 Extract, Property Card |
| **Tamil Nadu** | **Placeholder v1** | TNREGINET, e-Services | Sale Deed, Patta/Chitta Extract, TSLR, EC |

---

## 8. License & Team

Developed with ❤️ by **Team SwagCoders**:
- Lead Architecture, Cloud & Security Review
- Android Compose & Mobile Experience
- Multi-Agent Orchestration & Gemini AI Integration

Released under the **MIT License**.
