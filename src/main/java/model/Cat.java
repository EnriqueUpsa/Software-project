package model;

import java.time.LocalDate;

public class Cat extends Animal{
    public Cat(String microchipId, String breed, LocalDate intakeDate) {
        super(microchipId, breed, intakeDate);
    }
}
