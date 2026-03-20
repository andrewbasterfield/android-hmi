# Design System Specification: Industrial Precision HMI

## 1. Overview & Creative North Star
**Creative North Star: The Kinetic Cockpit**

This design system is engineered for high-stakes, mission-critical environments. It rejects the "softness" of modern consumer web design in favor of **Rugged Functionalism**. Inspired by aircraft glass cockpits and military-grade PLC (Programmable Logic Controller) interfaces, the system prioritizes "Information Density at a Glance."

We break the standard "template" look by utilizing **intentional asymmetry** and **brutalist structural blocks**. Instead of fluid, airy layouts, we use a rigid, modular grid that mimics physical hardware rack-mounting. Every element must feel like a physical toggle or a machined component—deliberate, heavy, and reliable.

## 2. Colors
The palette is rooted in deep obsidian tones to preserve operator night vision and reduce eye strain in dimly lit industrial bays.

### The "No-Line" Rule & Surface Hierarchy
Traditional 1px hairline borders are strictly prohibited for sectioning. They disappear under heavy vibration or low-resolution industrial displays. 
- **Containment:** Define boundaries through raw color shifts. A `surface-container-low` (#1c1b1b) module should sit directly on a `surface` (#131313) background.
- **Nesting:** To create depth, stack tiers. An inner data readout uses `surface-container-high` (#2a2a2a) to "push" the information toward the operator.
- **The Functional Gradient:** While ornamentation is forbidden, subtle gradients are permitted *only* to indicate physical depth on interactive touch targets. Use a transition from `primary` (#ebffe2) to `primary-container` (#00ff41) for high-priority "ACTIVATE" states to simulate a backlit physical button.

### Key Tokens:
*   **Background:** `#131313` (The Void)
*   **Status Green (Safe):** `primary_fixed_dim` (#00e639)
*   **Status Amber (Caution):** `secondary_container` (#feaa00)
*   **Status Red (Emergency):** `error_container` (#93000a)

## 3. Typography
The system utilizes a dual-font strategy to balance rapid data legibility with authoritative signaling.

*   **Display & Headlines (Space Grotesk):** Used for critical telemetry and system headers. Its geometric, wide stance mimics "stenciled" military marking. 
*   **Body & Labels (Inter):** A high-legibility sans-serif used for technical descriptions and tabular data. 

**Hierarchy as Brand:**
*   **Headline-LG (2rem):** Used for active machine states (e.g., "ENGINE I: ACTIVE").
*   **Label-MD (0.75rem):** Used for units of measure (e.g., "RPM", "PSI"). These should always be uppercase to reinforce the industrial aesthetic.

## 4. Elevation & Depth
In this system, "Up" means "Ready to Touch." We do not use traditional soft drop shadows.

*   **The Layering Principle:** Depth is binary. Level 0 is the `surface`. Level 1 is the `surface-container-low` (the "Panel"). Level 2 is the component itself.
*   **Thick Borders (The Structural Framework):** Instead of shadows, use the `outline` (#84967e) or `outline_variant` (#3b4b37) at a **2px minimum width** to define interactive zones. This mimics the protective "bezel" of ruggedized tablets.
*   **The "Ghost Border" Fallback:** For non-interactive data groupings, use the `outline-variant` at 20% opacity. 
*   **Haptic Glass:** Use `backdrop-blur` (12px) on top-level warning modals using a semi-transparent `error_container`. This ensures the operator sees the "emergency" without losing the context of the underlying system state.

## 5. Components

### Buttons (Tactile Targets)
*   **Primary:** Solid `primary_fixed_dim` (#00e639) background with `on_primary_fixed` (#002203) text. No rounded corners (`0px`). Minimum touch target: 64px height for gloved use.
*   **Secondary/Toggle:** 2px stroke of `outline`. On `active` state, fill with `secondary_container`.
*   **States:** `Pressed` states must invert the color scheme (Inverse Primary) to provide immediate visual confirmation of a mechanical-style "click."

### Inputs & Fields
*   **Industrial Input:** Background `surface_container_highest`. 4px bottom-border of `outline` to denote the "shelf" where data is entered.
*   **Error State:** Border shifts to `error` (#ffb4ab). Text flashes or utilizes a high-contrast `error_container` block.

### Cards & Telemetry Blocks
*   **Forbid Dividers:** Do not use lines to separate data points. Use `spacing-6` (2rem) or a shift to `surface_container_low`.
*   **The "Readout" Card:** A `surface_container_highest` block with a 4px left-accent bar in `primary` to indicate the "system is healthy."

### Critical Alerts (The HUD Overlay)
*   Full-screen peripheral "glow" using a `tertiary_container` (#ffd3c8) gradient at 10% opacity, pulsing at 1Hz for non-critical warnings, and 2Hz for critical failures.

## 6. Do’s and Don’ts

### Do:
*   **Use 0px Border Radius:** Everything is a hard edge. It communicates rigidity and precision.
*   **Use Monospaced Alignment:** Align decimal points in telemetry data so values don't "jump" when changing.
*   **Prioritize Gloved Touch:** Ensure all interactive elements have at least `spacing-4` (1.4rem) of dead space between them to prevent accidental triggers.

### Don’t:
*   **Don't use "Soft" Colors:** Avoid pastels or muted greys. If it’s not functional, it’s black.
*   **Don't use Centered Text:** In industrial contexts, left-aligned or "justified-block" styles feel more like technical manuals and are easier to scan.
*   **Don't hide info in tooltips:** If the data is important enough for a PLC, it should be on the screen. Hover states do not exist for gloved hands on a touchscreen.