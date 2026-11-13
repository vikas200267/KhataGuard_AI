# Architecture

- **Android app**: Kotlin + Compose + MVVM with Retrofit repository layer and DEMO MODE UX.
- **Backend API**: FastAPI service with repository abstraction and deterministic analysis pipeline.
- **Extraction providers**: mock fixture provider by default; Gemini provider stub behind shared interface.
- **Orchestration**: ADK-compatible modular functions in `backend/app/agents`.
- **Storage abstraction**: local in-memory repository for local dev and Firestore interface for production.
- **Identity graph**: Firestore-compatible node/edge structure generated from case + extraction + findings.
