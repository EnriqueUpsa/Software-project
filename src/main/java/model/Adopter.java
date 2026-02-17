package model;

/**
 * Potential adopter profile used to match with available animals.
 */
public class Adopter {
    private final String adopterId;
    private final String fullName;
    private final String phone;
    private final String preferredSpecies;
    private final String preferredBreed;

    public Adopter(String adopterId,
                   String fullName,
                   String phone,
                   String preferredSpecies,
                   String preferredBreed) {
        if (adopterId == null || adopterId.isBlank()) {
            throw new IllegalArgumentException("Adopter ID is required");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone is required");
        }
        this.adopterId = adopterId;
        this.fullName = fullName;
        this.phone = phone;
        this.preferredSpecies = preferredSpecies == null ? "" : preferredSpecies.trim();
        this.preferredBreed = preferredBreed == null ? "" : preferredBreed.trim();
    }

    public String getAdopterId() {
        return adopterId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPreferredSpecies() {
        return preferredSpecies;
    }

    public String getPreferredBreed() {
        return preferredBreed;
    }
}
