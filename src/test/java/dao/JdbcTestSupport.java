package dao;

import model.Animal;
import model.Dog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Shared setup for the JDBC data access tests.
 *
 * <p>Every test opens its own in-memory H2 database and builds the production
 * schema on it with {@link SchemaInitializer}. The DAOs are therefore exercised
 * against a real database, with the real foreign keys, instead of against a
 * test double: a query that does not match the schema fails here.</p>
 */
final class JdbcTestSupport {

    private JdbcTestSupport() {
    }

    /**
     * Opens an isolated database and creates the schema on it.
     *
     * @return an open connection; the caller is responsible for closing it.
     * @throws SQLException if the database cannot be opened.
     */
    static Connection openDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:dao_" + System.nanoTime(), "sa", "");
        SchemaInitializer.initialize(connection);
        return connection;
    }

    /**
     * Registers an animal so that the rows referencing it satisfy the foreign
     * keys.
     *
     * @param connection open connection.
     * @param microchipId microchip of the animal to create.
     * @return the animal that was stored.
     */
    static Animal givenAnimal(Connection connection, String microchipId) {
        Animal animal = new Dog(microchipId, "Beagle", LocalDate.of(2026, 8, 1),
                3, Animal.HealthStatus.UNDER_OBSERVATION, "photos/beagle.jpg",
                Animal.Status.READY_FOR_ADOPTION);
        new JdbcAnimalDAO(connection).save(animal);
        return animal;
    }
}
