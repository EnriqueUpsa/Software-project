package dao;

import model.Adoption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.EnumMap;
import java.util.Map;

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

    @Override
    public Map<Month, Integer> getMonthlyAdoptions(int year) {
        String sql = "SELECT MONTH(placement_date) AS adoption_month, COUNT(*) AS total "
                + "FROM adoptions WHERE YEAR(placement_date) = ? "
                + "GROUP BY MONTH(placement_date)";
        Map<Month, Integer> results = new EnumMap<>(Month.class);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int monthValue = rs.getInt("adoption_month");
                    Month month = Month.of(monthValue);
                    results.put(month, rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load adoption statistics", e);
        }

        return results;
    }
}
