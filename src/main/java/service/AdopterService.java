package service;

import dao.AdopterDAO;
import model.Adopter;
import model.Animal;

import java.util.List;

/**
 * Business rules for adopter registration and compatibility checks.
 */
public class AdopterService {
    private final AdopterDAO adopterDAO;

    public AdopterService(AdopterDAO adopterDAO) {
        this.adopterDAO = adopterDAO;
    }

    public void registerAdopter(Adopter adopter) {
        if (adopter == null) {
            throw new IllegalArgumentException("Adopter is required");
        }
        if (adopterDAO.findById(adopter.getAdopterId()).isPresent()) {
            throw new IllegalArgumentException("Adopter ID already exists");
        }
        adopterDAO.save(adopter);
    }

    public Adopter getAdopterById(String adopterId) {
        if (adopterId == null || adopterId.isBlank()) {
            throw new IllegalArgumentException("Adopter ID is required");
        }
        return adopterDAO.findById(adopterId)
                .orElseThrow(() -> new IllegalArgumentException("Adopter not found"));
    }

    public List<Adopter> getAllAdopters() {
        return adopterDAO.findAll();
    }

    public boolean isCompatible(Animal animal, Adopter adopter) {
        if (animal == null || adopter == null) {
            return false;
        }

        String preferredSpecies = adopter.getPreferredSpecies();
        if (!preferredSpecies.isBlank()
                && !preferredSpecies.equalsIgnoreCase(animal.getSpecies())) {
            return false;
        }

        String preferredBreed = adopter.getPreferredBreed();
        return preferredBreed.isBlank()
                || preferredBreed.equalsIgnoreCase(animal.getBreed());
    }
}
