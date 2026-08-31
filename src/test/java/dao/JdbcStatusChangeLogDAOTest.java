package dao;

import model.Animal;
import model.StatusChangeLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcStatusChangeLogDAO} against a real H2 database.
 */
class JdbcStatusChangeLogDAOTest {

    private Connection connection;
    private JdbcStatusChangeLogDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        JdbcTestSupport.givenAnimal(connection, "MC-1");
        dao = new JdbcStatusChangeLogDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void savesAndReadsBackTheTransition() {
        LocalDateTime moment = LocalDateTime.of(2026, 8, 20, 10, 30);
        dao.save(new StatusChangeLog("MC-1", Animal.Status.IN_OBSERVATION,
                Animal.Status.READY_FOR_ADOPTION, moment));

        List<StatusChangeLog> history = dao.findByAnimal("MC-1");

        assertEquals(1, history.size());
        StatusChangeLog entry = history.get(0);
        assertEquals("MC-1", entry.getMicrochipId());
        assertEquals(moment, entry.getTimestamp());
    }

    @Test
    void keepsTheWholeHistoryOfAnAnimal() {
        dao.save(new StatusChangeLog("MC-1", Animal.Status.IN_OBSERVATION,
                Animal.Status.UNDER_TREATMENT, LocalDateTime.of(2026, 8, 20, 10, 0)));
        dao.save(new StatusChangeLog("MC-1", Animal.Status.UNDER_TREATMENT,
                Animal.Status.READY_FOR_ADOPTION, LocalDateTime.of(2026, 8, 25, 10, 0)));
        dao.save(new StatusChangeLog("MC-1", Animal.Status.READY_FOR_ADOPTION,
                Animal.Status.ADOPTED, LocalDateTime.of(2026, 8, 30, 10, 0)));

        assertEquals(3, dao.findByAnimal("MC-1").size());
    }

    @Test
    void acceptsAnEntryWithoutAPreviousStatus() {
        dao.save(new StatusChangeLog("MC-1", null,
                Animal.Status.IN_OBSERVATION, LocalDateTime.of(2026, 8, 20, 10, 0)));

        assertEquals(1, dao.findByAnimal("MC-1").size());
    }

    @Test
    void returnsOnlyTheHistoryOfTheRequestedAnimal() {
        JdbcTestSupport.givenAnimal(connection, "MC-2");
        dao.save(new StatusChangeLog("MC-1", Animal.Status.IN_OBSERVATION,
                Animal.Status.READY_FOR_ADOPTION, LocalDateTime.of(2026, 8, 20, 10, 0)));
        dao.save(new StatusChangeLog("MC-2", Animal.Status.IN_OBSERVATION,
                Animal.Status.UNDER_TREATMENT, LocalDateTime.of(2026, 8, 21, 10, 0)));

        assertEquals(1, dao.findByAnimal("MC-1").size());
        assertTrue(dao.findByAnimal("MC-3").isEmpty());
    }

    @Test
    void rejectsAnEntryForAnAnimalThatIsNotRegistered() {
        assertThrows(RuntimeException.class,
                () -> dao.save(new StatusChangeLog("MC-UNKNOWN", Animal.Status.IN_OBSERVATION,
                        Animal.Status.ADOPTED, LocalDateTime.of(2026, 8, 20, 10, 0))));
    }
}
