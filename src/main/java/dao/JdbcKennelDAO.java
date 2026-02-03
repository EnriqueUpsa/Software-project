package dao;

import model.Kennel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcKennelDAO implements KennelDAO {
    private final Connection connection;

    public JdbcKennelDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Kennel kennel) {
        String sql = "INSERT INTO kennels (kennel_id, max_capacity, occupied) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kennel.getKennelId());
            stmt.setInt(2, kennel.getMaxCapacity());
            stmt.setInt(3, kennel.getOccupied());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save kennel", e);
        }
    }

    @Override
    public Optional<Kennel> findById(String kennelId) {
        String sql = "SELECT kennel_id, max_capacity, occupied FROM kennels WHERE kennel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kennelId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find kennel by id", e);
        }
    }

    @Override
    public List<Kennel> findAll() {
        String sql = "SELECT kennel_id, max_capacity, occupied FROM kennels";
        List<Kennel> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list kennels", e);
        }
    }

    @Override
    public void update(Kennel kennel) {
        String sql = "UPDATE kennels SET max_capacity = ?, occupied = ? WHERE kennel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, kennel.getMaxCapacity());
            stmt.setInt(2, kennel.getOccupied());
            stmt.setString(3, kennel.getKennelId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update kennel", e);
        }
    }

    private Kennel mapRow(ResultSet rs) throws SQLException {
        String kennelId = rs.getString("kennel_id");
        int maxCapacity = rs.getInt("max_capacity");
        int occupied = rs.getInt("occupied");
        return new Kennel(kennelId, maxCapacity, occupied);
    }
}
