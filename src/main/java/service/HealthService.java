package service;

import dao.HealthRecordDAO;
import model.HealthRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HealthService {
    private final HealthRecordDAO healthRecordDAO;
    private static final Logger logger =
            Logger.getLogger(HealthService.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
    }

    public HealthService(HealthRecordDAO healthRecordDAO) {
        this.healthRecordDAO = healthRecordDAO;
    }

    public void registerHealthRecord(HealthRecord record) {
        logger.info("Attempting to register health record for microchip: "
                + (record == null ? "null" : record.getMicrochipId()));
        validate(record);
        healthRecordDAO.save(record);
        logger.info("Health record registered successfully for microchip: "
                + record.getMicrochipId());
    }

    public List<HealthRecord> getUpcomingVaccines(String microchipId) {
        List<HealthRecord> records =
                healthRecordDAO.findByMicrochipId(microchipId);
        List<HealthRecord> upcoming = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (HealthRecord record : records) {
            if (isVaccineDueWithin48Hours(record, now)) {
                upcoming.add(record);
            }
        }

        return upcoming;
    }

    public boolean isVaccineDueWithin48Hours(HealthRecord record) {
        return isVaccineDueWithin48Hours(record, LocalDate.now());
    }

    public boolean isVaccineDueWithin48Hours(HealthRecord record,
                                             LocalDate referenceDate) {
        if (record == null || referenceDate == null) {
            return false;
        }

        if (record.getTreatmentType() != HealthRecord.TreatmentType.VACCINE) {
            return false;
        }

        LocalDate date = record.getDate();
        if (date == null) {
            return false;
        }

        LocalDate limit = referenceDate.plusDays(2);
        return (!date.isBefore(referenceDate)) && (!date.isAfter(limit));
    }

    private void validate(HealthRecord record) {
        if (record == null) {
            logger.warning("Validation error: health record is null");
            throw new IllegalArgumentException("Health record is required");
        }

        String microchipId = record.getMicrochipId();
        if (microchipId == null || microchipId.isBlank()) {
            logger.warning("Validation error: microchip ID is required");
            throw new IllegalArgumentException("Microchip ID is required");
        }

        if (record.getTreatmentType() == null) {
            logger.warning("Validation error: treatment type is required");
            throw new IllegalArgumentException("Treatment type is required");
        }

        if (record.getDate() == null) {
            logger.warning("Validation error: date is required");
            throw new IllegalArgumentException("Date is required");
        }

        String dosage = record.getDosage();
        if (dosage == null || dosage.isBlank()) {
            logger.warning("Validation error: dosage is required");
            throw new IllegalArgumentException("Dosage is required");
        }

        try {
            double amount = Double.parseDouble(dosage.trim());
            if (amount <= 0) {
                logger.warning("Validation error: dosage must be positive");
                throw new IllegalArgumentException("Dosage must be positive");
            }
        } catch (NumberFormatException ex) {
            logger.warning("Validation error: dosage must be a valid number");
            throw new IllegalArgumentException("Dosage must be a valid number");
        }
    }
}
