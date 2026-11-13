# Security baseline

- Environment-driven configuration (`.env`, `.env.example`).
- In production mode (`DEMO_MODE=false`), bearer token required.
- Secure per-user access checks in repository methods.
- File upload constraints: type allowlist + max size checks.
- No document content logged.
- Case deletion endpoint removes stored case + analysis state.
- CORS restricted by configured origin list.
- Firebase/Gemini credentials are never stored in source.
