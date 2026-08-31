package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Verifies the referential integrity created by {@link SchemaInitializer}.
 *
 * <p>Each test runs against its own in-memory H2 database, so the tests are
 * independent from each other and from the file database used by the
 * application.</p>
 */
class SchemaIntegrityTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:schema_" + System.nanoTime(), "sa", "");
        SchemaInitializer.initialize(connection);
        execute("INSERT INTO animals (microchip_id, animal_type, breed, intake_date, "
                + "estimated_age, health_status, photo_path, status) "
                + "VALUES ('CHIP-1', 'DOG', 'Beagle', DATE '2026-08-01', 3, "
                + "'UNDER_OBSERVATION', 'photos/unknown.jpg', 'IN_OBSERVATION')");
        execute("INSERT INTO adopters (adopter_id, full_name, phone, preferred_species, "
                + "preferred_breed) VALUES ('ADO-1', 'Giulia Rossi', '3290000000', 'DOG', '')");
        execute("INSERT INTO kennels (kennel_id, space_type, max_capacity, occupied) "
                + "VALUES ('KENNEL-A', 'KENNEL', 5, 0)");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private void execute(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private int count(String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void insertHealthRecord(String microchipId) throws SQLException {
        execute("INSERT INTO health_records (microchip_id, treatment_type, description, "
                + "treatment_date, dosage) VALUES ('" + microchipId + "', 'VACCINE', "
                + "'Rabies', DATE '2026-08-15', '1ml')");
    }

    private void insertAdoption(String animalId, String adopterId) throws SQLException {
        execute("INSERT INTO adoptions (animal_id, adopter_id, placement_date) VALUES ('"
                + animalId + "', '" + adopterId + "', DATE '2026-08-20')");
    }

    private void insertStatusLog(String microchipId) throws SQLException {
        execute("INSERT INTO status_change_log (microchip_id, old_status, new_status, "
                + "change_date) VALUES ('" + microchipId + "', 'IN_OBSERVATION', "
                + "'READY_FOR_ADOPTION', CURRENT_TIMESTAMP)");
    }

    @Test
    void acceptsRecordsThatReferenceExistingRows() {
        assertDoesNotThrow(() -> {
            insertHealthRecord("CHIP-1");
            insertAdoption("CHIP-1", "ADO-1");
            insertStatusLog("CHIP-1");
        });
    }

    @Test
    void rejectsHealthRecordForUnknownAnimal() {
        assertThrows(SQLException.class, () -> insertHealthRecord("CHIP-UNKNOWN"));
    }

    @Test
    void rejectsAdoptionForUnknownAnimal() {
        assertThrows(SQLException.class, () -> insertAdoption("CHIP-UNKNOWN", "ADO-1"));
    }

    @Test
    void rejectsAdoptionForUnknownAdopter() {
        assertThrows(SQLException.class, () -> insertAdoption("CHIP-1", "ADO-UNKNOWN"));
    }

    @Test
    void rejectsAuditEntryForUnknownAnimal() {
        assertThrows(SQLException.class, () -> insertStatusLog("CHIP-UNKNOWN"));
    }

    @Test
    void deletingAnAnimalCascadesToItsHealthRecordsAndAuditTrail() throws SQLException {
        insertHealthRecord("CHIP-1");
        insertStatusLog("CHIP-1");
        assertEquals(1, count("health_records"));
        assertEquals(1, count("status_change_log"));

        execute("DELETE FROM animals WHERE microchip_id = 'CHIP-1'");

        assertEquals(0, count("health_records"));
        assertEquals(0, count("status_change_log"));
    }

    @Test
    void rejectsOccupancyAboveTheMaximumCapacity() {
        assertThrows(SQLException.class,
                () -> execute("UPDATE kennels SET occupied = 6 WHERE kennel_id = 'KENNEL-A'"));
    }

    @Test
    void rejectsNegativeOccupancy() {
        assertThrows(SQLException.class,
                () -> execute("UPDATE kennels SET occupied = -1 WHERE kennel_id = 'KENNEL-A'"));
    }

    @Test
    void allowsOccupancyUpToTheMaximumCapacity() {
        assertDoesNotThrow(
                () -> execute("UPDATE kennels SET occupied = 5 WHERE kennel_id = 'KENNEL-A'"));
    }
}
