package model;

/**
 * Physical shelter unit with capacity and occupancy tracking.
 */
public class Kennel {
    public enum SpaceType {
        KENNEL("Kennel"),
        CAGE("Cage"),
        FENCED_AREA("Fenced area");

        private final String label;

        SpaceType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private String kennelId;
    private SpaceType spaceType;
    private int maxCapacity;
    private int occupied;

    public Kennel(String kennelId, int maxCapacity) {
        this(kennelId, SpaceType.KENNEL, maxCapacity, 0);
    }

    public Kennel(String kennelId, SpaceType spaceType, int maxCapacity) {
        this(kennelId, spaceType, maxCapacity, 0);
    }

    public Kennel(String kennelId, int maxCapacity, int occupied) {
        this(kennelId, SpaceType.KENNEL, maxCapacity, occupied);
    }

    public Kennel(String kennelId, SpaceType spaceType, int maxCapacity, int occupied) {
        validateKennelId(kennelId);
        validateSpaceType(spaceType);
        validateCapacity(maxCapacity);
        validateOccupied(occupied, maxCapacity);
        this.kennelId = kennelId;
        this.spaceType = spaceType;
        this.maxCapacity = maxCapacity;
        this.occupied = occupied;
    }

    public String getKennelId() {
        return kennelId;
    }

    public SpaceType getSpaceType() {
        return spaceType;
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

    public void setSpaceType(SpaceType spaceType) {
        validateSpaceType(spaceType);
        this.spaceType = spaceType;
    }

    private void validateKennelId(String kennelId) {
        if (kennelId == null || kennelId.isBlank()) {
            throw new IllegalArgumentException("Kennel ID is required");
        }
    }

    private void validateSpaceType(SpaceType spaceType) {
        if (spaceType == null) {
            throw new IllegalArgumentException("Space type is required");
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
