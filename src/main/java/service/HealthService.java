package service;

import dao.HealthRecordDAO;
import model.HealthRecord;

public class HealthService {
    private final HealthRecordDAO healthRecordDAO;

    public HealthService(HealthRecordDAO healthRecordDAO) {
        this.healthRecordDAO = healthRecordDAO;
    }

    public void registerHealthRecord(HealthRecord record) {
        validate(record);
        healthRecordDAO.save(record);
    }

    private void validate(HealthRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Health record is required");
        }

        String microchipId = record.getMicrochipId();
        if (microchipId == null || microchipId.isBlank()) {
            throw new IllegalArgumentException("Microchip ID is required");
        }

        if (record.getTreatmentType() == null) {
            throw new IllegalArgumentException("Treatment type is required");
        }

        if (record.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }

        String dosage = record.getDosage();
        if (dosage == null || dosage.isBlank()) {
            throw new IllegalArgumentException("Dosage is required");
        }

        try {
            double amount = Double.parseDouble(dosage.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Dosage must be positive");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Dosage must be a valid number");
        }
    }
}
