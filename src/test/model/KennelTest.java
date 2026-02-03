package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KennelTest {
    @Test
    void constructor_rejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> new Kennel(" ", 1));
    }

    @Test
    void constructor_rejectsNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Kennel("K-1", -1));
    }

    @Test
    void constructor_rejectsOccupiedAboveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Kennel("K-1", 1, 2));
    }

    @Test
    void setMaxCapacity_rejectsBelowOccupied() {
        Kennel kennel = new Kennel("K-1", 3, 2);
        assertThrows(IllegalArgumentException.class, () -> kennel.setMaxCapacity(1));
    }

    @Test
    void setOccupied_rejectsAboveCapacity() {
        Kennel kennel = new Kennel("K-1", 2);
        assertThrows(IllegalArgumentException.class, () -> kennel.setOccupied(3));
    }
}
