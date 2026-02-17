package dao;

import model.Kennel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link KennelDAO}.
 */
public class JdbcKennelDAO implements KennelDAO {
    private final Connection connection;

    public JdbcKennelDAO(Connection connection) {
        this.connection = connection;
        createTableIfNeeded();
    }

    @Override
    public void save(Kennel kennel) {
        String sql = "INSERT INTO kennels (kennel_id, space_type, max_capacity, occupied) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kennel.getKennelId());
            stmt.setString(2, kennel.getSpaceType().name());
            stmt.setInt(3, kennel.getMaxCapacity());
            stmt.setInt(4, kennel.getOccupied());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save kennel", e);
        }
    }

    @Override
    public Optional<Kennel> findById(String kennelId) {
        String sql = "SELECT kennel_id, space_type, max_capacity, occupied "
                + "FROM kennels WHERE kennel_id = ?";
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
        String sql = "SELECT kennel_id, space_type, max_capacity, occupied FROM kennels";
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
        String sql = "UPDATE kennels SET space_type = ?, max_capacity = ?, occupied = ? "
                + "WHERE kennel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kennel.getSpaceType().name());
            stmt.setInt(2, kennel.getMaxCapacity());
            stmt.setInt(3, kennel.getOccupied());
            stmt.setString(4, kennel.getKennelId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update kennel", e);
        }
    }

    @Override
    public boolean incrementOccupiedIfAvailable(String kennelId) {
        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read auto-commit state", e);
        }

        try {
            connection.setAutoCommit(false);
            String updateSql = "UPDATE kennels SET occupied = occupied + 1 "
                    + "WHERE kennel_id = ? AND occupied < max_capacity";
            int updatedRows;
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setString(1, kennelId);
                updatedRows = updateStmt.executeUpdate();
            }

            if (updatedRows == 0) {
                if (findById(kennelId).isEmpty()) {
                    connection.rollback();
                    throw new IllegalArgumentException("Kennel not found");
                }
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw new RuntimeException("Failed to increment kennel occupancy", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }
    }

    @Override
    public boolean transferOneAnimal(String sourceKennelId, String destinationKennelId) {
        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read auto-commit state", e);
        }

        try {
            connection.setAutoCommit(false);

            String decrementSql = "UPDATE kennels SET occupied = occupied - 1 "
                    + "WHERE kennel_id = ? AND occupied > 0";
            int sourceUpdatedRows;
            try (PreparedStatement decrementStmt = connection.prepareStatement(decrementSql)) {
                decrementStmt.setString(1, sourceKennelId);
                sourceUpdatedRows = decrementStmt.executeUpdate();
            }

            if (sourceUpdatedRows == 0) {
                if (!existsById(sourceKennelId)) {
                    connection.rollback();
                    throw new IllegalArgumentException("Source kennel not found");
                }
                connection.rollback();
                throw new IllegalStateException("Source kennel is empty");
            }

            String incrementSql = "UPDATE kennels SET occupied = occupied + 1 "
                    + "WHERE kennel_id = ? AND occupied < max_capacity";
            int destinationUpdatedRows;
            try (PreparedStatement incrementStmt = connection.prepareStatement(incrementSql)) {
                incrementStmt.setString(1, destinationKennelId);
                destinationUpdatedRows = incrementStmt.executeUpdate();
            }

            if (destinationUpdatedRows == 0) {
                if (!existsById(destinationKennelId)) {
                    connection.rollback();
                    throw new IllegalArgumentException("Destination kennel not found");
                }
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw new RuntimeException("Failed to transfer kennel occupancy", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }
    }

    private Kennel mapRow(ResultSet rs) throws SQLException {
        String kennelId = rs.getString("kennel_id");
        String spaceTypeValue = rs.getString("space_type");
        Kennel.SpaceType spaceType = spaceTypeValue == null
                ? Kennel.SpaceType.KENNEL
                : Kennel.SpaceType.valueOf(spaceTypeValue);
        int maxCapacity = rs.getInt("max_capacity");
        int occupied = rs.getInt("occupied");
        return new Kennel(kennelId, spaceType, maxCapacity, occupied);
    }

    private void createTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS kennels ("
                + "kennel_id VARCHAR(50) PRIMARY KEY, "
                + "space_type VARCHAR(30), "
                + "max_capacity INT, "
                + "occupied INT"
                + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
            try (PreparedStatement alterStmt = connection.prepareStatement(
                    "ALTER TABLE kennels ADD COLUMN IF NOT EXISTS space_type VARCHAR(30)")) {
                alterStmt.execute();
            }
            try (PreparedStatement backfillStmt = connection.prepareStatement(
                    "UPDATE kennels SET space_type = 'KENNEL' WHERE space_type IS NULL")) {
                backfillStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create kennels table", e);
        }
    }

    private boolean existsById(String kennelId) {
        String sql = "SELECT 1 FROM kennels WHERE kennel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kennelId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify kennel existence", e);
        }
    }
}
