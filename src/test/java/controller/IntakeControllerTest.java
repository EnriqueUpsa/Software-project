package controller;

import model.Animal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.AppContext;
import ui.DemoDataSeeder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the registry listing that the intake table is painted from.
 *
 * <p>The reception desk works with the latest intakes, so the order of the list is
 * part of the behaviour and is checked here, not only its content.</p>
 */
class IntakeControllerTest {

    private Connection connection;
    private AppContext context;
    private IntakeController intakeController;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:intake_" + System.nanoTime(), "sa", "");
        context = AppContext.createFor(connection);
        intakeController = new IntakeController(
                context.animalService(),
                context.kennelService(),
                context.kennelDAO()
        );
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void listsNothingWhileTheShelterIsEmpty() {
        assertTrue(intakeController.listAnimals().isEmpty());
    }

    @Test
    void listsEveryAnimalOfTheRegistry() {
        DemoDataSeeder.seedIfEmpty(context);

        assertEquals(5, intakeController.listAnimals().size());
    }

    @Test
    void putsTheMostRecentIntakeFirst() {
        DemoDataSeeder.seedIfEmpty(context);

        List<Animal> animals = intakeController.listAnimals();

        // MC-1004 entered three days ago, MC-1005 sixty days ago.
        assertEquals("MC-1004", animals.get(0).getMicrochipId());
        assertEquals("MC-1005", animals.get(animals.size() - 1).getMicrochipId());
    }

    @Test
    void showsAnAnimalRegisteredTodayAtTheTopOfTheList() {
        DemoDataSeeder.seedIfEmpty(context);

        intakeController.registerAnimal(new IntakeController.IntakeRequest(
                "MC-2001",
                "Podenco",
                "2",
                "photos/podenco.jpg",
                LocalDate.now(),
                "Dog",
                Animal.HealthStatus.HEALTHY,
                AppContext.DEFAULT_KENNEL_ID
        ));

        List<Animal> animals = intakeController.listAnimals();

        assertEquals(6, animals.size());
        assertEquals("MC-2001", animals.get(0).getMicrochipId());
    }

    @Test
    void reportsTheOccupancyOfTheIntakeSpace() {
        DemoDataSeeder.seedIfEmpty(context);

        // The demo shelter puts four animals in the main kennel, which holds twenty.
        assertEquals("4 / 20", intakeController.getOccupancyText(AppContext.DEFAULT_KENNEL_ID));
    }
}
