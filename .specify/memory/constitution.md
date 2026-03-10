<!--
SYNC IMPACT REPORT
- Version change: 0.0.0 → 1.0.0
- List of modified principles:
  - Added: I. Compose-First
  - Added: II. Unidirectional Data Flow
  - Added: III. Test-First & Coverage
  - Added: IV. Accessibility by Default
  - Added: V. Modular Architecture
- Added sections: Architecture Guidelines, Quality Assurance
- Removed sections: None
- Templates requiring updates (✅ updated / ⚠ pending):
  - ✅ .specify/templates/plan-template.md
  - ✅ .specify/templates/spec-template.md
  - ✅ .specify/templates/tasks-template.md
- Follow-up TODOs: TODO(RATIFICATION_DATE): Needs confirmation from team on exact start date of project.
-->

# android-ui Constitution

## Core Principles

### I. Compose-First
All new UI components MUST be built using Jetpack Compose. Legacy XML layouts or views SHOULD be migrated to Compose when materially modified.
Rationale: Jetpack Compose is the modern toolkit for Android UI, offering improved developer velocity, fewer bugs, and better state management.

### II. Unidirectional Data Flow
State MUST flow down from ViewModels to UI components, and events MUST flow up from UI components to ViewModels. UI components MUST NOT mutate state directly.
Rationale: Unidirectional Data Flow ensures predictability, makes state mutations traceable, and vastly simplifies UI testing.

### III. Test-First & Coverage
Business logic and ViewModels MUST be unit tested. Critical UI components and screens MUST have screenshot or UI tests. Code changes MUST NOT decrease overall test coverage.
Rationale: Rigorous testing catches regressions early and provides confidence when refactoring.

### IV. Accessibility by Default
All UI components MUST define appropriate content descriptions, minimum touch target sizes (48dp x 48dp), and support dynamic text sizing.
Rationale: Inclusivity is a baseline requirement, not a feature. Android applications must be usable by everyone.

### V. Modular Architecture
Features MUST be encapsulated within their own Gradle modules. Modules MUST NOT have circular dependencies and SHOULD depend on a common core module only when necessary.
Rationale: Modularity improves build times, enforces strict boundaries, and enables parallel development.

## Architecture Guidelines

- **UI Layer**: Jetpack Compose, ViewModels (AAC), StateFlow/SharedFlow.
- **Domain Layer**: Optional, use cases for complex business logic.
- **Data Layer**: Repositories, Retrofit for network, Room for local storage.

## Quality Assurance

- **Code Review**: All pull requests MUST require at least one approving review before merging.
- **CI/CD**: The continuous integration pipeline MUST pass (tests, linting, ktlint) before merging.
- **Static Analysis**: Projects MUST use Detekt and Ktlint to enforce code style and identify potential bugs.

## Governance

Amendments to this constitution require a pull request with justification and approval from the core maintainers. Any changes MUST be reflected by bumping the semantic version. All new features and PRs MUST verify compliance with the active constitution.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): Unknown initial date | **Last Amended**: 2026-03-10