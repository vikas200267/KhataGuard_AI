from pathlib import Path

from app.state_adapters.base import StateAdapter
from app.state_adapters.json_adapter import JsonStateAdapter


ADAPTERS = {
    "Karnataka": "karnataka.v1.json",
    "Maharashtra": "maharashtra.placeholder.v1.json",
    "Tamil Nadu": "tamilnadu.placeholder.v1.json",
}


def get_state_adapter(state: str) -> StateAdapter:
    filename = ADAPTERS.get(state, "karnataka.v1.json")
    config_path = Path(__file__).resolve().parent / "config" / filename
    return JsonStateAdapter(config_path)
