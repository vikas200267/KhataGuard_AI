# API specification (v1)

Base path: `/api/v1`. Local/demo requests use `DEMO_MODE=true` and synthetic fixtures. With demo mode disabled, send a Firebase Authentication ID token as `Authorization: Bearer <id-token>`. Tokens are verified by Firebase Admin with revocation checks. Development servers also accept `Bearer test-<username>` only when `APP_ENV=development`.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Health and configured mode |
| GET | `/api/v1/states` | Supported adapter jurisdictions |
| GET | `/api/v1/document-types` | Document category list |
| POST | `/api/v1/cases` | Create a case |
| GET | `/api/v1/cases` | List current user's cases |
| GET | `/api/v1/cases/{id}` | Retrieve a case |
| POST | `/api/v1/cases/{id}/documents` | Upload PDF/JPEG/PNG (configured size limit) |
| POST | `/api/v1/cases/{id}/analyze` | Extract and compare documents |
| GET | `/api/v1/cases/{id}/findings` | Retrieve analysis |
| GET | `/api/v1/cases/{id}/graph` | Retrieve the identity graph |
| GET | `/api/v1/cases/{id}/report` | Download PDF pre-check report |
| DELETE | `/api/v1/cases/{id}` | Delete local case and uploaded temp files |

Analysis is document intelligence only. It does not certify title, query a government registry, or establish fraud. In demo mode extraction uses synthetic fixtures. In live AI mode the backend sends document bytes to Google GenAI when configured; external calls and Firebase integration have not been verified in this workspace.
