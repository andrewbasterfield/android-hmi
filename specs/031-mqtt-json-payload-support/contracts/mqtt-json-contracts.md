# MQTT JSON Path Contract

## Addressing Format

The `jsonPath` field on a widget allows you to extract a single primitive value from a JSON payload.

### Syntax
`key1.key2.key3`

### Rules
1. **Plain Key:** `temp` extracts `25.5` from `{"temp": 25.5}`.
2. **Nested Key:** `status.motor.rpm` extracts `1500` from `{"status": {"motor": {"rpm": 1500}}}`.
3. **Invalid Path:** If the path does not exist, the system logs a warning and displays the raw payload as a fallback.
4. **Arrays:** Not supported in the initial version. The path MUST only reference objects.

---

# MQTT JSON Write Template Contract

## Template Format

The `writeTemplate` field allows you to wrap outgoing values in a JSON structure.

### Substitution Token
`$VALUE`

### Rules
1. **Raw Substitution:** Template `{"level": $VALUE}` with value `75` results in `{"level": 75}`.
2. **String Wrapping:** Template `{"cmd": "$VALUE"}` with value `on` results in `{"cmd": "on"}`.
3. **Static Command:** Template `{"reset": true}` (no `$VALUE`) sends the literal payload regardless of the widget's internal value.
4. **Multiple Tokens:** Template `{"min": $VALUE, "max": $VALUE}` with value `50` results in `{"min": 50, "max": 50}`. All occurrences are replaced.
