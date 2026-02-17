package dao;

import model.Adopter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link AdopterDAO}.
 */
public class JdbcAdopterDAO implements AdopterDAO {
    private final Connection connection;

    public JdbcAdopterDAO(Connection connection) {
        this.connection = connection;
        createTableIfNeeded();
    }

    @Override
    public void save(Adopter adopter) {
        String sql = "INSERT INTO adopters "
                + "(adopter_id, full_name, phone, preferred_species, preferred_breed) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, adopter.getAdopterId());
            stmt.setString(2, adopter.getFullName());
            stmt.setString(3, adopter.getPhone());
            stmt.setString(4, adopter.getPreferredSpecies());
            stmt.setString(5, adopter.getPreferredBreed());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save adopter", e);
        }
    }

    @Override
    public Optional<Adopter> findById(String adopterId) {
        String sql = "SELECT adopter_id, full_name, phone, preferred_species, preferred_breed "
                + "FROM adopters WHERE adopter_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, adopterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find adopter", e);
        }
    }

    @Override
    public List<Adopter> findAll() {
        String sql = "SELECT adopter_id, full_name, phone, preferred_species, preferred_breed "
                + "FROM adopters";
        List<Adopter> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list adopters", e);
        }
    }

    private Adopter mapRow(ResultSet rs) throws SQLException {
        return new Adopter(
                rs.getString("adopter_id"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("preferred_species"),
                rs.getString("preferred_breed")
        );
    }

    private void createTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS adopters ("
                + "adopter_id VARCHAR(50) PRIMARY KEY, "
                + "full_name VARCHAR(150), "
                + "phone VARCHAR(40), "
                + "preferred_species VARCHAR(40), "
                + "preferred_breed VARCHAR(100)"
                + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create adopters table", e);
        }
    }
}
