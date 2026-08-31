package dao;

import model.Kennel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcKennelDAO} against a real H2 database, including the
 * transactional operations that protect the shelter from overcrowding.
 */
class JdbcKennelDAOTest {

    private Connection connection;
    private JdbcKennelDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        dao = new JdbcKennelDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void savesAndReadsBackTheShelterSpace() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 5, 2));

        Kennel stored = dao.findById("KENNEL-A").orElseThrow();

        assertEquals(Kennel.SpaceType.KENNEL, stored.getSpaceType());
        assertEquals(5, stored.getMaxCapacity());
        assertEquals(2, stored.getOccupied());
        assertEquals(3, stored.getAvailableCapacity());
    }

    @Test
    void keepsTheThreeSpaceTypesOfTheSpecification() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 5));
        dao.save(new Kennel("CAGE-A", Kennel.SpaceType.CAGE, 4));
        dao.save(new Kennel("FENCED-A", Kennel.SpaceType.FENCED_AREA, 8));

        assertEquals(Kennel.SpaceType.KENNEL, dao.findById("KENNEL-A").orElseThrow().getSpaceType());
        assertEquals(Kennel.SpaceType.CAGE, dao.findById("CAGE-A").orElseThrow().getSpaceType());
        assertEquals(Kennel.SpaceType.FENCED_AREA, dao.findById("FENCED-A").orElseThrow().getSpaceType());
        assertEquals(3, dao.findAll().size());
    }

    @Test
    void returnsEmptyForAnUnknownSpace() {
        assertFalse(dao.findById("KENNEL-UNKNOWN").isPresent());
    }

    @Test
    void incrementsTheOccupancyWhileThereIsRoom() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 2, 1));

        assertTrue(dao.incrementOccupiedIfAvailable("KENNEL-A"));
        assertEquals(2, dao.findById("KENNEL-A").orElseThrow().getOccupied());
    }

    @Test
    void refusesToExceedTheMaximumCapacity() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 2, 2));

        assertFalse(dao.incrementOccupiedIfAvailable("KENNEL-A"));
        assertEquals(2, dao.findById("KENNEL-A").orElseThrow().getOccupied());
    }

    @Test
    void movesOneAnimalBetweenTwoSpaces() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 5, 3));
        dao.save(new Kennel("CAGE-A", Kennel.SpaceType.CAGE, 5, 0));

        assertTrue(dao.transferOneAnimal("KENNEL-A", "CAGE-A"));
        assertEquals(2, dao.findById("KENNEL-A").orElseThrow().getOccupied());
        assertEquals(1, dao.findById("CAGE-A").orElseThrow().getOccupied());
    }

    @Test
    void leavesBothSpacesUntouchedWhenTheDestinationIsFull() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 5, 3));
        dao.save(new Kennel("CAGE-A", Kennel.SpaceType.CAGE, 1, 1));

        assertFalse(dao.transferOneAnimal("KENNEL-A", "CAGE-A"));
        assertEquals(3, dao.findById("KENNEL-A").orElseThrow().getOccupied());
        assertEquals(1, dao.findById("CAGE-A").orElseThrow().getOccupied());
    }

    @Test
    void updatesTheStoredSpace() {
        dao.save(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 5, 1));

        dao.update(new Kennel("KENNEL-A", Kennel.SpaceType.KENNEL, 9, 4));

        Kennel stored = dao.findById("KENNEL-A").orElseThrow();
        assertEquals(9, stored.getMaxCapacity());
        assertEquals(4, stored.getOccupied());
        assertEquals(1, dao.findAll().size());
    }
}
