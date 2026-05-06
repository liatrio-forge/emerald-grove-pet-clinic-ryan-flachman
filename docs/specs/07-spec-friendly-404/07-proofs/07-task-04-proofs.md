# Proofs: Task 04 — Create error/404.html and strip exception message from error.html

Covers: AC-3.a, AC-3.b, AC-3.c, AC-4.a

## Planned evidence

- `find src/main/resources/templates/error -name "404.html"` output showing the
  file path.
- `grep -n 'href.*\/owners\|th:href.*owners' src/.../error/404.html` output
  (≥1 match).
- `grep -in "not found\|could not be found" src/.../error/404.html` output (≥1
  match).
- `grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/.../error.html`
  output (no matches).
- `./mvnw test` output confirming `BUILD SUCCESS` (no regressions from template
  changes).

## Completion notes

(Filled in by `implement-sdd-spec`.)
