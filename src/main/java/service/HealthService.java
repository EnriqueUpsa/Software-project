package service;

import dao.HealthRecordDAO;
import model.HealthRecord;
import util.LoggerConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Application service for health records and vaccine deadline alerts.
 */
public class HealthService {
    private final HealthRecordDAO healthRecordDAO;
    private static final Logger logger =
            LoggerConfig.getLogger(HealthService.class);

    public HealthService(HealthRecordDAO healthRecordDAO) {
        this.healthRecordDAO = healthRecordDAO;
    }

    public void registerHealthRecord(HealthRecord record) {
        logger.info("event=health_record_register_start microchipId="
                + (record == null ? "null" : record.getMicrochipId()));
        validate(record);
        healthRecordDAO.save(record);
        logger.info("event=therapy_change microchipId=" + record.getMicrochipId()
                + " treatmentType=" + record.getTreatmentType().name()
                + " treatmentDate=" + record.getDate());
    }

    public List<HealthRecord> getUpcomingVaccines() {
        List<HealthRecord> records = healthRecordDAO.findAll();
        List<HealthRecord> upcoming = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (HealthRecord record : records) {
            if (isVaccineDueWithin48Hours(record, now)) {
                upcoming.add(record);
            }
        }

        return upcoming;
    }

    public List<HealthRecord> getUpcomingMedicalDeadlines() {
        List<HealthRecord> records = healthRecordDAO.findAll();
        List<HealthRecord> upcoming = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (HealthRecord record : records) {
            if (isMedicalDeadlineWithin48Hours(record, now)) {
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

    public boolean isMedicalDeadlineWithin48Hours(HealthRecord record,
                                                  LocalDate referenceDate) {
        if (record == null || referenceDate == null || record.getDate() == null) {
            return false;
        }

        if (record.getTreatmentType() == HealthRecord.TreatmentType.DIET) {
            return false;
        }

        LocalDate date = record.getDate();
        LocalDate limit = referenceDate.plusDays(2);
        return (!date.isBefore(referenceDate)) && (!date.isAfter(limit));
    }

    private void validate(HealthRecord record) {
        if (record == null) {
            logger.warning("event=health_record_validation_failed reason=null_record");
            throw new IllegalArgumentException("Health record is required");
        }

        String microchipId = record.getMicrochipId();
        if (microchipId == null || microchipId.isBlank()) {
            logger.warning("event=health_record_validation_failed reason=microchip_required");
            throw new IllegalArgumentException("Microchip ID is required");
        }

        if (record.getTreatmentType() == null) {
            logger.warning("event=health_record_validation_failed reason=treatment_type_required");
            throw new IllegalArgumentException("Treatment type is required");
        }

        if (record.getDate() == null) {
            logger.warning("event=health_record_validation_failed reason=date_required");
            throw new IllegalArgumentException("Date is required");
        }

        String dosage = record.getDosage();
        if (dosage == null || dosage.isBlank()) {
            logger.warning("event=health_record_validation_failed reason=dosage_required");
            throw new IllegalArgumentException("Dosage is required");
        }

        try {
            double amount = Double.parseDouble(dosage.trim());
            if (amount <= 0) {
                logger.warning("event=health_record_validation_failed reason=dosage_not_positive");
                throw new IllegalArgumentException("Dosage must be positive");
            }
        } catch (NumberFormatException ex) {
            logger.warning("event=health_record_validation_failed reason=dosage_not_numeric");
            throw new IllegalArgumentException("Dosage must be a valid number");
        }
    }
}
