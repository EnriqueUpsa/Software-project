package model;

public class Kennel {
    private String kennelId;
    private int maxCapacity;
    private int occupied;

    public Kennel(String kennelId, int maxCapacity) {
        validateKennelId(kennelId);
        validateCapacity(maxCapacity);
        this.kennelId = kennelId;
        this.maxCapacity = maxCapacity;
        this.occupied = 0;
    }

    public Kennel(String kennelId, int maxCapacity, int occupied) {
        validateKennelId(kennelId);
        validateCapacity(maxCapacity);
        validateOccupied(occupied, maxCapacity);
        this.kennelId = kennelId;
        this.maxCapacity = maxCapacity;
        this.occupied = occupied;
    }

    public String getKennelId() {
        return kennelId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public int getAvailableCapacity() {
        return maxCapacity - occupied;
    }

    public void setMaxCapacity(int maxCapacity) {
        validateCapacity(maxCapacity);
        validateOccupied(this.occupied, maxCapacity);
        this.maxCapacity = maxCapacity;
    }

    public void setOccupied(int occupied) {
        validateOccupied(occupied, this.maxCapacity);
        this.occupied = occupied;
    }

    private void validateKennelId(String kennelId) {
        if (kennelId == null || kennelId.isBlank()) {
            throw new IllegalArgumentException("Kennel ID is required");
        }
    }

    private void validateCapacity(int maxCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("Capacity must be >= 0");
        }
    }

    private void validateOccupied(int occupied, int maxCapacity) {
        if (occupied < 0 || occupied > maxCapacity) {
            throw new IllegalArgumentException("Occupied must be between 0 and capacity");
        }
    }
}
