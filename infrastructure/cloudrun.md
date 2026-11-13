# Cloud Run deployment (backend)

```bash
gcloud builds submit --tag gcr.io/$PROJECT_ID/khataguard-backend .
gcloud run deploy khataguard-backend \
  --image gcr.io/$PROJECT_ID/khataguard-backend \
  --platform managed \
  --allow-unauthenticated=false \
  --set-env-vars DEMO_MODE=false,USE_FIRESTORE=true
```

Use Secret Manager for GEMINI_API_KEY and Firebase credentials.
