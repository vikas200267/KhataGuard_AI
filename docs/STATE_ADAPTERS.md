# KhataGuard AI — National State Adapter Architecture

**Author**: Team SwagCoders  
**Audience**: Real Estate Domain Specialists & Systems Engineers  
**Last Updated**: 2026-11-13

---

## 1. Why State Adapters Are Necessary

Land administration and real estate registration in India fall under the **State List** (Entry 18) and **Concurrent List** (Entry 6) of the Constitution of India. Consequently:

1. **Document Nomenclature Diverges**:
   - A property tax register is called a **Khata** (A Khata / B Khata) in Karnataka, a **Property Card** or **Malmatta Patrak** in urban Maharashtra, and a **Property Tax Receipt (Vari)** in Tamil Nadu.
   - Land titles in rural areas are called **RTC / Pahani** in Karnataka, **7/12 Extract (Satbara Utara)** in Maharashtra, and **Patta / Chitta** in Tamil Nadu.
2. **Measurement Units Vary**:
   - Karnataka: Square feet (`sq_ft`), Square meters (`sq_m`), Guntas (`1 gunta = 1,089 sq ft`), and Acres (`1 acre = 43,560 sq ft`).
   - Tamil Nadu: Cent (`1 cent = 435.6 sq ft`), Ground (`1 ground = 2,400 sq ft`), and Acre.
   - Maharashtra: Guntha (`1 guntha = 1,089 sq ft`), Hectare (`1 ha = 10,000 sq m`), and Square meter.
3. **Registration Portals Differ**:
   - Karnataka: **Kaveri 2.0** (Registration), **Bhoomi** (Land Records), **e-Swathu** (Panchayat Properties).
   - Maharashtra: **IGR Maharashtra (SARITA)**, **Mahabhulekh**.
   - Tamil Nadu: **TNREGINET**, **Anytime Anywhere e-Services**.

---

## 2. The State Adapter Interface

Each adapter inherits from `StateAdapter` (`app/state_adapters/base.py`):

```python
class StateAdapter(ABC):
    state_name: str
    version: str

    @abstractmethod
    def supported_document_terms(self) -> dict[str, str]:
        """Returns map of canonical keys to state-specific record titles."""
        raise NotImplementedError

    @abstractmethod
    def required_checks(self) -> list[str]:
        """Returns mandatory discrepancy checks for this jurisdiction."""
        raise NotImplementedError
```

Configuration files reside in `app/state_adapters/config/<state_slug>.<version>.json`.

---

## 3. Supported Adapters

### Karnataka (`karnataka.v1.json`) — **IMPLEMENTED CONFIGURATION v1 (not legally validated)**
- The configuration provides terminology and basic document comparison checks. It has not been reviewed or certified by Karnataka authorities or legal counsel.
- **Required Documents**:
  - `sale_deed`: Registered Conveyance Deed
  - `khata_record`: BBMP e-Khata Certificate or Extract
- **Active Checks**:
  - `survey_number_mismatch`
  - `property_area_difference` (50 sq. ft. threshold)
  - `owner_name_variation` (transliteration fuzzy comparison)
  - `missing_owner`
  - `missing_required_fields` (boundary schedule)
  - `conflicting_property_identifiers`
  - `document_date_inconsistency`

### Maharashtra (`maharashtra.placeholder.v1.json`) — **PLACEHOLDER v1**
- **Terminology**: 7/12 Extract, 8A Extract, Index II, Malmatta Patrak.
- **Active Checks**: Basic survey number and area comparison.
- **Explicit Limitations**: Tenancy (Kul) rights, Non-Agricultural (NA) order regularizations, and ULCRA clearances are marked as unsupported and deferred to official verification.

### Tamil Nadu (`tamilnadu.placeholder.v1.json`) — **PLACEHOLDER v1**
- **Terminology**: Patta, Chitta, TSLR Extract, FMB Sketch, Villangam Sanrridhazh (EC).
- **Active Checks**: Basic survey number and area comparison.
- **Explicit Limitations**: Temple HR&CE land checks and conditional Panchami land verifications require official department clearance.

---

## 4. How to Add a New State Adapter

1. Create a new configuration JSON file in `app/state_adapters/config/<state_name>.v1.json`.
2. Define canonical document mappings under `"document_terms"`.
3. Register the state in `app/state_adapters/selector.py` within `ADAPTERS`.
4. Add the state to the enum `SupportedState` in `app/models/schemas.py`.
5. Write corresponding test fixtures in `backend/tests/`.
