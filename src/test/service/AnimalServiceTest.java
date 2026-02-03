package service;

import dao.InMemoryAnimalDAO;
import model.Dog;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AnimalServiceTest {
    @Test
    void registerAnimal_success() {
        InMemoryAnimalDAO dao = new InMemoryAnimalDAO();
        AnimalService service = new AnimalService(dao);

        Dog dog = new Dog("123", "Labrador", LocalDate.now());

        service.registerAnimal(dog);

        assertTrue(dao.findByMicrochipId("123").isPresent());
    }

    @Test
    void registerAnimal_duplicateMicrochip_throwsException() {
        InMemoryAnimalDAO dao = new InMemoryAnimalDAO();
        AnimalService service = new AnimalService(dao);

        Dog dog1 = new Dog("123", "Labrador", LocalDate.now());
        Dog dog2 = new Dog("123", "Beagle", LocalDate.now());

        service.registerAnimal(dog1);

        assertThrows(IllegalArgumentException.class, () -> {
            service.registerAnimal(dog2);
        });
    }


}
