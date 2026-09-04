# Pet Shelter and Adoption Management System

**Project 11 — Software Engineering 2025/2026 — University of Cassino and Southern Lazio**
Authors: Pablo Verdejo Alonso · Enrique García Bello

Desktop JavaFX application that covers the full life cycle of a sheltered animal: intake
registration, health and nutrition tracking, the guided adoption workflow, shelter space
logistics with capacity control, dashboard metrics and a complete audit history.

---

## 1. Requirements

| Tool | Version | Notes |
|---|---|---|
| JDK | 21 | `java -version` must report 21.x |
| Maven | 3.8+ | `mvn -v` |
| H2 Database | 2.2.224 | resolved automatically by Maven, no server needed |

JavaFX 21 is resolved by Maven with the platform-specific classifier for the machine that
builds the project, so no manual JavaFX SDK installation is required.

## 2. Build, test and run

All commands are run **from the project root** (the folder containing `pom.xml`).

```bash
# compile
mvn clean compile

# run the full JUnit 5 suite (123 tests)
mvn test

# launch the application
mvn javafx:run

# generate the Javadoc under target/site/apidocs
mvn javadoc:javadoc
```

Convenience wrappers are also provided for machines without Maven on the PATH:
`bash run_tests.sh` and `bash run_app.sh`.

## 3. Database

The backend is a **relational H2 database in file mode**. No installation or server is
needed: the file is created on first launch.

- JDBC URL: `jdbc:h2:file:./data/petshelter;DB_CLOSE_ON_EXIT=FALSE`
- User `sa`, empty password
- Physical file: `data/petshelter.mv.db` (relative to the working directory)

The schema is created automatically at start-up by each DAO (`ensureTable()`), and is
documented in full — keys, foreign keys, check constraints and indexes — in
[`db/schema.sql`](db/schema.sql). To start from a clean database, delete the `data/`
folder and relaunch.

### Entity–relationship overview

```
adopters ──1───┐
               ├──< adoptions >──┐
animals ───1───┘                 │
   │                             │
   ├──< health_records           │
   └──< status_change_log        │
kennels (capacity control)  ─────┘
```

- `animals` — core registry, primary key `microchip_id` (natural business key)
- `adopters` — potential adopters with species/breed preferences
- `adoptions` — placement of one animal with one adopter (FK to both)
- `health_records` — veterinary history, N:1 with `animals`
- `status_change_log` — append-only audit trail of every lifecycle transition
- `kennels` — physical spaces with `max_capacity` / `occupied` control

## 4. Architecture

**Pattern: MVC + DAO over JDBC**, with a service layer holding the business rules.

```
src/main/java/
├── model/       entities and enums (Animal, Dog, Cat, Adopter, Adoption,
│                HealthRecord, Kennel, StatusChangeLog)
├── dao/         DAO contracts + two implementations per contract:
│                Jdbc*DAO (production) and InMemory*DAO (used by the tests)
├── service/     business rules, validation, transactions and logging
├── controller/  use-case controllers, one per functional area
├── ui/view/     JavaFX views, one tab per functional area
├── ui/          AppContext (dependency wiring) and ShelterManagementApp (bootstrap)
└── util/        LoggerConfig (structured logging to logs/shelter.log)
```

The DAO interfaces are what make the layers independent: the services depend on the
contract, never on JDBC, which is why the whole business layer is unit-testable without a
database.

### Object-oriented design

- **Abstract class** — `Animal`, with the shared state and the lifecycle status.
- **Inheritance / polymorphism** — `Dog` and `Cat` extend `Animal`; the services work
  against `Animal` and never switch on the concrete type.
- **Interfaces** — every DAO (`AnimalDAO`, `AdoptionDAO`, `HealthRecordDAO`, `KennelDAO`,
  `AdopterDAO`, `StatusChangeLogDAO`) is an interface with a JDBC and an in-memory
  implementation.
- **Encapsulation** — entity state is private with validation in the setters/constructors.

### Transactions

`AdoptionService.processAdoption` is the critical path: inserting the adoption, updating
the animal status and writing the audit entry happen inside a **single JDBC transaction**.
If any step fails the whole operation is rolled back — this is covered by unit tests using
failing DAO doubles.

### Animal registry

The intake tab lists the animals of the shelter, newest intake first, with their
species, breed, age, health and lifecycle status. Selecting a row copies its microchip
into the status form, so no identifier has to be typed from memory. The species column
calls the abstract `getSpecies()` of `Animal`, so the table never tests whether the
object is a `Dog` or a `Cat`.

### Dashboard

The dashboard reads three indicators through `DashboardController` and paints them with the
JavaFX chart API: a bar chart with the distribution of the animals over the lifecycle
statuses, a bar chart with the adoptions closed in each month of the current year, and a
counter of the veterinary deadlines that fall inside the next 48 hours, highlighted in red
while it is not zero. Every module refreshes it after changing data.

## 5. Logging

Structured logging is configured in `util/LoggerConfig` and written to
`logs/shelter.log`: intakes, exits, changes to medical therapies, kennel occupancy
changes, adoption processing and every database error.

## 6. Testing

JUnit 5, **123 test methods** across 21 classes in `src/test/java`:

| Area | Class | Covers |
|---|---|---|
| Model | `AnimalProfileTest`, `KennelTest` | entity invariants, capacity rules |
| Schema | `SchemaIntegrityTest`, `SchemaUpgradeTest` | tables, foreign keys, check constraint, indexes |
| DAO | `JdbcAnimalDAOTest`, `JdbcAdopterDAOTest`, `JdbcHealthRecordDAOTest`, `JdbcAdoptionDAOTest`, `JdbcKennelDAOTest`, `JdbcStatusChangeLogDAOTest`, `InMemoryKennelDAOTest` | persistence contract on a real H2 database |
| Service | `AnimalServiceTest`, `HealthServiceTest`, `AdopterServiceTest`, `AdoptionServiceTest`, `AdoptionMatchingTest`, `KennelServiceTest`, `StatisticsServiceTest` | validation, vaccine deadlines, adoption eligibility, guided matching, transaction rollback, statistics |
| Controller | `DashboardControllerTest`, `IntakeControllerTest` | the indicators the dashboard charts are painted from, the registry listing and its order |
| Start-up | `DemoDataSeederTest` | the demo shelter loaded on an empty database |

Edge cases are exercised with hand-written test doubles (`FailingAdoptionDAO`,
`FailingStatusChangeLogDAO`, `TrackingAnimalDAO`, `FakeConnection`) that simulate database
failures, so the rollback and error-handling paths are actually executed.

Run them with `mvn test`.

### Coverage

Coverage is measured with **JaCoCo**, on demand, so that the normal build and the continuous
integration job stay exactly as they are:

```bash
mvn test -Pcoverage
```

The report is written to `target/site/jacoco/index.html`. The JavaFX views are left out of the
measurement: they cannot be exercised without starting the toolkit, and counting them would hide
the coverage of the code that is actually tested — the model, the DAO layer, the services and
the controllers.

## 7. User documentation

[`docs/user-guide.md`](docs/user-guide.md) is the guide for the reception desk: how to start the
application and how to use each of the six modules, with a screenshot of every screen, and a
table of the messages the application answers with when it refuses an operation.

## 8. Project management

The project was developed with SCRUM in **4 sprints**, managed on
[Taiga](https://tree.taiga.io/project/pablova02-stockmaster-inventory) (backlog, user stories
with acceptance criteria, sprint planning, review and retrospective) and versioned on GitHub.

The process documentation is in [`docs/scrum/`](docs/scrum/):

- [`sprint-history.md`](docs/scrum/sprint-history.md) — the four sprints, their goals, user stories and increments
- [`burndown.md`](docs/scrum/burndown.md) — release burndown and commit distribution
- [`retrospective.md`](docs/scrum/retrospective.md) — what went wrong, what was corrected and what to keep

| Sprint | Focus | Main deliverable |
|---|---|---|
| 1 | Domain model and persistence contracts | `Animal` hierarchy, DAO interfaces, in-memory persistence |
| 2 | Animal intake and shelter logistics | intake UI, kennel capacity with transactional check, logging |
| 3 | Health tracking and adoption workflow | health records, vaccine deadline alerts, transactional adoption |
| 4 | Dashboard, statistics and consolidation | dashboard charts, statistics services, JDBC layer, test suite |
