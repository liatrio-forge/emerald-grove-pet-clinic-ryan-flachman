# Proofs: Task 02 — Extend `OwnerController.addPaginationModel` to pass filter params to model

Covers: AC-1.a, AC-1.b, AC-1.c

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing AC-1 model-attribute tests now PASSING
- AC-2 href tests still FAILING at this point (expected — template not yet updated)

## Completion notes

### AC-1.a: `GET /owners?lastName=Davis` → model has `filterLastName=Davis`, `filterTelephone=null`, `filterCity=null`

Implemented by extending `addPaginationModel` signature:

```java
private String addPaginationModel(int page, Model model, Page<Owner> paginated,
        String lastName, String telephone, String city) {
    // ...
    if (lastName != null) model.addAttribute("filterLastName", lastName);
    if (telephone != null) model.addAttribute("filterTelephone", telephone);
    if (city != null) model.addAttribute("filterCity", city);
    // ...
}
```

Null attributes are NOT added to the model — Thymeleaf 3.1.3 renders
null model attributes as empty URL params (`?param=`) rather than
omitting them. Not adding the attribute lets `${filterX}` evaluate to
null in the template, but a separate `filterQuery` string (see Task 03
notes) is used for URL building to avoid the null-omission issue entirely.

### AC-1.b: `GET /owners?telephone=608` → model has `filterTelephone=608`, others null

Same conditional-add pattern as AC-1.a. `model.getAttribute("filterTelephone")`
returns `"608"`; `model.getAttribute("filterLastName")` returns `null` (absent).

### AC-1.c: `GET /owners?lastName=D&telephone=6&city=M` → all three filter model attributes set

All three non-null values added via the conditional path.

### Test results after Task 02 (before Task 03 template update)

```text
$ ./mvnw test -Dtest=OwnerControllerTests

[INFO] Tests run: 25, Failures: 1, Errors: 0, Skipped: 0

[ERROR]   OwnerControllerTests.testPaginationLinksIncludeActiveLastNameFilter
          — href assertion failed (template not yet updated)
```

AC-1 model-attribute tests now pass. AC-2.a href test remains red until
Task 03 updates `ownersList.html`.

### File changed

`src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`

```diff
-private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
+private String addPaginationModel(int page, Model model, Page<Owner> paginated,
+        String lastName, String telephone, String city) {
     List<Owner> listOwners = paginated.getContent();
     model.addAttribute("currentPage", page);
     model.addAttribute("totalPages", paginated.getTotalPages());
     model.addAttribute("totalItems", paginated.getTotalElements());
     model.addAttribute("listOwners", listOwners);
+    if (lastName != null) model.addAttribute("filterLastName", lastName);
+    if (telephone != null) model.addAttribute("filterTelephone", telephone);
+    if (city != null) model.addAttribute("filterCity", city);
+    // ... filterQuery built below (see Task 03 notes)
     return "owners/ownersList";
 }
```

Call site updated: `return addPaginationModel(safePage, model, ownersResults, lastName, telephone, city);`
