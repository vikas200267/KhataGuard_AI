"""Test Gemini extraction provider (requires API key)."""

import pytest
from pathlib import Path
from unittest.mock import Mock, patch

from app.models.schemas import DocumentRecord, ExtractionOutput
from app.services.extraction import GeminiExtractionProvider


@pytest.fixture
def sample_document(tmp_path):
    content = b"%PDF-1.4\nsynthetic test document\n"
    source = tmp_path / "sale_deed.pdf"
    source.write_bytes(content)
    return DocumentRecord(
        document_id="doc-123",
        filename="sale_deed_42_3.pdf",
        document_type="sale_deed",
        content_type="application/pdf",
        size_bytes=len(content),
        source_uri=str(source),
        uploaded_at="2024-01-15T10:30:00Z",
    )


def test_gemini_provider_missing_api_key(sample_document):
    """Test provider fails gracefully without API key."""
    with patch("app.services.extraction.get_settings") as mock_settings:
        mock_settings.return_value.gemini_api_key = None
        provider = GeminiExtractionProvider()
        
        with pytest.raises(RuntimeError, match="missing GEMINI_API_KEY"):
            provider.extract(sample_document)


@pytest.mark.skip("Requires GEMINI_API_KEY in environment")
def test_gemini_provider_real_extraction(sample_document):
    """Integration test with real Gemini API (skip by default)."""
    provider = GeminiExtractionProvider()
    result = provider.extract(sample_document)
    
    assert isinstance(result, ExtractionOutput)
    assert result.document_id == sample_document.document_id
    assert isinstance(result.source_references, list)


def test_gemini_provider_mock_response(sample_document):
    """Test provider with mocked Gemini response."""
    mock_response = Mock()
    mock_response.text = """{
        "owner_name": "Test Owner",
        "survey_number": "42/3",
        "area_value": 1200.0,
        "area_unit": "sq_ft",
        "location": "Bangalore",
        "document_date": "2021-06-15",
        "document_identifiers": ["SD-2021-9482"],
        "boundary_description": "North: Road, South: Property 42/4",
        "state": "Karnataka",
        "language": "English",
        "source_references": []
    }"""
    
    with patch("app.services.extraction.get_settings") as mock_settings, \
         patch("google.genai.Client") as mock_client_class:
        
        mock_settings.return_value.gemini_api_key = "test-key"
        mock_client = Mock()
        mock_client.models.generate_content.return_value = mock_response
        mock_client_class.return_value = mock_client
        
        provider = GeminiExtractionProvider()
        result = provider.extract(sample_document)
        
        assert result.document_id == "doc-123"
        assert result.owner_name == "Test Owner"
        assert result.survey_number == "42/3"
        assert result.area_value == 1200.0
        request = mock_client.models.generate_content.call_args.kwargs
        assert request["contents"][1].inline_data.data == Path(sample_document.source_uri).read_bytes()
        assert request["contents"][1].inline_data.mime_type == "application/pdf"
        assert request["config"].response_mime_type == "application/json"
        assert result.source_references == []
