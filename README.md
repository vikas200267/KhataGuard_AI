# KhataGuard AI

KhataGuard AI is a production-minded starter monorepo for multilingual Indian property-document intelligence and preliminary risk screening by Team SwagCoders.

## What works now

- FastAPI backend with demo-ready synthetic analysis pipeline.
- Deterministic mock extraction for sale deed vs khata comparison.
- Discrepancy engine detects **120 sq. ft.** difference for demo fixtures.
- PDF report endpoint with legal disclaimer and review checklist.
- State-adapter architecture (Karnataka validated sample + Maharashtra/Tamil Nadu placeholders).
- Simulated registry connector with explicit warning:
  - **SIMULATED REGISTRY DATA — NOT OFFICIAL GOVERNMENT VERIFICATION.**
- Android Jetpack Compose scaffold with 13 navigable screens and DEMO MODE banner.
- GitHub Actions workflows for backend tests and Android build.

## Repository layout

- `backend/` FastAPI API, rules, adapters, services, reports and tests
- `android-app/` Kotlin + Compose + MVVM app scaffold
- `sample-data/` synthetic fixtures only (fictional)
- `docs/` architecture, API, security and roadmap notes

## Quick start (backend)

```bash
cp .env.example .env
python -m venv .venv
source .venv/bin/activate
pip install -r backend/requirements.txt
PYTHONPATH=backend uvicorn app.main:app --reload --app-dir backend
```

Run tests:

```bash
PYTHONPATH=backend pytest backend/tests
```

### Docker

```bash
docker compose up --build
```

## Demo flow

1. Create a case (`POST /api/v1/cases`).
2. Upload two PDFs (e.g. `sale_deed.pdf`, `khata_record.pdf`).
3. Call `POST /api/v1/cases/{case_id}/analyze`.
4. Check findings and report endpoints.

## Cloud Run + Firebase notes

See `docs/DEMO.md` and `docs/SECURITY.md` for deployment/security setup. Production token verification is represented with a strict interface and local test token fallback.

## Legal and safety

- Findings are preliminary risk indicators, not fraud confirmation.
- Only synthetic sample data is included.
- No private property documents or credentials are committed.
