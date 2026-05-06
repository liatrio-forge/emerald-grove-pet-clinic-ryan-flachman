# Proofs: Task 03 — Update `ownersList.html` pagination links to thread filter params

Covers: AC-2.a, AC-2.b

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing all AC-1 and AC-2 tests PASSING
- Diff of `ownersList.html` showing the five updated `th:href` expressions

## Completion notes

### Implementation approach — deviation from task bullets

The task spec says to use `@{/owners(page=${i},lastName=${filterLastName},...)}` relying on
Thymeleaf's null-omission for URL params. Empirical testing with Thymeleaf 3.1.3 (Spring Boot 4.0)
showed that null model attributes **are** included as empty params (`?lastName=`), not omitted.
The vetList.html reference uses ternary expressions to work around this. For three params,
a ternary chain would require 2³ = 8 URL patterns, which the spec explicitly prohibits.

Instead, `addPaginationModel` now pre-builds a URL-encoded `filterQuery` string in the
controller (e.g., `"&lastName=Davis"` or `""` when no filter). The template appends this string
to the base page URL. Null values never reach the Thymeleaf URL engine — AC-2.b is satisfied
without ternary chains.

### AC-2.a: Rendered HTML with active `lastName=Davis` contains hrefs with `lastName=Davis`

The test `testPaginationLinksIncludeActiveLastNameFilter` stubs `findBySearchCriteria` to return
10 owners (2 pages), performs `GET /owners?lastName=Davis&page=1`, and asserts:

```java
.andExpect(content().string(containsString("page=2&amp;lastName=Davis")));
```

Rendered pagination link:

```html
<a href="/owners?page=2&amp;lastName=Davis">2</a>
```

`&` is HTML-escaped to `&amp;` by Thymeleaf's attribute writer. ✓

### AC-2.b: No-filter pagination hrefs contain only `page=N`

The test `testPaginationLinksOmitEmptyFiltersWhenNoFilterActive` stubs a 10-owner result
with no filter, performs `GET /owners?page=1`, and asserts:

```java
.andExpect(content().string(not(containsString("lastName="))))
.andExpect(content().string(not(containsString("telephone="))))
.andExpect(content().string(not(containsString("city="))));
```

Rendered pagination link (no filter):

```html
<a href="/owners?page=2">2</a>
```

No filter params present. ✓

### Template diff — `ownersList.html`

```diff
-    <span th:each="i: ${#numbers.sequence(1, totalPages)}">
-      <a th:if="${currentPage != i}" th:href="@{'/owners?page=' + ${i}}">[[${i}]]</a>
+      <a th:if="${currentPage != i}" th:href="@{'/owners?page=' + ${i} + ${filterQuery}}">[[${i}]]</a>

-      <a th:if="${currentPage > 1}" th:href="@{'/owners?page=1'}" ...></a>
+      <a th:if="${currentPage > 1}" th:href="@{'/owners?page=1' + ${filterQuery}}" ...></a>

-      <a th:if="${currentPage > 1}" th:href="@{'/owners?page=__${currentPage - 1}__'}" ...></a>
+      <a th:if="${currentPage > 1}" th:href="@{'/owners?page=' + ${currentPage - 1} + ${filterQuery}}" ...></a>

-      <a th:if="${currentPage < totalPages}" th:href="@{'/owners?page=__${currentPage + 1}__'}" ...></a>
+      <a th:if="${currentPage < totalPages}" th:href="@{'/owners?page=' + ${currentPage + 1} + ${filterQuery}}" ...></a>

-      <a th:if="${currentPage < totalPages}" th:href="@{'/owners?page=__${totalPages}__'}" ...></a>
+      <a th:if="${currentPage < totalPages}" th:href="@{'/owners?page=' + ${totalPages} + ${filterQuery}}" ...></a>
```

### Full test pass — AC-1 + AC-2

```text
$ ./mvnw test -Dtest=OwnerControllerTests

[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.967 s
[INFO] BUILD SUCCESS
```

Full suite:

```text
$ ./mvnw test

[WARNING] Tests run: 94, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
[INFO] Total time: 13.788 s
```
