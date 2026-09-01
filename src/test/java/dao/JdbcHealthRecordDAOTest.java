package dao;

import model.HealthRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcHealthRecordDAO} against a real H2 database.
 */
class JdbcHealthRecordDAOTest {

    private Connection connection;
    private JdbcHealthRecordDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        JdbcTestSupport.givenAnimal(connection, "MC-1");
        dao = new JdbcHealthRecordDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private HealthRecord vaccine(String microchipId, LocalDate date) {
        return new HealthRecord(microchipId, HealthRecord.TreatmentType.VACCINE,
                "Rabies", date, "1");
    }

    @Test
    void savesAndReadsBackTheTreatment() {
        dao.save(vaccine("MC-1", LocalDate.of(2026, 8, 15)));

        List<HealthRecord> records = dao.findByMicrochipId("MC-1");

        assertEquals(1, records.size());
        HealthRecord stored = records.get(0);
        assertEquals("MC-1", stored.getMicrochipId());
        assertEquals(HealthRecord.TreatmentType.VACCINE, stored.getTreatmentType());
        assertEquals("Rabies", stored.getDescription());
        assertEquals(LocalDate.of(2026, 8, 15), stored.getDate());
        assertEquals("1", stored.getDosage());
    }

    @Test
    void keepsEveryTreatmentTypeOfTheSpecification() {
        for (HealthRecord.TreatmentType type : HealthRecord.TreatmentType.values()) {
            dao.save(new HealthRecord("MC-1", type, type.name(),
                    LocalDate.of(2026, 8, 15), "1"));
        }

        List<HealthRecord> records = dao.findByMicrochipId("MC-1");

        assertEquals(HealthRecord.TreatmentType.values().length, records.size());
        for (HealthRecord.TreatmentType type : HealthRecord.TreatmentType.values()) {
            assertTrue(records.stream().anyMatch(r -> r.getTreatmentType() == type),
                    "missing treatment type " + type);
        }
    }

    @Test
    void returnsOnlyTheHistoryOfTheRequestedAnimal() {
        JdbcTestSupport.givenAnimal(connection, "MC-2");
        dao.save(vaccine("MC-1", LocalDate.of(2026, 8, 15)));
        dao.save(vaccine("MC-2", LocalDate.of(2026, 8, 16)));

        assertEquals(1, dao.findByMicrochipId("MC-1").size());
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void returnsAnEmptyHistoryForAnAnimalWithoutTreatments() {
        JdbcTestSupport.givenAnimal(connection, "MC-3");

        assertTrue(dao.findByMicrochipId("MC-3").isEmpty());
    }

    @Test
    void rejectsATreatmentForAnAnimalThatIsNotRegistered() {
        assertThrows(RuntimeException.class,
                () -> dao.save(vaccine("MC-UNKNOWN", LocalDate.of(2026, 8, 15))));
    }
}
