# Feature Specification: MQTT JSON Payload Support

**Feature Branch**: `031-mqtt-json-payload-support`  
**Created**: 2026-03-29  
**Status**: Draft  
**Input**: User description: "Implement robust per-widget MQTT JSON payload parsing and write templates based on the MQTT_JSON_PAYLOAD_SUPPORT_PROPOSAL.md"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Per-Widget JSON Extraction (Priority: P1)

As an Engineer, I want to configure multiple widgets to read different values from the same MQTT JSON topic, so that I can reduce network traffic and simplify my broker configuration.

**Why this priority**: This is the core functional improvement that solves the "one-key-per-connection" limitation. It provides the most immediate value for industrial integrations like Zigbee2MQTT.

**Independent Test**: Can be fully tested by creating two Gauge widgets subscribed to the same topic (e.g., `sensors/living_room`) with different JSON Paths (e.g., `temp` and `humidity`) and verifying they display correct independent values from a single JSON payload.

**Acceptance Scenarios**:

1. **Given** a widget is configured with a JSON Path, **When** a JSON payload is received on the topic, **Then** the widget extracts and displays only the value at that path.
2. **Given** multiple widgets share the same topic but have different JSON Paths, **When** a single JSON payload is received, **Then** all widgets update simultaneously with their respective values.
3. **Given** a widget has no JSON Path, **When** any payload is received, **Then** it falls back to the global profile-level parsing (current behavior).

---

### User Story 2 - Nested Path Traversal (Priority: P2)

As an Engineer, I want to use dot-notation to access values nested deep within a JSON object, so that I can integrate with complex telemetry sources without restructuring the data at the source.

**Why this priority**: Industrial telemetry often groups data into logical sub-objects (e.g., `{"status": {"motor": {"rpm": 1500}}}`). Without nested support, the HMI remains incompatible with modern JSON schemas.

**Independent Test**: Can be tested by setting a JSON Path to `status.motor.rpm` and verifying that the widget correctly extracts `1500` from a nested JSON payload.

**Acceptance Scenarios**:

1. **Given** a JSON Path like `a.b.c`, **When** a nested JSON object is received, **Then** the system traverses the keys to find the leaf node value.
2. **Given** an invalid or missing path, **When** a JSON payload is received, **Then** the system logs a warning and displays the raw payload or a fallback value.

---

### User Story 3 - Structured JSON Write Templates (Priority: P2)

As an Engineer, I want to define a template for outgoing messages, so that I can control equipment that requires structured JSON commands rather than plain numbers.

**Why this priority**: Many modern IoT and HMI systems (like Home Assistant) require commands to be wrapped in JSON (e.g., `{"cmd": "SET", "val": 45}`).

**Independent Test**: Can be tested by configuring a Slider with a Write Template `{"brightness": $VALUE}` and verifying that moving the slider to 75 publishes the literal string `{"brightness": 75}` to the broker.

**Acceptance Scenarios**:

1. **Given** a Write Template is defined, **When** a widget action occurs (button press, slider move), **Then** the `$VALUE` placeholder is replaced by the widget's current value and published as the payload.
2. **Given** a Write Template uses quotes like `{"state": "$VALUE"}`, **When** a button is toggled to "on", **Then** the published payload is `{"state": "on"}` (string-wrapped).

---

### Edge Cases

- **Malformed JSON**: How does the system handle a payload that is not valid JSON? (Expected: Fall back to plain text parsing and log an error).
- **Type Mismatch**: What happens if the JSON Path points to an array or another object instead of a primitive value (Number/Boolean/String)? (Expected: Treat as a string representation or log "path not found").
- **Prefix Collision**: Does a leading `/` in a JSON-enabled topic still bypass the Root Topic Prefix? (Expected: Yes, standard prefix rules apply before JSON parsing).
- **Read/Write Asymmetry**: A widget with `jsonPath` but no `writeTemplate` reads from JSON but writes raw values. This is intentional — state and command topics often have different schemas.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow configuring a `jsonPath` field for every widget when using the MQTT protocol.
- **FR-002**: System MUST support dot-notation traversal for `jsonPath` (e.g., `level1.level2.key`).
- **FR-003**: System MUST allow configuring a `writeTemplate` for Slider and Button widgets.
- **FR-004**: System MUST replace the `$VALUE` token in `writeTemplate` with the formatted widget value before publishing.
- **FR-005**: System MUST implement a shared topic subscription cache to ensure JSON payloads are only parsed once per incoming message on a topic, regardless of how many widgets observe it.
- **FR-006**: System MUST fall back to profile-level `payloadFormat` and `jsonKey` if the widget-specific `jsonPath` is empty.
- **FR-007**: System MUST support literal substitution in `writeTemplate` (no automatic quoting of `$VALUE` unless the user includes quotes in the template).
- **FR-008**: If a widget has `jsonPath` set but no `writeTemplate`, outgoing writes MUST send the raw value (not auto-wrapped in JSON). JSON write formatting requires an explicit `writeTemplate`.
- **FR-009**: If `writeTemplate` contains multiple `$VALUE` tokens, all occurrences MUST be replaced.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: The Widget Configuration dialog MUST include clear labels and placeholder examples for "JSON Path" and "Write Template".

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Now includes `jsonPath` (String) and `writeTemplate` (String) metadata.
- **MqttPlcCommunicator**: Responsible for managing the topic cache and executing the extraction/template logic.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Multiple widgets can extract different keys from the same JSON payload topic with zero redundant network requests.
- **SC-002**: JSON parsing and extraction adds less than 50ms of latency to the end-to-end telemetry update (from receipt to UI render).
- **SC-003**: 100% backward compatibility: existing dashboards with no `jsonPath` configured continue to function without modification.
- **SC-004**: System handles nested JSON objects up to 5 levels deep without significant performance degradation.
