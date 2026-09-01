package dao;

import model.Adopter;
import model.Adoption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcAdoptionDAO} against a real H2 database.
 */
class JdbcAdoptionDAOTest {

    private Connection connection;
    private JdbcAdoptionDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        JdbcTestSupport.givenAnimal(connection, "MC-1");
        new JdbcAdopterDAO(connection)
                .save(new Adopter("ADO-1", "Giulia Rossi", "3290000000", "Dog", ""));
        dao = new JdbcAdoptionDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void countsThePlacementInTheMonthItHappened() {
        dao.save(new Adoption("MC-1", "ADO-1", LocalDate.of(2026, 5, 20)));

        Map<Month, Integer> monthly = dao.getMonthlyAdoptions(2026);

        assertEquals(1, monthly.get(Month.MAY));
    }

    @Test
    void addsUpSeveralPlacementsInTheSameMonth() {
        JdbcTestSupport.givenAnimal(connection, "MC-2");
        dao.save(new Adoption("MC-1", "ADO-1", LocalDate.of(2026, 5, 3)));
        dao.save(new Adoption("MC-2", "ADO-1", LocalDate.of(2026, 5, 28)));

        assertEquals(2, dao.getMonthlyAdoptions(2026).get(Month.MAY));
    }

    @Test
    void ignoresPlacementsOfAnotherYear() {
        dao.save(new Adoption("MC-1", "ADO-1", LocalDate.of(2025, 5, 20)));

        assertTrue(dao.getMonthlyAdoptions(2026).isEmpty());
    }

    @Test
    void leavesMonthsWithoutPlacementsOutOfTheReport() {
        dao.save(new Adoption("MC-1", "ADO-1", LocalDate.of(2026, 5, 20)));

        assertNull(dao.getMonthlyAdoptions(2026).get(Month.JUNE));
    }

    @Test
    void rejectsAPlacementOfAnAnimalThatIsNotRegistered() {
        assertThrows(RuntimeException.class,
                () -> dao.save(new Adoption("MC-UNKNOWN", "ADO-1", LocalDate.of(2026, 5, 20))));
    }

    @Test
    void rejectsAPlacementWithAnAdopterThatIsNotRegistered() {
        assertThrows(RuntimeException.class,
                () -> dao.save(new Adoption("MC-1", "ADO-UNKNOWN", LocalDate.of(2026, 5, 20))));
    }
}
