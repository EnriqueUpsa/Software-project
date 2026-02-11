package dao;

import model.Adoption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcAdoptionDAO implements AdoptionDAO {
    private final Connection connection;

    public JdbcAdoptionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Adoption adoption) {
        String sql = "INSERT INTO adoptions (animal_id, adopter_id, placement_date) "
                + "VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, adoption.getAnimalId());
            stmt.setString(2, adoption.getAdopterId());
            stmt.setDate(3, java.sql.Date.valueOf(adoption.getPlacementDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save adoption", e);
        }
    }
}
