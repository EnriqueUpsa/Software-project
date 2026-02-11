package model;

import java.time.LocalDate;

/**
 * Represents a medical record linked to an animal's microchip ID.
 */
public class HealthRecord {
    public enum TreatmentType {
        VACCINE("Vaccine"),
        DIET("Diet"),
        PARASITE_TREATMENT("Parasite Treatment");

        private final String label;

        TreatmentType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final String microchipId;
    private final TreatmentType treatmentType;
    private final String description;
    private final LocalDate date;
    private final String dosage;

    public HealthRecord(String microchipId,
                        TreatmentType treatmentType,
                        String description,
                        LocalDate date,
                        String dosage) {
        this.microchipId = microchipId;
        this.treatmentType = treatmentType;
        this.description = description;
        this.date = date;
        this.dosage = dosage;
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDosage() {
        return dosage;
    }
}
