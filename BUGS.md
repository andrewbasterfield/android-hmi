# 🐛 App Bugs & Regressions

This file tracks functional and visual bugs identified during development and testing of the HMI application.

## 🔴 Critical / High Priority

- [ ] **BUG-011**: Gauge needle "Strobing" / "Ghosting" artifact.
    - *Symptoms*: During very fast data updates, the needle appears to be in multiple places at once (stroboscopic effect).
    - *Cause*: The `StiffnessMedium` spring allows for an angular velocity that exceeds the display's refresh rate/persistence, causing the eye to see distinct "teleported" positions instead of continuous motion.
    - *Requirement*: Tune the physics or implement a "visual bridge" (e.g., subtle motion blur or velocity capping) to maintain a single, solid needle perception.

## 🟡 Medium Priority

- [ ] **BUG-006**: Typography scale refinement.
    - *Symptoms*: Despite the baseline fix in BUG-002, fonts are still perceived as "tiny" on some devices. Need to audit `fontSizeMultiplier` propagation.

## 🟢 Low Priority / Visual Polish

---

## ✅ Resolved

- [X] **BUG-001**: Resize handles were absent (Refactored into 32x32dp tactical handles).
- [X] **BUG-002**: Initial typography baseline scale (Enforced 16sp/24sp minimums).
- [X] **BUG-003**: Resize handles were non-functional due to gesture conflict.
- [X] **BUG-004**: Font size slider was not working for buttons.
- [X] **BUG-005**: Automatic capitalization was unwanted.
- [X] **BUG-006**: Typography scale refinement (Resize logic verified).
- [X] **BUG-007**: Typography baseline doubling (Default 1.0x is now "Normal").
- [X] **BUG-008**: Widget Configuration Dialog was oversized (Fixed height to wrapContent).
- [X] **BUG-009**: Text color override was not applied to content (Fixed by propagating LocalContentColor and baseContentColor).
- [X] **BUG-010**: Gauge needle lag/jump artifact during fast data updates. (Fixed by switching from 300ms `tween` to responsive mechanical `spring`).
- [ ] **BUG-011**: Gauge needle "Strobing" / "Ghosting" artifact at high velocity.
    - *Status*: Parked. Attempts at visual bridging (ghosts/wedges) were insufficient or introduced unwanted damping. Reverted to fastest spring implementation for responsiveness.
