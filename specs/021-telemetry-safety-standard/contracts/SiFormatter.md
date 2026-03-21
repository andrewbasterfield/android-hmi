# Interface Contract: SiFormatter

## Purpose
Strictly enforces SI unit compliance and prevents dangerous case-sensitivity errors in the HMI.

## Symbol Dictionary (Mandatory Compliance)

| Magnitude | Correct Symbol | Forbidden Transformation |
|-----------|----------------|--------------------------|
| Millivolts | `mV` | `MV`, `mv` |
| Megavolts | `MV` | `mv`, `mV` |
| Kilowatts | `kW` | `KW`, `kw` |
| Megawatts | `MW` | `mw`, `mW` |
| Hertz | `Hz` | `HZ`, `hz` |
| Megahertz | `MHz` | `mHz`, `MHZ` |
| Meters | `m` | `M` |
| Liters | `L` | `l` |

## UI Contract
1. **Value Render**: Numerics are formatted to `%.1f`.
2. **Unit Render**: Units are displayed in the `Inter` font, **Medium Weight**.
3. **No Transformation**: Any `String.toUpperCase()` call on the unit string is a functional safety violation.

## Data Layer Contract
- Data arriving via PLC tag attributes MUST NOT be sanitized for case unless matching the Symbol Dictionary.
- If a unit is not recognized in the SI dictionary, it MUST be rendered exactly as received without modification.
