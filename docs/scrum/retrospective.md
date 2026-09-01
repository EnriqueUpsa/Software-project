# Retrospective

*Written during the post-review remediation, 30 August – 8 September 2026, after the February
review of the project.*

This is an honest account of what worked and what did not. It is dated: the sprint history and
the acceptance criteria of the earlier user stories were written into Taiga and into this
repository during the remediation, not during the sprints they describe. That gap is itself
one of the findings below.

## What went wrong

**The topic was not agreed with the Product Owner before starting.** The first delivery, in
January, was a messaging application that had never been discussed. It was rejected, and
correctly so: an increment nobody asked for has no value however well it is built. The Pet
Shelter project only started after the requirements were agreed.

**The board was not the single source of truth.** Taiga held the sprints, the user stories and
the tasks, but the user stories carried no acceptance criteria, so there was no shared
definition of done. Whether a story was finished was decided in conversation instead of
against a written criterion.

**The work inside each sprint was concentrated in one or two days.** Every sprint delivered its
increment, but 29 of the 52 commits of the project landed on a single day. A sprint that is
executed in one sitting is a deadline, not an iteration: there is no room to inspect and adapt
half way through, which is the point of the ceremony.

**The meetings were irregular and the communication was fragmented.** Sessions that had been
accepted were missed, the two team members wrote separately instead of keeping each other in
copy, and the conversations focused on problems and dates rather than on the increment.

**The test suite was not being executed.** The JUnit sources lived in `src/test`, outside the
directory Maven compiles, so `mvn test` ran zero tests while 55 of them sat in the repository.
The continuous integration workflow passed green and proved nothing. A test that is not run is
not a test.

**The database had no referential integrity.** Each DAO created its own table when it was
constructed, so the creation order made foreign keys impossible: `adoptions` and
`health_records` pointed at animals and adopters with plain text columns and nothing enforced
that the referenced rows existed.

## What was done about it

| Finding | Correction |
|---|---|
| Tests not executed | Sources moved to `src/test/java`; `mvn test` now compiles and runs the whole suite |
| No referential integrity | `SchemaInitializer` creates the schema in dependency order and declares four foreign keys, a check constraint and seven indexes |
| Persistence untested | The JDBC layer is now covered against a real in-memory database, not against doubles |
| Matching not guided | `findCandidatesFor` proposes only the animals an adopter can actually take home |
| No definition of done | Every user story now carries acceptance criteria in Taiga |
| Process undocumented | The sprint history, the burndown and this retrospective live in the repository |

The suite went from 55 tests that never ran to 108 tests that run on every build.

## What to keep

The architecture held up. The decision taken in Sprint 1 — services depending on DAO
interfaces rather than on JDBC — is what made it possible to add the whole test suite in the
remediation without touching the business logic, and to run the tests against an in-memory
database and against H2 with the same code. Layering that is respected from the first
increment pays for itself later.

The transactional design also held up. The adoption writes the placement, the animal status
and the audit entry inside one transaction, and the rollback path was covered by tests from
the start.

## What we would do differently

Agree the scope with the Product Owner before writing any code. Write the acceptance criteria
when the story is created, not afterwards. Commit every day of the sprint, so the burndown
reflects the work instead of a deadline. And keep both team members in copy on every message,
which sounds trivial and was the request the Product Owner had to repeat most often.
