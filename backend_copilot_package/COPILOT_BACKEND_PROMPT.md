# Copilot Agent — Backend Kickoff

You are the senior backend engineer for this Japanese Language School Management System.

## Mandatory reading
Read all of these completely before editing code:
1. `BACKEND_REQUIREMENTS.md`
2. `BACKEND_ARCHITECTURE.md`
3. `DATABASE_DESIGN.md`
4. `API_CONTRACT.md`
5. `SECURITY_AND_AUTH.md`
6. `BACKEND_ROADMAP.md`
7. `README.md`

Inspect the existing repository and configuration. Do not assume it is empty or that its current stack exactly matches these documents.

## Non-negotiable
- Check `git status` first and preserve all uncommitted work.
- Do not reset, clean, force-push, or delete user code.
- Keep existing build tool/framework versions unless a change is justified and approved.
- Follow layered architecture; controllers contain no business logic.
- Never expose JPA entities directly; separate request and response DTOs.
- Validate inputs and enforce business rules in services.
- Backend authorization is authoritative; enforce student data ownership.
- Use versioned migrations; no destructive production schema auto-generation.
- Never store plain-text passwords or commit secrets.
- Do not invent unclear business rules, DTO fields, payment rules, or state transitions. Record them in `docs/OPEN_QUESTIONS.md`.
- Use synthetic data only.
- No real payment gateway integration without explicit approval.
- No teacher login in MVP.

## First task: assessment only
1. Inspect Git status, project tree, Java/Spring versions, build tool, dependencies, database, migrations, security, tests, Docker and CI.
2. Compare current code with the requirements.
3. Create/update:
   - `docs/IMPLEMENTATION_PLAN.md`
   - `docs/OPEN_QUESTIONS.md`
   - `docs/ARCHITECTURE_DECISIONS.md`
4. Report current state, reusable code, gaps/risks, proposed package structure, dependencies to add/change and why, unresolved assumptions, and exact files proposed for modification.

Do not begin large implementation before presenting this assessment.

## Implementation workflow
After review, implement one phase at a time, starting with Phase 1 in `BACKEND_ROADMAP.md`.
For each phase: state scope/files → implement a small complete vertical slice → add migrations → add tests for success/validation/auth/edge cases → run actual project tests/build → fix regressions → report changed files, API behavior, test results and open questions → stop for review.

## Quality
Production-minded, maintainable Java; consistent errors and pagination; transactions around multi-step operations; safe logging/audit; OpenAPI kept current; no fake endpoints or pretend-success placeholders.

Start with repository assessment only. Do not generate the entire backend in one pass.
