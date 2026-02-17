package model;

import java.time.LocalDate;

/**
 * Cat specialization of {@link Animal}.
 */
public class Cat extends Animal{
    public Cat(String microchipId, String breed, LocalDate intakeDate) {
        super(microchipId, breed, intakeDate);
    }

    public Cat(String microchipId, String breed, LocalDate intakeDate,
               Status status) {
        super(microchipId, breed, intakeDate, status);
    }

    public Cat(String microchipId, String breed, LocalDate intakeDate,
               int estimatedAgeYears, HealthStatus healthStatus, String photoPath,
               Status status) {
        super(microchipId, breed, intakeDate, estimatedAgeYears, healthStatus, photoPath, status);
    }

    @Override
    public String getSpecies() {
        return "Cat";
    }
}
