# Test Results

Latest local run:
- Date: 2026-02-17
- Command: `bash run_tests.sh`
- Result: `55 tests successful, 0 failed`

Coverage focus:
- `HealthServiceTest`
- `KennelServiceTest`
- `AdoptionServiceTest`
- `AdopterServiceTest`
- `AnimalServiceTest`
- `StatisticsServiceTest`
- model and DAO behavior tests

Notes:
- Non-Maven environments are supported by the fallback compiler/test runner in `run_tests.sh`.
- Script auto-downloads `junit-platform-console-standalone` if needed.
