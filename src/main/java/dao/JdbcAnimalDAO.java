package dao;

import model.Animal;
import model.Cat;
import model.Dog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC implementation of {@link AnimalDAO}.
 */
public class JdbcAnimalDAO implements AnimalDAO {
    private final Connection connection;

    public JdbcAnimalDAO(Connection connection) {
        this.connection = connection;
        createTableIfNeeded();
    }

    @Override
    public void save(Animal animal) {
        validateAnimalForPersistence(animal);
        String sql = "INSERT INTO animals "
                + "(microchip_id, animal_type, breed, intake_date, estimated_age, "
                + "health_status, photo_path, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, animal.getMicrochipId());
            stmt.setString(2, resolveAnimalType(animal));
            stmt.setString(3, animal.getBreed());
            stmt.setDate(4, Date.valueOf(animal.getIntakeDate()));
            stmt.setInt(5, animal.getEstimatedAgeYears());
            stmt.setString(6, animal.getHealthStatus().name());
            stmt.setString(7, animal.getPhotoPath());
            stmt.setString(8, animal.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save animal", e);
        }
    }

    @Override
    public Optional<Animal> findByMicrochipId(String microchipId) {
        String sql = "SELECT microchip_id, animal_type, breed, intake_date, estimated_age, "
                + "health_status, photo_path, status "
                + "FROM animals WHERE microchip_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, microchipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find animal by microchip", e);
        }
    }

    @Override
    public List<Animal> findAll() {
        String sql = "SELECT microchip_id, animal_type, breed, intake_date, estimated_age, "
                + "health_status, photo_path, status FROM animals";
        List<Animal> animals = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                animals.add(mapRow(rs));
            }
            return animals;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load animals", e);
        }
    }

    @Override
    public void update(Animal animal) {
        validateAnimalForPersistence(animal);
        String sql = "UPDATE animals "
                + "SET animal_type = ?, breed = ?, intake_date = ?, estimated_age = ?, "
                + "health_status = ?, photo_path = ?, status = ? "
                + "WHERE microchip_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, resolveAnimalType(animal));
            stmt.setString(2, animal.getBreed());
            stmt.setDate(3, Date.valueOf(animal.getIntakeDate()));
            stmt.setInt(4, animal.getEstimatedAgeYears());
            stmt.setString(5, animal.getHealthStatus().name());
            stmt.setString(6, animal.getPhotoPath());
            stmt.setString(7, animal.getStatus().name());
            stmt.setString(8, animal.getMicrochipId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update animal", e);
        }
    }

    @Override
    public Map<String, Integer> getStatusDistribution() {
        String sql = "SELECT status, COUNT(*) AS total FROM animals GROUP BY status";
        Map<String, Integer> results = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String statusValue = rs.getString("status");
                String label;
                try {
                    label = Animal.Status.valueOf(statusValue).toString();
                } catch (IllegalArgumentException ex) {
                    label = statusValue;
                }
                int updatedCount = results.getOrDefault(label, 0) + rs.getInt("total");
                results.put(label, updatedCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load status distribution", e);
        }

        return results;
    }

    private Animal mapRow(ResultSet rs) throws SQLException {
        String microchipId = rs.getString("microchip_id");
        String animalType = rs.getString("animal_type");
        String breed = rs.getString("breed");
        Date intakeDateValue = rs.getDate("intake_date");
        LocalDate intakeDate = intakeDateValue == null ? null : intakeDateValue.toLocalDate();
        int estimatedAge = rs.getInt("estimated_age");
        String healthStatusValue = rs.getString("health_status");
        String photoPath = rs.getString("photo_path");
        Animal.Status status = Animal.Status.valueOf(rs.getString("status"));
        Animal.HealthStatus healthStatus = healthStatusValue == null
                ? Animal.HealthStatus.UNDER_OBSERVATION
                : Animal.HealthStatus.valueOf(healthStatusValue);
        if (photoPath == null || photoPath.isBlank()) {
            photoPath = "photos/unknown.jpg";
        }

        if (intakeDate == null) {
            throw new IllegalStateException("Animal intake date cannot be null");
        }

        if ("CAT".equalsIgnoreCase(animalType)) {
            return new Cat(microchipId, breed, intakeDate,
                    estimatedAge, healthStatus, photoPath, status);
        }
        return new Dog(microchipId, breed, intakeDate,
                estimatedAge, healthStatus, photoPath, status);
    }

    private String resolveAnimalType(Animal animal) {
        if (animal instanceof Cat) {
            return "CAT";
        }
        if (animal instanceof Dog) {
            return "DOG";
        }
        return animal.getClass().getSimpleName().toUpperCase();
    }

    private void createTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS animals ("
                + "microchip_id VARCHAR(50) PRIMARY KEY, "
                + "animal_type VARCHAR(20), "
                + "breed VARCHAR(100), "
                + "intake_date DATE, "
                + "estimated_age INT, "
                + "health_status VARCHAR(50), "
                + "photo_path VARCHAR(255), "
                + "status VARCHAR(50)"
                + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create animals table", e);
        }
    }

    private void validateAnimalForPersistence(Animal animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is required");
        }
        if (animal.getMicrochipId() == null || animal.getMicrochipId().isBlank()) {
            throw new IllegalArgumentException("Microchip ID is required");
        }
        if (animal.getIntakeDate() == null) {
            throw new IllegalArgumentException("Intake date is required");
        }
        if (animal.getHealthStatus() == null) {
            throw new IllegalArgumentException("Health status is required");
        }
        if (animal.getPhotoPath() == null || animal.getPhotoPath().isBlank()) {
            throw new IllegalArgumentException("Photo path is required");
        }
    }
}
