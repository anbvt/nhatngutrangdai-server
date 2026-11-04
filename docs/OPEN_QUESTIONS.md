# Phase 4 — Enrollment decisions still required

- Define valid transitions among `PENDING`, `APPROVED`, `CONFIRMED`, `COMPLETED`, `REJECTED`, `WAITLISTED`, and `CANCELLED`, including whether transitions can be repeated or reversed. Until confirmed, applications stay `PENDING` and the admin status-transition API is not exposed.
- Define secure public reference lookup verification and rate limiting. Submission returns a one-time, high-entropy reference; only its SHA-256 hash is stored. No public lookup endpoint is exposed yet.
- Define class-assignment eligibility: allowed enrollment status, whether only `ACTIVE` classrooms can be selected, date eligibility, and capacity enforcement.
- Define when/how an application becomes a Student profile and whether/how a Student login account is created. Current implementation does not automatically create or link either.
- Confirm whether public applicants need a public course catalog endpoint and whether applications can target inactive courses. Current submission validates that the course exists but does not restrict its status.
- Rate limiting and abuse protection for public application submission remains to be implemented before production exposure.