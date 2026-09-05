# Sprint history

**Pet Shelter and Adoption Management System** — Project 11, Software Engineering 2025/2026,
University of Cassino and Southern Lazio.
Team: Pablo Verdejo Alonso and Enrique García Bello. Product Owner and Stakeholder: Prof. Mario Molinara.

The project was developed with SCRUM in **four weekly sprints**, managed on
[Taiga](https://tree.taiga.io/project/pablova02-stockmaster-inventory) and versioned on GitHub.
This document is the summary of that process; Taiga holds the live board with the user
stories, their acceptance criteria and their tasks.

| Sprint | Dates | Goal | Points | Commits |
|---|---|---|---|---|
| 1 · Animal Foundation & Initial Registration | 23 – 30 Jan 2026 | Domain model and persistence contracts | 13 | 4 |
| 2 · Health Monitoring & Med-Alert System | 30 Jan – 6 Feb 2026 | Intake, shelter logistics and health records | 10 | 15 |
| 3 · Adopter Matching & Management Dashboard | 6 – 13 Feb 2026 | Adoption workflow and dashboard | 13 | 29 |
| 4 · Audit, Logging & Reliability Testing | 13 – 20 Feb 2026 | Audit trail, logging and test suite | 10 | 3 |
| Post-review remediation | 30 Aug – 8 Sep 2026 | Corrections after the February review | 37 | in progress |

**41 story points** were delivered across the four sprints.

---

## Sprint 1 — Animal Foundation & Initial Registration
*23 – 30 January 2026 · 13 points*

| User story | Points |
|---|---|
| US.01 — As a shelter volunteer, I want to register a new animal with its microchip ID, breed and intake date so that we have a centralized record. | 5 |
| US.02 — As a logistics coordinator, I want to track shelter space occupancy so that we prevent overcrowding. | 8 |

**Delivered.** The abstract `Animal` class with its lifecycle status, the `Dog` and `Cat`
subclasses, and the `AnimalDAO` contract. The first increment established the layering that
the rest of the project relies on: the services talk to a DAO interface, never to JDBC.

## Sprint 2 — Health Monitoring & Med-Alert System
*30 January – 6 February 2026 · 10 points*

| User story | Points |
|---|---|
| US.03 — As a veterinarian, I want to log vaccines and diets so that the animal's health history is up to date. | 5 |
| US.04 — As a worker, I want visual alerts for medical deadlines so that no treatment is missed. | 5 |

**Delivered.** The JavaFX intake form connected to the service layer, the shelter spaces with
their maximum capacity, the transactional capacity check and the transfer between spaces, the
first JUnit tests and the structured logging of occupancy changes.

## Sprint 3 — Adopter Matching & Management Dashboard
*6 – 13 February 2026 · 13 points*

| User story | Points |
|---|---|
| US.05 — As an adoption officer, I want to match animals with adopters so that we can streamline placements. | 8 |
| US.06 — As a director, I want to see monthly adoption charts so that I can monitor shelter performance. | 5 |

**Delivered.** The health records with their treatment types and the 48-hour deadline alerts,
the `Adoption` entity, the transactional placement, the adopter compatibility rules, the
statistics services and the dashboard with its charts. The heaviest sprint of the project.

## Sprint 4 — Audit, Logging & Reliability Testing
*13 – 20 February 2026 · 10 points*

| User story | Points |
|---|---|
| US.07 — As an auditor, I want to log all status changes so that we have a transparent history. | 5 |
| US.08 — As a developer, I want a test suite for adoption rules so that the system is stable. | 5 |

**Delivered.** The consolidation of the application into a full MVC structure with the JDBC
modules, the append-only audit trail of status changes, the continuous integration workflow
and the test suite covering the adoption rules.

## Post-review remediation
*30 August – 8 September 2026 · 40 points*

Corrective iteration opened after the February review, to fix the defects the review exposed.
It is not a fifth development sprint: it adds no new functional scope, only the parts of
the original stories that the review found missing, unverified or badly implemented.

| User story | Points | Status |
|---|---|---|
| US.09 — Referential integrity for animal, health and adoption records | 5 | Done |
| US.10 — Persistence layer verified by automated tests | 8 | Done |
| US.11 — Guided adoption matching reviewed against the specification | 5 | Done |
| US.12 — User guide with screenshots for reception staff | 3 | Done |
| US.13 — SCRUM process documented in the repository | 5 | Done |
| US.14 — Dashboard indicators shown as charts | 5 | Done |
| US.15 — Registry of the animals visible in the intake module | 3 | Done |
| US.16 — Test coverage measured and published | 3 | Done |
| US.17 — Medical history of an animal visible in the health module | 3 | Done |
