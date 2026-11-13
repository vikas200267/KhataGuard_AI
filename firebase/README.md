# Firebase and Firestore setup

The backend now verifies Firebase ID tokens and has an optional Firestore repository. A live project is still required to exercise these integrations.

1. Create a Firebase project and enable the Authentication providers you intend to use.
2. Set the Android package name to `com.swagcoders.khataguard`. Firebase sign-in in the Android client is not wired yet.
3. Install the pinned backend dependencies with `pip install -r backend/requirements.txt`.
4. For local backend development, obtain Application Default Credentials with `gcloud auth application-default login`. In Cloud Run, attach a dedicated service account and grant only the permissions needed for token verification and Firestore access.
5. Configure these backend environment variables:

   ```text
   APP_ENV=production
   DEMO_MODE=false
   FIREBASE_PROJECT_ID=<your-project-id>
   USE_FIRESTORE=true
   ```

6. Start the backend from `backend/` with `python -m uvicorn app.main:app --host 0.0.0.0 --port 8000`.
7. Send a Firebase client ID token as `Authorization: Bearer <id-token>` to protected API routes. The backend verifies it with Firebase Admin and uses its `uid` for case isolation.
8. Keep service-account JSON, `google-services.json`, and all credentials out of Git. Use ADC locally and the Cloud Run service identity in deployment.

`USE_FIRESTORE=true` persists cases and analysis under `users/{uid}/cases/{case_id}`. Uploaded documents still use local temporary storage, so Cloud Storage and durable file deletion must be configured before a public deployment.
