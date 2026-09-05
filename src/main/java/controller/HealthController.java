package controller;

import model.HealthRecord;
import service.HealthService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for health and nutrition operations.
 */
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    public void saveRecord(String microchipId,
                           HealthRecord.TreatmentType treatmentType,
                           LocalDate date,
                           String description,
                           String dosage) {
        HealthRecord record = new HealthRecord(
                microchipId,
                treatmentType,
                description,
                date,
                dosage
        );
        healthService.registerHealthRecord(record);
    }

    /**
     * Medical history of one animal, for the health table.
     *
     * @param microchipId microchip of the animal to consult.
     * @return its treatments, oldest first.
     */
    public List<HealthRecord> listRecordsFor(String microchipId) {
        return healthService.getRecordsFor(microchipId);
    }

    public int getUrgentMedicalDeadlineCount() {
        return healthService.getUpcomingMedicalDeadlines().size();
    }
}
