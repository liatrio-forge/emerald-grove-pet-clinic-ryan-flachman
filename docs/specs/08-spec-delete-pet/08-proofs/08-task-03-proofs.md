# Proofs: Task 03 — Add Delete trigger and confirmation modal to ownerDetails.html

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- `grep -n "data-pet-name"` output in `ownerDetails.html` — at least one match.
- `grep -n "data-visit-count"` output in `ownerDetails.html` — at least one match.
- `grep -n "deletePetModal"` output in `ownerDetails.html` — at least one match.
- `grep -n "Delete anyway\|confirmDeleteBtn"` output — at least one match.
- `grep -n "deleteForm\|/delete"` output in `ownerDetails.html` — at least one match.
- `./mvnw test` output confirming no regressions after template change
  (`BUILD SUCCESS`).

## Completion notes

### AC-1.a: `grep -n "data-pet-name" src/main/resources/templates/owners/ownerDetails.html`

```text
79:                 th:data-pet-name="${pet.name}"
```

### AC-1.b: `grep -n "data-visit-count" src/main/resources/templates/owners/ownerDetails.html`

```text
80:                 th:data-visit-count="${pet.visits.size()}"
```

### AC-2.a: `grep -n "deletePetModal" src/main/resources/templates/owners/ownerDetails.html`

```text
91:  <div id="deletePetModal" style="display:none; position:fixed; top:0; left:0;
100:                onclick="document.getElementById('deletePetModal').style.display='none'">
134:        document.getElementById('deletePetModal').style.display = 'block';
```

### AC-2.b: `grep -n "Delete anyway\|confirmDeleteBtn" src/main/resources/templates/owners/ownerDetails.html`

```text
103:        <button type="submit" id="confirmDeleteBtn" th:text="#{deletePet}">Delete</button>
120:        var confirmBtn = document.getElementById('confirmDeleteBtn');
125:          confirmBtn.textContent  = 'Delete anyway';
```

### AC-2.c: `grep -n "deleteForm\|/delete" src/main/resources/templates/owners/ownerDetails.html`

```text
98:      <form id="deleteForm" method="post" action="">
131:        document.getElementById('deleteForm').action =
132:          '/owners/' + ownerId + '/pets/' + petId + '/delete';
```

### Full test suite: `./mvnw test`

```text
[INFO] Tests run: 78, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
```

### Notes

- The `I18nPropertiesSyncTest.checkNonInternationalizedStrings` test was failing
  because hardcoded strings ("Delete", "This cannot be undone.") were present
  in the template. Fixed by:
  1. Adding `deletePet=Delete` and `delete.cannotUndo=This cannot be undone.`
     to `messages.properties` and all non-en language files.
  2. Using `th:text="#{deletePet}"` and `th:text="#{delete.cannotUndo}"` in
     the template (trigger link, modal heading span, paragraph, confirm button).
  - The spec lists i18n as out of scope for translation; English text is used as
    the fallback value in all language files.
- Spring Security is not on the classpath; the CSRF hidden input was omitted
  from the delete form as directed by the spec's Note.
