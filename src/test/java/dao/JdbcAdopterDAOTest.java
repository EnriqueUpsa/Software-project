package dao;

import model.Adopter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link JdbcAdopterDAO} against a real H2 database.
 */
class JdbcAdopterDAOTest {

    private Connection connection;
    private JdbcAdopterDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = JdbcTestSupport.openDatabase();
        dao = new JdbcAdopterDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void savesAndReadsBackTheAdopterWithItsPreferences() {
        dao.save(new Adopter("ADO-1", "Giulia Rossi", "3290000000", "Dog", "Beagle"));

        Optional<Adopter> found = dao.findById("ADO-1");

        assertTrue(found.isPresent());
        Adopter stored = found.get();
        assertEquals("Giulia Rossi", stored.getFullName());
        assertEquals("3290000000", stored.getPhone());
        assertEquals("Dog", stored.getPreferredSpecies());
        assertEquals("Beagle", stored.getPreferredBreed());
    }

    @Test
    void storesAnAdopterWithoutBreedPreference() {
        dao.save(new Adopter("ADO-2", "Marco Bianchi", "3291111111", "Cat", null));

        Adopter stored = dao.findById("ADO-2").orElseThrow();

        assertEquals("Cat", stored.getPreferredSpecies());
        assertEquals("", stored.getPreferredBreed());
    }

    @Test
    void returnsEmptyForAnUnknownAdopter() {
        assertFalse(dao.findById("ADO-UNKNOWN").isPresent());
    }

    @Test
    void listsEveryRegisteredAdopter() {
        dao.save(new Adopter("ADO-3", "Giulia Rossi", "3290000000", "Dog", ""));
        dao.save(new Adopter("ADO-4", "Marco Bianchi", "3291111111", "Cat", ""));

        assertEquals(2, dao.findAll().size());
    }
}
