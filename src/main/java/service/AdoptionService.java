package service;

import model.Animal;

public class AdoptionService {

    public void validateAdoptionEligibility(Animal animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is required");
        }

        if (animal.getStatus() != Animal.Status.READY_FOR_ADOPTION) {
            throw new IllegalArgumentException(
                    "Animal is not ready for adoption");
        }
    }
}
