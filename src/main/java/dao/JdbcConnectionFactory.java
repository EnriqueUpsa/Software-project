package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Centralized JDBC connection factory for the desktop app.
 */
public final class JdbcConnectionFactory {
    private JdbcConnectionFactory() {
    }

    public static Connection createH2FileConnection() {
        try {
            Files.createDirectories(Path.of("data"));
            Class.forName("org.h2.Driver");
            String url = "jdbc:h2:file:./data/petshelter;DB_CLOSE_ON_EXIT=FALSE";
            return DriverManager.getConnection(url, "sa", "");
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to create data directory", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "H2 driver not found. Add dependency com.h2database:h2", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open H2 JDBC connection", e);
        }
    }
}
