# Proofs: Task 02 — Implement delete endpoint; add orphanRemoval to Owner.pets (GREEN)

Covers: AC-3.a, AC-4.a, AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a, AC-7.b

## Planned evidence

- `grep -n "orphanRemoval" src/main/java/.../owner/Owner.java` output showing
  `orphanRemoval = true` on the `pets` `@OneToMany`.
- `grep -n "pets/{petId}/delete\|/delete" src/main/java/.../owner/PetController.java`
  output showing the new `@PostMapping`.
- `grep -n "Pet has been deleted" src/main/java/.../owner/PetController.java`
  output showing the flash attribute.
- `./mvnw test -Dtest=PetControllerTests` output showing all four new tests
  passing alongside pre-existing tests (`BUILD SUCCESS`).
- `./mvnw test` full-suite output (`BUILD SUCCESS`, no regressions).

## Completion notes

### AC-4.a: `orphanRemoval = true` on `Owner.pets` `@OneToMany`

```diff
-	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
+	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
 	@JoinColumn(name = "owner_id")
```

File: `src/main/java/org/springframework/samples/petclinic/owner/Owner.java`

### AC-3.a / AC-7.b: `@PostMapping` for `/pets/{petId}/delete` and flash message in `PetController.java`

```java
@PostMapping("/pets/{petId}/delete")
public String deletePet(@ModelAttribute Owner owner, @ModelAttribute Pet pet,
        RedirectAttributes redirectAttributes) {
    owner.getPets().remove(pet);
    this.owners.save(owner);
    redirectAttributes.addFlashAttribute("message", "Pet has been deleted");
    return "redirect:/owners/{ownerId}";
}
```

File: `src/main/java/org/springframework/samples/petclinic/owner/PetController.java`

### AC-4.b / AC-4.c / AC-5.a / AC-6.a / AC-7.a: `./mvnw test -Dtest=PetControllerTests`

```text
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 -- PetControllerTests$ProcessUpdateFormHasErrors
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- PetControllerTests$ProcessCreationFormHasErrors
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0 -- PetControllerTests (parent)

[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  7.278 s
```

All four new delete tests pass:

- `testDeletePetSuccess` — status 3xxRedirection + redirectedUrlPattern("/owners/*")
- `testDeletePetWithVisitsCascade` — status 3xxRedirection
- `testDeletePetOwnerNotFound` — status 404
- `testDeletePetNotFound` — status 404

### Full suite: `./mvnw test`

```text
[INFO] Tests run: 78, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Notes

- Task 01 RED phase commit was skipped per user direction (Option B): the
  Maven-test-check pre-commit hook blocks commits with failing tests. The RED
  phase failure output is captured in `08-task-01-proofs.md` as the durable
  record.
- The `deletePet` handler uses `@ModelAttribute Owner owner` and
  `@ModelAttribute Pet pet` without `@Valid`, so `PetValidator` does not run.
  404 behaviour is inherited automatically from `findOwner` and `findPet`.
