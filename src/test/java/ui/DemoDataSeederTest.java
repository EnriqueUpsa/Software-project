package ui;

import model.Animal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the demo shelter that the application loads on an empty database.
 *
 * <p>The live demonstration of the project depends on this data, so it is worth
 * a test: every screen must have something to show after the first start.</p>
 */
class DemoDataSeederTest {

    private Connection connection;
    private AppContext context;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:demo_" + System.nanoTime(), "sa", "");
        context = AppContext.createFor(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void fillsAnEmptyRegistryWithTheDemoShelter() {
        assertTrue(DemoDataSeeder.seedIfEmpty(context));

        assertEquals(5, context.animalService().getAllAnimals().size());
        assertEquals(2, context.adopterService().getAllAdopters().size());
    }

    @Test
    void coversTheFourLifecycleStatuses() {
        DemoDataSeeder.seedIfEmpty(context);

        Map<String, Integer> distribution = context.animalService().getAnimalStatusDistribution();

        assertEquals(2, distribution.get(Animal.Status.READY_FOR_ADOPTION.toString()));
        assertEquals(1, distribution.get(Animal.Status.UNDER_TREATMENT.toString()));
        assertEquals(1, distribution.get(Animal.Status.IN_OBSERVATION.toString()));
        assertEquals(1, distribution.get(Animal.Status.ADOPTED.toString()));
    }

    @Test
    void leavesAnUpcomingMedicalDeadlineForTheAlert() {
        DemoDataSeeder.seedIfEmpty(context);

        assertFalse(context.healthService().getUpcomingMedicalDeadlines().isEmpty());
    }

    @Test
    void completesOnePlacementWithItsAuditTrail() {
        DemoDataSeeder.seedIfEmpty(context);

        Animal adopted = context.animalService().getAnimalByMicrochip("MC-1005");

        assertEquals(Animal.Status.ADOPTED, adopted.getStatus());
        assertFalse(context.animalService().getStatusHistory("MC-1005").isEmpty());
    }

    @Test
    void proposesOnlyCompatibleAnimalsToTheDemoAdopters() {
        DemoDataSeeder.seedIfEmpty(context);

        assertEquals(1, context.adoptionService().findCandidatesFor("ADO-1001").size());
        assertEquals(1, context.adoptionService().findCandidatesFor("ADO-1002").size());
    }

    @Test
    void doesNotWriteTwiceOnAnAlreadyPopulatedRegistry() {
        assertTrue(DemoDataSeeder.seedIfEmpty(context));

        assertFalse(DemoDataSeeder.seedIfEmpty(context));
        assertEquals(5, context.animalService().getAllAnimals().size());
    }
}
