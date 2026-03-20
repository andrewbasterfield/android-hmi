# Research: Configurable Gauge Tick Density

**Feature**: `018-gauge-tick-config` | **Status**: Complete
**Input**: Planning Phase 0 research tasks from `plan.md`

## 1. Algorithm Validation (Nice Numbers)

- **Decision**: Retain the current Paul Heckbert algorithm in `ScaleUtils.calculateNiceStep`.
- **Rationale**: Manual testing of the formula with extreme values (Target 2 and Target 20) confirms it correctly adjusts the step magnitude to maintain logical increments (1, 2, 5, 10 bases) while attempting to meet the density target.
- **Findings**: 
    - A target of 20 on a 0-100 scale correctly results in 5-unit increments (21 total ticks).
    - A target of 2 on a 0-100 scale correctly results in 50-unit increments (3 total ticks).
- **Alternatives Considered**: Fixed step selection (Rejected as it doesn't scale with arbitrary user-defined ranges).

## 2. UI Placement & Scale Assistance

- **Decision**: Implement the "Scale Assistance" readout as an information sub-label directly under the "Tick Density" slider.
- **Rationale**: Provides immediate, low-friction feedback without requiring a separate tooltip or dialog state.
- **Formatting**: `Outcome: ~[CalculatedCount] Ticks (Steps of [IncrementValue])`
- **Alternatives Considered**: Dynamic slider tooltips (Rejected due to complexity in standard Compose Material 3 Slider implementation).
