# Proofs: Task 04 — Implement OwnerService (GREEN)

Covers: AC-2.a, AC-2.b

## Planned evidence

- `OwnerService.java` file listing showing `@Service`, constructor injection of `OwnerRepository`, and `isDuplicate` method body.
- `./mvnw test -Dtest=OwnerServiceTests` passing output confirming both `testIsDuplicate_returnsTrueWhenMatchExists` and `testIsDuplicate_returnsFalseWhenNoMatch` pass (GREEN phase).

## Completion notes

### AC-2.a and AC-2.b: `OwnerService.java` created

File: `src/main/java/org/springframework/samples/petclinic/owner/OwnerService.java`

```java
@Service
public class OwnerService {

    private final OwnerRepository owners;

    public OwnerService(OwnerRepository owners) {
        this.owners = owners;
    }

    public boolean isDuplicate(String firstName, String lastName, String telephone) {
        return owners.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(firstName, lastName, telephone);
    }
}
```

### GREEN phase: `./mvnw test -Dtest=OwnerServiceTests`

```text
[INFO] Running org.springframework.samples.petclinic.owner.OwnerServiceTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.800 s -- in org.springframework.samples.petclinic.owner.OwnerServiceTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  5.158 s
```

Both `testIsDuplicate_returnsTrueWhenMatchExists` and `testIsDuplicate_returnsFalseWhenNoMatch` pass. GREEN phase confirmed.
