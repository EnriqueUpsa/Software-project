# Exam Demo Guide

## 1. Quick Demo Flow (5-7 minutes)
1. Open app with `bash run_app.sh`.
2. Register an animal in Intake (Dog/Cat, microchip, breed, date).
3. Add a health record and show urgent deadline count.
4. Register adopter, check compatibility, process adoption.
5. Open Logistics and show occupancy/transfer capacity behavior.
6. Open Dashboard and show status + monthly adoptions + urgent needs.
7. Open History and load status transitions by microchip.

## 2. Architecture Talking Points
- MVC with clear separation: UI views, controllers, services, DAOs, models.
- Business rules in services; persistence via JDBC DAOs.
- Transaction management for critical operations:
  - adoption finalization
  - occupancy transfer and assign capacity checks
- Structured logging and deterministic validation rules.

## 3. Testing Talking Points
- JUnit suite covers core domain/service logic:
  - health deadlines
  - kennel capacity and transfer
  - adoption validation and transaction behavior
  - compatibility and status history
- Run with `bash run_tests.sh`.

## 4. Scrum/GitHub Evidence
- Burndown and sprint board screenshots in `docs/screenshots/taiga/`.
- Commit history reflects iterative delivery and integration.

## 5. What each teammate should be able to explain
- Domain model and status lifecycle.
- Why transaction boundaries are in current layers.
- How validation avoids inconsistent data states.
- Why DAO interfaces + in-memory implementations improve testability.
