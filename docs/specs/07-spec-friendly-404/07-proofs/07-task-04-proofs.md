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

### AC-3.a: `error/404.html` exists

```text
$ find src/main/resources/templates/error -name "404.html"
src/main/resources/templates/error/404.html
```

### AC-3.b: `error/404.html` contains a link to `/owners`

```text
$ grep -n 'href.*\/owners\|th:href.*owners' src/main/resources/templates/error/404.html
12:      <a th:href="@{/owners/find}" th:text="#{findOwners}" class="btn btn-primary mt-3">Find Owners</a>
```

### AC-3.c: `error/404.html` contains a human-readable not-found message

```text
$ grep -in "not found\|could not be found" src/main/resources/templates/error/404.html
10:      <h2 th:text="#{error.404.heading}">Page Not Found</h2>
11:      <p th:text="#{error.404.body}">The requested resource could not be found.</p>
```

### AC-4.a: `error.html` does not render `${message}` / `${error}` / `${exception}`

```text
$ grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/main/resources/templates/error.html
(no output — zero matches)
```

### No regressions — full suite passes

```text
$ ./mvnw test

[INFO] Tests run: 73, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Notes

- `I18nPropertiesSyncTest` required adding `error.404.heading` and `error.404.body` keys to all locale files (i18n
  internationalisation of 404 text is out of scope per spec, so English values used for all locales).
- `CrashControllerIntegrationTests.testTriggerExceptionHtml` previously asserted that the exception message was
  visible in the HTML error page. That assertion was removed as it directly conflicts with AC-4.a
  (removing the exception message from `error.html` is the spec's explicit requirement). The test now asserts
  that the exception message does NOT appear in the response, and that the branded error page (not Whitelabel) is shown.
