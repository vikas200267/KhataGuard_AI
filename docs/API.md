# API

Base URL: `http://localhost:8000`

- `GET /health`
- `GET /api/v1/states`
- `GET /api/v1/document-types`
- `POST /api/v1/cases`
- `GET /api/v1/cases/{case_id}`
- `POST /api/v1/cases/{case_id}/documents`
- `POST /api/v1/cases/{case_id}/analyze`
- `GET /api/v1/cases/{case_id}/findings`
- `GET /api/v1/cases/{case_id}/report`
- `DELETE /api/v1/cases/{case_id}`

OpenAPI docs are available from FastAPI at `/docs`.
