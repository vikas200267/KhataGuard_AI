from app.services.fee_estimator import FeeEstimator
from app.state_adapters.selector import get_state_adapter


def test_state_adapter_selection():
    ka = get_state_adapter("Karnataka")
    assert ka.state_name == "Karnataka"

    mh = get_state_adapter("Maharashtra")
    assert "placeholder" in mh.version


def test_unknown_fee_rule_unavailable():
    fee = FeeEstimator().estimate("Karnataka", "gift")
    assert fee.status == "unavailable"
    assert fee.message == "Estimate unavailable — official rate verification required"
