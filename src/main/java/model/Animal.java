package model;

import java.time.LocalDate;

public abstract class Animal {
    public enum Status {
        IN_OBSERVATION("In observation"),
        UNDER_TREATMENT("Under treatment"),
        READY_FOR_ADOPTION("Ready for adoption"),
        ADOPTED("Adopted");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    protected String microchipId;
    protected String breed;
    protected LocalDate intakeDate;
    protected Status status;

    public Animal(String microchipId, String breed, LocalDate intakeDate) {
        this.microchipId = microchipId;
        this.breed = breed;
        this.intakeDate = intakeDate;
        this.status = Status.IN_OBSERVATION;
    }

    public Animal(String microchipId, String breed, LocalDate intakeDate,
                  Status status) {
        this.microchipId = microchipId;
        this.breed = breed;
        this.intakeDate = intakeDate;
        setStatus(status);
    }
    public String getMicrochipId() {
        return microchipId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        this.status = status;
    }
}
