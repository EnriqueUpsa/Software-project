package dao;

import model.Animal;
import model.Cat;
import model.Dog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcAnimalDAO} against a real H2 database.
 */
class JdbcAnimalDAOTest {

    private Connection connection;
    private JdbcAnimalDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        dao = new JdbcAnimalDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void savesAndReadsBackEveryFieldOfTheProfile() {
        Animal dog = new Dog("MC-1", "Beagle", LocalDate.of(2026, 3, 12),
                4, Animal.HealthStatus.HEALTHY, "photos/beagle.jpg",
                Animal.Status.READY_FOR_ADOPTION);
        dao.save(dog);

        Optional<Animal> found = dao.findByMicrochipId("MC-1");

        assertTrue(found.isPresent());
        Animal stored = found.get();
        assertEquals("MC-1", stored.getMicrochipId());
        assertEquals("Beagle", stored.getBreed());
        assertEquals(LocalDate.of(2026, 3, 12), stored.getIntakeDate());
        assertEquals(4, stored.getEstimatedAgeYears());
        assertEquals(Animal.HealthStatus.HEALTHY, stored.getHealthStatus());
        assertEquals("photos/beagle.jpg", stored.getPhotoPath());
        assertEquals(Animal.Status.READY_FOR_ADOPTION, stored.getStatus());
    }

    @Test
    void restoresTheConcreteSpeciesOfTheAnimal() {
        dao.save(new Dog("MC-DOG", "Beagle", LocalDate.of(2026, 3, 12)));
        dao.save(new Cat("MC-CAT", "Siamese", LocalDate.of(2026, 3, 12)));

        assertEquals("Dog", dao.findByMicrochipId("MC-DOG").orElseThrow().getSpecies());
        assertEquals("Cat", dao.findByMicrochipId("MC-CAT").orElseThrow().getSpecies());
    }

    @Test
    void returnsEmptyForAnUnknownMicrochip() {
        assertFalse(dao.findByMicrochipId("MC-UNKNOWN").isPresent());
    }

    @Test
    void updatesTheStoredProfile() {
        dao.save(new Dog("MC-2", "Beagle", LocalDate.of(2026, 3, 12)));

        dao.update(new Dog("MC-2", "Labrador", LocalDate.of(2026, 3, 12),
                6, Animal.HealthStatus.CHRONIC_CONDITION, "photos/lab.jpg",
                Animal.Status.UNDER_TREATMENT));

        Animal stored = dao.findByMicrochipId("MC-2").orElseThrow();
        assertEquals("Labrador", stored.getBreed());
        assertEquals(6, stored.getEstimatedAgeYears());
        assertEquals(Animal.HealthStatus.CHRONIC_CONDITION, stored.getHealthStatus());
        assertEquals(Animal.Status.UNDER_TREATMENT, stored.getStatus());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void listsEveryRegisteredAnimal() {
        dao.save(new Dog("MC-3", "Beagle", LocalDate.of(2026, 3, 12)));
        dao.save(new Cat("MC-4", "Siamese", LocalDate.of(2026, 3, 13)));

        List<Animal> all = dao.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void groupsTheRegistryByLifecycleStatus() {
        dao.save(new Dog("MC-5", "Beagle", LocalDate.of(2026, 3, 12),
                Animal.Status.READY_FOR_ADOPTION));
        dao.save(new Dog("MC-6", "Beagle", LocalDate.of(2026, 3, 12),
                Animal.Status.READY_FOR_ADOPTION));
        dao.save(new Cat("MC-7", "Siamese", LocalDate.of(2026, 3, 12),
                Animal.Status.IN_OBSERVATION));

        Map<String, Integer> distribution = dao.getStatusDistribution();

        assertEquals(2, distribution.get(Animal.Status.READY_FOR_ADOPTION.toString()));
        assertEquals(1, distribution.get(Animal.Status.IN_OBSERVATION.toString()));
    }
}
