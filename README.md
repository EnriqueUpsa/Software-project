# Pet Shelter and Adoption Management System

Desktop JavaFX application for animal intake, health tracking, adoption workflow, shelter logistics, dashboard metrics and audit history.

## Features
- Animal registry with full profile (microchip, species, breed, age, intake date, health status, photo path, lifecycle status).
- Health and nutrition records (veterinary visits, vaccines, diets, parasite treatments).
- 48-hour medical deadline alerts in JavaFX.
- Adopter registry and guided compatibility + adoption flow.
- Shelter space logistics (kennel/cage/fenced area), capacity control and transactional transfers.
- Dashboard with status distribution, monthly adoptions and urgent needs.
- Status-change history and structured logging.

## Architecture
- `model/`: entities and enums
- `dao/`: DAO contracts + JDBC and in-memory implementations
- `service/`: business rules
- `controller/`: use-case controllers
- `ui/view/`: JavaFX views
- `ui/ShelterManagementApp`: app bootstrap

Pattern: MVC + DAO over JDBC.

## Requirements
- Java 21
- Maven (optional, scripts also support non-Maven local fallback)
- macOS recommended for provided run scripts

## Run the App
```bash
cd "/Users/administrador/Downloads/PetShelterManagement 4"
bash run_app.sh
```

## Run Tests
```bash
cd "/Users/administrador/Downloads/PetShelterManagement 4"
bash run_tests.sh
```

## Data and Logs
- DB file: `data/petshelter` (H2)
- Logs: `logs/shelter.log`
