# Security status

The current application is a local/demo MVP. The controls below describe code that exists; they are not a production security certification.

## Authentication and data isolation

- `DEMO_MODE=true` maps all requests to `demo-user` and is intended only for synthetic data.
- With demo mode disabled, Firebase Admin verifies ID tokens and checks revocation before using the token UID. `test-<username>` developer tokens work only when `APP_ENV=development`.
- When `USE_FIRESTORE=true`, case documents are nested under `users/{uid}/cases/{case_id}`. This code path has not been smoke-tested against a live Firebase project.
- The local repository filters case operations by `user_id` but is in-memory and not durable.

## Upload handling

- Only PDF, JPEG, and PNG MIME types and matching file signatures are accepted.
- Upload reads are capped at configured `MAX_UPLOAD_SIZE_BYTES` plus one detection byte.
- Files receive server-generated names in a host temp directory. Case deletion attempts to remove those local files.
- There is no Cloud Storage integration, malware scanning, PDF page-count limit, or durable cloud deletion workflow.

## Logging and secrets

- The app does not intentionally log document contents. Review provider/SDK logs before enabling live AI in a sensitive environment.
- Keep `.env`, Gemini keys, Firebase configuration, and service-account files out of version control. `.gitignore` excludes these file types.
- `GEMINI_API_KEY` is read from environment settings; Secret Manager deployment is not implemented here.

## Production work required

Before handling real property records in a public deployment, configure Firebase credentials and Firestore, implement private Cloud Storage uploads and object deletion, add rate limiting and ingress body limits, and use least-privilege IAM. No government registry is queried by this application.
