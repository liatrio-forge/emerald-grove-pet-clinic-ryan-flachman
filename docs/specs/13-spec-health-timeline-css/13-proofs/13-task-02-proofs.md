# Proofs: Task 02 — Add health-timeline CSS classes to `petclinic.scss`

Covers: AC-1.a, AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-5.b

## Planned evidence

- Output of each `grep -n` command showing matching lines in `petclinic.scss`
  for all five class names and the `@keyframes` block.
- The actual SCSS block added (file excerpt).

## Completion notes

### AC-1.a: `.urgency-routine` in `petclinic.scss`

```text
$ grep -n '\.urgency-routine' src/main/scss/petclinic.scss
454:.urgency-routine,
466:.urgency-routine {
```

Exit 0 — two matches (shared selector group and individual rule).

### AC-2.a: `.urgency-monitor` in `petclinic.scss`

```text
$ grep -n '\.urgency-monitor' src/main/scss/petclinic.scss
455:.urgency-monitor,
471:.urgency-monitor {
```

Exit 0 — two matches.

### AC-3.a: `.urgency-urgent` in `petclinic.scss`

```text
$ grep -n '\.urgency-urgent' src/main/scss/petclinic.scss
456:.urgency-urgent {
476:.urgency-urgent {
```

Exit 0 — two matches (shared group and individual rule).

### AC-4.a: `.health-tag` in `petclinic.scss`

```text
$ grep -n '\.health-tag' src/main/scss/petclinic.scss
482:.health-tag {
```

Exit 0.

### AC-5.a: `@keyframes ai-spinner-rotate` in `petclinic.scss`

```text
$ grep -n '@keyframes ai-spinner-rotate' src/main/scss/petclinic.scss
494:@keyframes ai-spinner-rotate {
```

Exit 0.

### AC-5.b: `.ai-spinner` in `petclinic.scss`

```text
$ grep -n '\.ai-spinner' src/main/scss/petclinic.scss
499:.ai-spinner {
```

Exit 0.

### SCSS block added

New block inserted before the three `@import` lines at end of `petclinic.scss`:

```scss
// Health timeline — urgency badges
.urgency-routine,
.urgency-monitor,
.urgency-urgent {
  display: inline-block;
  padding: 0.25em 0.6em;
  font-size: 0.75em;
  font-weight: 600;
  line-height: 1;
  border-radius: 9999px;
  white-space: nowrap;
}

.urgency-routine {
  background-color: $spring-green;
  color: $spring-brown;
}

.urgency-monitor {
  background-color: #ffc107;
  color: #111111;
}

.urgency-urgent {
  background-color: #dc3545;
  color: #f8f9fa;
}

// Health timeline — tag chip
.health-tag {
  display: inline-block;
  padding: 0.2em 0.55em;
  font-size: 0.75em;
  border: 1px solid $spring-green;
  border-radius: 9999px;
  color: $spring-green;
  background-color: transparent;
  white-space: nowrap;
}

// Health timeline — loading spinner
@keyframes ai-spinner-rotate {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
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
