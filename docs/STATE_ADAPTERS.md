# State adapters

Adapters are selected by state name and loaded from versioned JSON files under `backend/app/state_adapters/config`.

Current adapters:
- `karnataka.v1.json` (sample validated starter)
- `maharashtra.placeholder.v1.json` (placeholder)
- `tamilnadu.placeholder.v1.json` (placeholder)

## Adding a new adapter

1. Create `state-name.vX.json`.
2. Define terminology and required checks.
3. Mark `validated` status honestly.
4. Register in `selector.py`.
5. Add tests for adapter selection and checks.
