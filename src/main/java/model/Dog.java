package model;
import java.time.LocalDate;

/**
 * Dog specialization of {@link Animal}.
 */
public class Dog extends Animal{
    public Dog(String microchipId, String breed, LocalDate intakeDate) {
        super(microchipId, breed, intakeDate);
    }

    public Dog(String microchipId, String breed, LocalDate intakeDate,
               Status status) {
        super(microchipId, breed, intakeDate, status);
    }

    public Dog(String microchipId, String breed, LocalDate intakeDate,
               int estimatedAgeYears, HealthStatus healthStatus, String photoPath,
               Status status) {
        super(microchipId, breed, intakeDate, estimatedAgeYears, healthStatus, photoPath, status);
    }

    @Override
    public String getSpecies() {
        return "Dog";
    }
}
