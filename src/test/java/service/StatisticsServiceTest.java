package service;

import dao.InMemoryAdoptionDAO;
import dao.InMemoryAnimalDAO;
import model.Adoption;
import model.Animal;
import model.Cat;
import model.Dog;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {

    @Test
    void getMonthlyAdoptions_countsByMonth() {
        InMemoryAdoptionDAO adoptionDAO = new InMemoryAdoptionDAO();
        AdoptionService adoptionService = new AdoptionService(adoptionDAO);

        adoptionDAO.save(new Adoption("A1", "U1",
                LocalDate.of(2026, Month.JANUARY, 10)));
        adoptionDAO.save(new Adoption("A2", "U2",
                LocalDate.of(2026, Month.JANUARY, 20)));
        adoptionDAO.save(new Adoption("A3", "U3",
                LocalDate.of(2026, Month.FEBRUARY, 5)));

        Map<Month, Integer> result = adoptionService.getMonthlyAdoptions(2026);

        assertEquals(2, result.get(Month.JANUARY));
        assertEquals(1, result.get(Month.FEBRUARY));
    }

    @Test
    void getAnimalStatusDistribution_countsByStatus() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        AnimalService animalService = new AnimalService(animalDAO);

        Dog dog = new Dog("D1", "Beagle", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        Cat cat = new Cat("C1", "Siamese", LocalDate.now(),
                Animal.Status.UNDER_TREATMENT);
        Dog adopted = new Dog("D2", "Labrador", LocalDate.now(),
                Animal.Status.ADOPTED);

        animalDAO.save(dog);
        animalDAO.save(cat);
        animalDAO.save(adopted);

        Map<String, Integer> result =
                animalService.getAnimalStatusDistribution();

        assertEquals(1, result.get(Animal.Status.READY_FOR_ADOPTION.toString()));
        assertEquals(1, result.get(Animal.Status.UNDER_TREATMENT.toString()));
        assertEquals(1, result.get(Animal.Status.ADOPTED.toString()));
    }
}
