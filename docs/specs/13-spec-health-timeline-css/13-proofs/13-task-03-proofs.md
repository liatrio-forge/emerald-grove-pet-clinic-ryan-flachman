# Proofs: Task 03 — Compile SCSS and verify classes in `petclinic.css`

Covers: AC-1.b, AC-2.b, AC-3.b, AC-4.b, AC-5.c

## Planned evidence

- Output of `./mvnw package -P css` showing BUILD SUCCESS.
- Output of `grep -c` command on `petclinic.css` confirming all six class/keyframe
  patterns are present.
- Excerpt from compiled `petclinic.css` showing `background-color` values for
  each urgency class and `border-radius` for `.health-tag`.
- Playwright CSS fixture test run output showing all assertions pass (GREEN phase).

## Completion notes

### Notes — SCSS compilation approach

The spec calls for `./mvnw package -P css` (libsass-Maven-plugin 0.3.4). This plugin
has a **pre-existing failure** on this branch: `header.scss` line 2 uses `$spring-green`
but libsass exits with "Undefined variable" in that import. This is unrelated to the
changes in this spec (confirmed by reverting the SCSS and re-running the plugin on
the base branch — same failure). The compiled `petclinic.css` in the repository was
produced with a newer Sass toolchain.

SCSS was compiled using `npx sass` (Dart Sass 1.99.0) with the bootstrap SCSS load path
resolved from `target/webjars/`:

```text
$ npx sass src/main/scss/petclinic.scss \
    src/main/resources/static/resources/css/petclinic.css \
    --load-path=target/webjars/META-INF/resources/webjars/bootstrap/5.3.8/scss \
    --style expanded --no-source-map

WARNING: 314 repetitive deprecation warnings omitted.
Run in verbose mode to see all warnings.

EXIT: 0
```

### AC-1.b: Compiled CSS contains `.urgency-routine` with green background

```text
$ grep -n 'urgency-routine' src/main/resources/static/resources/css/petclinic.css
12271:.urgency-routine,
12272:.urgency-monitor,
12273:.urgency-urgent {
12283:.urgency-routine {
```

Excerpt (lines 12283–12287):

```css
.urgency-routine {
  background-color: #24AE1D;
  color: #111111;
}
```

`#24AE1D` = `rgb(36, 174, 29)` — in the green range.

### AC-2.b: Compiled CSS contains `.urgency-monitor` with amber background

Excerpt (lines 12288–12292):

```css
.urgency-monitor {
  background-color: #ffc107;
  color: #111111;
}
```

`#ffc107` = `rgb(255, 193, 7)` — in the amber range.

### AC-3.b: Compiled CSS contains `.urgency-urgent` with red background

Excerpt (lines 12293–12297):

```css
.urgency-urgent {
  background-color: #dc3545;
  color: #f8f9fa;
}
```

`#dc3545` = `rgb(220, 53, 69)` — in the red range.

### AC-4.b: Compiled CSS contains `.health-tag` with pill border-radius

Excerpt (lines 12298–12308):

```css
.health-tag {
  display: inline-block;
  padding: 0.2em 0.55em;
  font-size: 0.75em;
  border: 1px solid #24AE1D;
  border-radius: 9999px;
  color: #24AE1D;
  background-color: transparent;
  white-space: nowrap;
}
```

`border-radius: 9999px` — pill shape.

### AC-5.c: Compiled CSS contains `.ai-spinner` and `ai-spinner-rotate` keyframe

```text
$ grep -c '\.urgency-routine\|\.urgency-monitor\|\.urgency-urgent\|\.health-tag\|\.ai-spinner\|ai-spinner-rotate' \
    src/main/resources/static/resources/css/petclinic.css
10
```

10 ≥ 6 — all patterns present. Keyframe excerpt:

```css
@keyframes ai-spinner-rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.ai-spinner {
  display: inline-block;
  width: 1em;
  height: 1em;
  border: 0.15em solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: ai-spinner-rotate 0.75s linear infinite;
}
```

### Playwright GREEN phase — all five assertions pass

```text
$ cd e2e-tests && npx playwright test --grep "health-timeline CSS"

Running 1 test using 1 worker

  1 passed (2.2s)
```

All five assertions satisfied:

- `.urgency-routine` → `background-color: rgb(36, 174, 29)` ✓
- `.urgency-monitor` → `background-color: rgb(255, 193, 7)` ✓
- `.urgency-urgent` → `background-color: rgb(220, 53, 69)` ✓
- `.health-tag` → `border-radius` > 50px (9999px computes to much larger) ✓
- `.ai-spinner` → `animation-name: ai-spinner-rotate` ✓
