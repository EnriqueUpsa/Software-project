package service;

import dao.InMemoryAdopterDAO;
import model.Adopter;
import model.Animal;
import model.Cat;
import model.Dog;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdopterServiceTest {

    @Test
    void registerAdopter_duplicateId_throwsException() {
        InMemoryAdopterDAO adopterDAO = new InMemoryAdopterDAO();
        AdopterService service = new AdopterService(adopterDAO);

        service.registerAdopter(new Adopter("U-1", "Ana", "123", "Dog", ""));

        assertThrows(IllegalArgumentException.class, () ->
                service.registerAdopter(new Adopter("U-1", "Ana 2", "999", "Dog", "")));
    }

    @Test
    void isCompatible_speciesAndBreedMatch_returnsTrue() {
        AdopterService service = new AdopterService(new InMemoryAdopterDAO());
        Animal dog = new Dog("D-1", "Labrador", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        Adopter adopter = new Adopter("U-2", "Luis", "777", "Dog", "Labrador");

        assertTrue(service.isCompatible(dog, adopter));
    }

    @Test
    void isCompatible_speciesMismatch_returnsFalse() {
        AdopterService service = new AdopterService(new InMemoryAdopterDAO());
        Animal cat = new Cat("C-1", "Siamese", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        Adopter adopter = new Adopter("U-3", "Marta", "555", "Dog", "");

        assertFalse(service.isCompatible(cat, adopter));
    }

    @Test
    void isCompatible_emptyPreferences_returnsTrue() {
        AdopterService service = new AdopterService(new InMemoryAdopterDAO());
        Animal dog = new Dog("D-2", "Beagle", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        Adopter adopter = new Adopter("U-4", "Nora", "333", "", "");

        assertTrue(service.isCompatible(dog, adopter));
    }
}
