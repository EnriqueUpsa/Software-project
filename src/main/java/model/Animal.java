package model;

import java.time.LocalDate;

/**
 * Base abstract class for all animal types in the shelter.
 */
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

    public enum HealthStatus {
        HEALTHY("Healthy"),
        UNDER_OBSERVATION("Under observation"),
        CHRONIC_CONDITION("Chronic condition"),
        CRITICAL("Critical");

        private final String label;

        HealthStatus(String label) {
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
    protected int estimatedAgeYears;
    protected HealthStatus healthStatus;
    protected String photoPath;
    protected Status status;

    public Animal(String microchipId, String breed, LocalDate intakeDate) {
        this(microchipId, breed, intakeDate,
                0, HealthStatus.UNDER_OBSERVATION, "photos/unknown.jpg",
                Status.IN_OBSERVATION);
    }

    public Animal(String microchipId, String breed, LocalDate intakeDate,
                  Status status) {
        this(microchipId, breed, intakeDate,
                0, HealthStatus.UNDER_OBSERVATION, "photos/unknown.jpg",
                status);
    }

    public Animal(String microchipId, String breed, LocalDate intakeDate,
                  int estimatedAgeYears,
                  HealthStatus healthStatus,
                  String photoPath,
                  Status status) {
        setMicrochipId(microchipId);
        setBreed(breed);
        setIntakeDate(intakeDate);
        setEstimatedAgeYears(estimatedAgeYears);
        setHealthStatus(healthStatus);
        setPhotoPath(photoPath);
        setStatus(status);
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public void setMicrochipId(String microchipId) {
        if (microchipId == null || microchipId.isBlank()) {
            throw new IllegalArgumentException("Microchip ID is required");
        }
        this.microchipId = microchipId;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed is required");
        }
        this.breed = breed;
    }

    public LocalDate getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(LocalDate intakeDate) {
        if (intakeDate == null) {
            throw new IllegalArgumentException("Intake date is required");
        }
        this.intakeDate = intakeDate;
    }

    public int getEstimatedAgeYears() {
        return estimatedAgeYears;
    }

    public void setEstimatedAgeYears(int estimatedAgeYears) {
        if (estimatedAgeYears < 0) {
            throw new IllegalArgumentException("Estimated age must be >= 0");
        }
        this.estimatedAgeYears = estimatedAgeYears;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        if (healthStatus == null) {
            throw new IllegalArgumentException("Health status is required");
        }
        this.healthStatus = healthStatus;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            throw new IllegalArgumentException("Photo path is required");
        }
        this.photoPath = photoPath;
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

    public abstract String getSpecies();
}
