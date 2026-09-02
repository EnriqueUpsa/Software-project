package controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.AppContext;
import ui.DemoDataSeeder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the three indicators that the dashboard charts are painted from.
 *
 * <p>The charts themselves are JavaFX nodes and are not unit tested; what has to be
 * correct is the data the controller hands to them, and that is what is checked here
 * against a real database.</p>
 */
class DashboardControllerTest {

    private Connection connection;
    private AppContext context;
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:dashboard_" + System.nanoTime(), "sa", "");
        context = AppContext.createFor(connection);
        dashboardController = new DashboardController(
                context.animalService(),
                context.adoptionService(),
                context.healthService()
        );
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void showsNothingWhileTheShelterIsEmpty() {
        assertTrue(dashboardController.getAnimalStatusDistribution().isEmpty());
        assertEquals(0, totalAdoptionsIn(LocalDate.now().getYear()));
        assertEquals(0, dashboardController.getUrgentMedicalDeadlineCount());
    }

    @Test
    void countsEveryAnimalOfTheShelterOnceInTheStatusChart() {
        DemoDataSeeder.seedIfEmpty(context);

        Map<String, Integer> distribution = dashboardController.getAnimalStatusDistribution();

        assertEquals(4, distribution.size());
        assertEquals(5, distribution.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void placesTheClosedAdoptionInTheMonthlyChart() {
        DemoDataSeeder.seedIfEmpty(context);

        // The demo placement is dated two days ago, so it belongs to that year.
        LocalDate placementDate = LocalDate.now().minusDays(2);

        assertEquals(1, totalAdoptionsIn(placementDate.getYear()));
        assertEquals(1, dashboardController
                .getMonthlyAdoptions(placementDate.getYear())
                .getOrDefault(placementDate.getMonth(), 0));
    }

    @Test
    void raisesTheAlertWhenAVeterinaryDeadlineIsWithin48Hours() {
        DemoDataSeeder.seedIfEmpty(context);

        assertEquals(1, dashboardController.getUrgentMedicalDeadlineCount());
    }

    private int totalAdoptionsIn(int year) {
        Map<Month, Integer> monthlyAdoptions = dashboardController.getMonthlyAdoptions(year);
        return monthlyAdoptions.values().stream().mapToInt(Integer::intValue).sum();
    }
}
