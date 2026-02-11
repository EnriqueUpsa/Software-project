package model;
import java.time.LocalDate;

public class Dog extends Animal{
    public Dog(String microchipId, String breed, LocalDate intakeDate) {
        super(microchipId, breed, intakeDate);
    }

    public Dog(String microchipId, String breed, LocalDate intakeDate,
               Status status) {
        super(microchipId, breed, intakeDate, status);
    }
}
