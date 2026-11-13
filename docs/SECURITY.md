# Security status

The current application is a local/demo MVP. The controls below describe code that exists; they are not a production security certification.

## Authentication and data isolation

- `DEMO_MODE=true` maps all requests to `demo-user` and is intended only for synthetic data.
- With demo mode disabled, the backend accepts the repository's `test-<username>` development token format. It does **not** verify Firebase ID tokens. Do not expose this configuration publicly.
- The in-memory repository filters case operations by `user_id`. This is not durable or a substitute for production tenant controls.

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

Before handling real property records in a public deployment, implement Firebase ID-token verification, Firestore/Storage tenant rules, private bucket access, deletion of cloud objects, rate limiting, request/body limits at the ingress, and least-privilege IAM. No government registry is queried by this application.
