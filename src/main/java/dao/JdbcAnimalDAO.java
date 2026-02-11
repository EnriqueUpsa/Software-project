package dao;

import model.Animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcAnimalDAO implements AnimalDAO {
    private final Connection connection;

    public JdbcAnimalDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Animal animal) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Animal> findByMicrochipId(String microchipId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Animal> findAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void update(Animal animal) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Map<String, Integer> getStatusDistribution() {
        String sql = "SELECT status, COUNT(*) AS total FROM animals GROUP BY status";
        Map<String, Integer> results = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.put(rs.getString("status"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load status distribution", e);
        }

        return results;
    }
}
