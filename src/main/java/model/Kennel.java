package model;

public class Kennel {
    private String kennelId;
    private int capacity;
    private int occupied;

    public Kennel(String kennelId, int capacity) {
        validateKennelId(kennelId);
        validateCapacity(capacity);
        this.kennelId = kennelId;
        this.capacity = capacity;
        this.occupied = 0;
    }

    public Kennel(String kennelId, int capacity, int occupied) {
        validateKennelId(kennelId);
        validateCapacity(capacity);
        validateOccupied(occupied, capacity);
        this.kennelId = kennelId;
        this.capacity = capacity;
        this.occupied = occupied;
    }

    public String getKennelId() {
        return kennelId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public int getAvailableCapacity() {
        return capacity - occupied;
    }

    public void setCapacity(int capacity) {
        validateCapacity(capacity);
        validateOccupied(this.occupied, capacity);
        this.capacity = capacity;
    }

    public void setOccupied(int occupied) {
        validateOccupied(occupied, this.capacity);
        this.occupied = occupied;
    }

    private void validateKennelId(String kennelId) {
        if (kennelId == null || kennelId.isBlank()) {
            throw new IllegalArgumentException("Kennel ID is required");
        }
    }

    private void validateCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be >= 0");
        }
    }

    private void validateOccupied(int occupied, int capacity) {
        if (occupied < 0 || occupied > capacity) {
            throw new IllegalArgumentException("Occupied must be between 0 and capacity");
        }
    }
}
