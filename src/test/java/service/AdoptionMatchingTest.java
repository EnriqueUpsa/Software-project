package service;

import dao.InMemoryAdopterDAO;
import dao.InMemoryAdoptionDAO;
import dao.InMemoryAnimalDAO;
import dao.InMemoryStatusChangeLogDAO;
import model.Adopter;
import model.Animal;
import model.Cat;
import model.Dog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the guided matching required by the specification: the procedure must
 * propose only the animals an adopter can actually take home.
 */
class AdoptionMatchingTest {

    private InMemoryAnimalDAO animalDAO;
    private InMemoryAdopterDAO adopterDAO;
    private AdoptionService service;

    @BeforeEach
    void setUp() {
        animalDAO = new InMemoryAnimalDAO();
        adopterDAO = new InMemoryAdopterDAO();
        service = new AdoptionService(new InMemoryAdoptionDAO(), animalDAO, adopterDAO,
                new InMemoryStatusChangeLogDAO(), null);
    }

    private void givenDog(String microchipId, String breed, Animal.Status status) {
        animalDAO.save(new Dog(microchipId, breed, LocalDate.of(2026, 8, 1), status));
    }

    private void givenCat(String microchipId, String breed, Animal.Status status) {
        animalDAO.save(new Cat(microchipId, breed, LocalDate.of(2026, 8, 1), status));
    }

    private void givenAdopter(String adopterId, String species, String breed) {
        adopterDAO.save(new Adopter(adopterId, "Giulia Rossi", "3290000000", species, breed));
    }

    @Test
    void proposesOnlyTheAnimalsOfThePreferredSpecies() {
        givenDog("MC-DOG", "Beagle", Animal.Status.READY_FOR_ADOPTION);
        givenCat("MC-CAT", "Siamese", Animal.Status.READY_FOR_ADOPTION);
        givenAdopter("ADO-1", "Dog", "");

        List<Animal> candidates = service.findCandidatesFor("ADO-1");

        assertEquals(1, candidates.size());
        assertEquals("MC-DOG", candidates.get(0).getMicrochipId());
    }

    @Test
    void narrowsTheProposalWithTheBreedPreference() {
        givenDog("MC-1", "Beagle", Animal.Status.READY_FOR_ADOPTION);
        givenDog("MC-2", "Labrador", Animal.Status.READY_FOR_ADOPTION);
        givenAdopter("ADO-1", "Dog", "Labrador");

        List<Animal> candidates = service.findCandidatesFor("ADO-1");

        assertEquals(1, candidates.size());
        assertEquals("MC-2", candidates.get(0).getMicrochipId());
    }

    @Test
    void proposesEveryAvailableAnimalWhenTheAdopterHasNoPreference() {
        givenDog("MC-DOG", "Beagle", Animal.Status.READY_FOR_ADOPTION);
        givenCat("MC-CAT", "Siamese", Animal.Status.READY_FOR_ADOPTION);
        givenAdopter("ADO-1", "", "");

        assertEquals(2, service.findCandidatesFor("ADO-1").size());
    }

    @Test
    void leavesOutTheAnimalsThatAreNotReadyForAdoption() {
        givenDog("MC-READY", "Beagle", Animal.Status.READY_FOR_ADOPTION);
        givenDog("MC-OBSERVATION", "Beagle", Animal.Status.IN_OBSERVATION);
        givenDog("MC-TREATMENT", "Beagle", Animal.Status.UNDER_TREATMENT);
        givenDog("MC-ADOPTED", "Beagle", Animal.Status.ADOPTED);
        givenAdopter("ADO-1", "Dog", "");

        List<Animal> candidates = service.findCandidatesFor("ADO-1");

        assertEquals(1, candidates.size());
        assertEquals("MC-READY", candidates.get(0).getMicrochipId());
    }

    @Test
    void returnsAnEmptyProposalWhenNothingMatches() {
        givenCat("MC-CAT", "Siamese", Animal.Status.READY_FOR_ADOPTION);
        givenAdopter("ADO-1", "Dog", "");

        assertTrue(service.findCandidatesFor("ADO-1").isEmpty());
    }

    @Test
    void rejectsAnAdopterThatIsNotRegistered() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findCandidatesFor("ADO-UNKNOWN"));
    }

    @Test
    void refusesToMatchWhenTheServiceHasNoAccessToTheRegistries() {
        AdoptionService withoutRegistries = new AdoptionService(new InMemoryAdoptionDAO());

        assertThrows(IllegalStateException.class,
                () -> withoutRegistries.findCandidatesFor("ADO-1"));
    }
}
