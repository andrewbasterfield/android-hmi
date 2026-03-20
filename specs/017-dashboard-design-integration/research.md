# Research: Dashboard Design Integration (Kinetic Cockpit)

**Feature**: `017-dashboard-design-integration` | **Status**: Complete
**Input**: Planning Phase 0 research tasks from `plan.md`

## 1. Tactical Resize Handles for Industrial Touchscreens

- **Decision**: Implement a 32x32dp corner handle with a high-contrast diagonal "Stripe" or "Grip" pattern.
- **Rationale**: In high-vibration environments, single-pixel corners are impossible to hit. A dedicated, visually distinct handle area ensures operators can resize without frustration.
- **Alternatives**:
  - Edge-dragging: Rejected due to accidental triggers during scrolling/swiping.
  - Context Menu resizing: Rejected for being too slow/high-tap-count.

## 2. Industrial-Scale Typography Audit (10" Tablets)

- **Decision**: 
  - Standard Labels: 16sp (Bold)
  - Primary Readouts: 24sp - 32sp (Monospaced)
  - Secondary Data: 12sp (Min)
- **Rationale**: Based on common industrial standards (ISO 9241-303), labels must be readable from a distance of ~1 meter. Space Grotesk's wide stance requires slightly larger sizes to maintain clarity.
- **Alternatives**:
  - Material 3 Defaults: Rejected as they are optimized for close-range handheld use, not mounted industrial screens.

## 3. Legacy Color Migration Logic

- **Decision**:
  - Exact Color Match: If a legacy hex matches a Kinetic token (e.g., #FF0000), keep it but wrap in the token.
  - Grey Scale: Map to obsidian surface levels (#131313, #1A1A1A, etc.).
  - Other: Sanitize to nearest OSHA preset (Safety Green, Caution Amber, Danger Red).
- **Rationale**: Ensures the dashboard looks "rugged" immediately upon first launch after the update without the user needing to manual re-color dozens of widgets.
- **Alternatives**:
  - Hard Reset: Rejected as it would frustrate users who spent time customizing their layouts.
