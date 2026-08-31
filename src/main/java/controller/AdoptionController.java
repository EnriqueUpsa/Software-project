package controller;

import model.Adopter;
import model.Animal;
import service.AdopterService;
import service.AdoptionService;
import service.AnimalService;
import service.KennelService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for adopter management, compatibility checks and placements.
 */
public class AdoptionController {
    private final AdopterService adopterService;
    private final AnimalService animalService;
    private final AdoptionService adoptionService;
    private final KennelService kennelService;

    public AdoptionController(AdopterService adopterService,
                              AnimalService animalService,
                              AdoptionService adoptionService,
                              KennelService kennelService) {
        this.adopterService = adopterService;
        this.animalService = animalService;
        this.adoptionService = adoptionService;
        this.kennelService = kennelService;
    }

    public void registerAdopter(String adopterId,
                                String fullName,
                                String phone,
                                String preferredSpecies,
                                String preferredBreed) {
        adopterService.registerAdopter(new Adopter(
                adopterId,
                fullName,
                phone,
                preferredSpecies,
                preferredBreed
        ));
    }

    /**
     * Guided matching: the animals this adopter can actually take home.
     *
     * @param adopterId identifier of a registered adopter.
     * @return the compatible animals that are ready for adoption.
     */
    public List<Animal> listCandidatesForAdopter(String adopterId) {
        return adoptionService.findCandidatesFor(adopterId);
    }

    public boolean checkCompatibility(String animalMicrochipId, String adopterId) {
        Animal animal = animalService.getAnimalByMicrochip(animalMicrochipId);
        Adopter adopter = adopterService.getAdopterById(adopterId);
        return adopterService.isCompatible(animal, adopter);
    }

    public void processAdoption(String animalMicrochipId,
                                String adopterId,
                                LocalDate placementDate,
                                String sourceSpaceId) {
        adoptionService.processAdoption(animalMicrochipId, adopterId, placementDate);
        try {
            kennelService.releaseAnimalFromKennel(sourceSpaceId);
        } catch (IllegalStateException ignored) {
        }
    }
}
