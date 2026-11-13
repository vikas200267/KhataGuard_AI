from types import SimpleNamespace
from unittest.mock import Mock

from app.models.schemas import PropertyCase
from app.repositories.firestore_repo import FirestoreCaseRepository


def test_firestore_stores_case_under_user_scoped_path():
    db = Mock()
    repo = FirestoreCaseRepository(client=db)
    case = PropertyCase(
        case_id="case-1",
        user_id="uid-1",
        property_name="Synthetic Plot",
        state="Karnataka",
        created_at="2026-11-14T00:00:00Z",
        documents=[],
    )

    repo.create_case(case)

    db.collection.assert_called_once_with("users")
    db.collection.return_value.document.assert_called_once_with("uid-1")
    db.collection.return_value.document.return_value.collection.assert_called_once_with("cases")
    db.collection.return_value.document.return_value.collection.return_value.document.assert_called_once_with("case-1")
    db.collection.return_value.document.return_value.collection.return_value.document.return_value.set.assert_called_once_with(
        case.model_dump(mode="json")
    )


def test_firestore_reads_case_only_from_requested_user_path():
    db = Mock()
    case_data = {
        "case_id": "case-1",
        "user_id": "uid-1",
        "property_name": "Synthetic Plot",
        "state": "Karnataka",
        "created_at": "2026-11-14T00:00:00+00:00",
        "documents": [],
    }
    case_ref = db.collection.return_value.document.return_value.collection.return_value.document.return_value
    case_ref.get.return_value = SimpleNamespace(exists=True, to_dict=lambda: case_data)

    result = FirestoreCaseRepository(client=db).get_case("case-1", "uid-1")

    assert result is not None
    assert result.case_id == "case-1"
    db.collection.return_value.document.assert_called_once_with("uid-1")
