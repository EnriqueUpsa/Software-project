package model;

import java.time.LocalDate;

public abstract class Animal {
    protected String microchipId;
    protected String breed;
    protected LocalDate intakeDate;

    public Animal(String microchipId, String breed, LocalDate intakeDate) {
        this.microchipId = microchipId;
        this.breed = breed;
        this.intakeDate = intakeDate;
    }
    public String getMicrochipId() {
        return microchipId;
    }

}
