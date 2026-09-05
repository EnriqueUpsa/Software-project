package controller;

import model.HealthRecord;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the medical history that the health table is painted from.
 *
 * <p>The records were already being written before this listing existed, so what
 * is checked here is that they are read back for the right animal and in the
 * order a veterinarian reads them: oldest treatment first.</p>
 */
class HealthControllerTest {

    private Connection connection;
    private AppContext context;
    private HealthController healthController;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:health_" + System.nanoTime(), "sa", "");
        context = AppContext.createFor(connection);
        healthController = new HealthController(context.healthService());
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void listsNothingForAnAnimalWithoutTreatments() {
        DemoDataSeeder.seedIfEmpty(context);

        assertTrue(healthController.listRecordsFor("MC-1004").isEmpty());
    }

    @Test
    void listsOnlyTheTreatmentsOfThatAnimal() {
        DemoDataSeeder.seedIfEmpty(context);

        List<HealthRecord> records = healthController.listRecordsFor("MC-1002");

        assertEquals(2, records.size());
        for (HealthRecord record : records) {
            assertEquals("MC-1002", record.getMicrochipId());
        }
    }

    @Test
    void ordersTheHistoryFromTheOldestTreatment() {
        DemoDataSeeder.seedIfEmpty(context);

        List<HealthRecord> records = healthController.listRecordsFor("MC-1002");

        assertEquals(HealthRecord.TreatmentType.PARASITE_TREATMENT,
                records.get(0).getTreatmentType());
        assertEquals(HealthRecord.TreatmentType.VACCINE,
                records.get(1).getTreatmentType());
        assertTrue(records.get(0).getDate().isBefore(records.get(1).getDate()));
    }

    @Test
    void showsATreatmentAsSoonAsItIsSaved() {
        DemoDataSeeder.seedIfEmpty(context);
        int before = healthController.listRecordsFor("MC-1001").size();

        healthController.saveRecord("MC-1001", HealthRecord.TreatmentType.VACCINE,
                LocalDate.now(), "Annual booster", "1");

        assertEquals(before + 1, healthController.listRecordsFor("MC-1001").size());
    }

    @Test
    void rejectsAnEmptyMicrochip() {
        assertThrows(IllegalArgumentException.class,
                () -> healthController.listRecordsFor("  "));
    }
}
