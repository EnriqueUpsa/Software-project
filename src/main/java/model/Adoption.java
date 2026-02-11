package model;

import java.time.LocalDate;

/**
 * Represents an adoption placement for a specific animal.
 */
public class Adoption {
    private final String animalId;
    private final String adopterId;
    private final LocalDate placementDate;

    public Adoption(String animalId, String adopterId, LocalDate placementDate) {
        this.animalId = animalId;
        this.adopterId = adopterId;
        this.placementDate = placementDate;
    }

    public String getAnimalId() {
        return animalId;
    }

    public String getAdopterId() {
        return adopterId;
    }

    public LocalDate getPlacementDate() {
        return placementDate;
    }
}
