package controller;

import dao.KennelDAO;
import model.Animal;
import model.Cat;
import model.Dog;
import model.Kennel;
import service.AnimalService;
import service.KennelService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for animal intake and status transitions.
 */
public class IntakeController {
    /**
     * Immutable DTO with intake form input values.
     */
    public record IntakeRequest(String microchipId,
                                String breed,
                                String estimatedAge,
                                String photoPath,
                                LocalDate intakeDate,
                                String type,
                                Animal.HealthStatus healthStatus,
                                String intakeSpaceId) {
    }

    private final AnimalService animalService;
    private final KennelService kennelService;
    private final KennelDAO kennelDAO;

    public IntakeController(AnimalService animalService,
                            KennelService kennelService,
                            KennelDAO kennelDAO) {
        this.animalService = animalService;
        this.kennelService = kennelService;
        this.kennelDAO = kennelDAO;
    }

    public void registerAnimal(IntakeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Intake request is required");
        }

        Animal animal = buildAnimalFromRequest(request);

        kennelService.assignAnimalToKennel(request.intakeSpaceId());
        try {
            animalService.registerAnimal(animal);
        } catch (RuntimeException ex) {
            try {
                kennelService.releaseAnimalFromKennel(request.intakeSpaceId());
            } catch (RuntimeException ignored) {
            }
            throw ex;
        }
    }

    public void updateStatus(String microchipId, Animal.Status status) {
        animalService.updateStatus(microchipId, status);
    }

    /**
     * Lists the animals of the shelter for the registry table.
     *
     * <p>The most recent intakes are shown first, which is the order the
     * reception desk works in.</p>
     *
     * @return animals ordered by intake date, newest first.
     */
    public List<Animal> listAnimals() {
        List<Animal> animals = new ArrayList<>(animalService.getAllAnimals());
        animals.sort(Comparator.comparing(Animal::getIntakeDate).reversed());
        return animals;
    }

    public String getOccupancyText(String spaceId) {
        if (spaceId == null || spaceId.isBlank()) {
            throw new IllegalArgumentException("Space ID is required");
        }

        Kennel kennel = kennelDAO.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        return kennel.getOccupied() + " / " + kennel.getMaxCapacity();
    }

    private Animal buildAnimalFromRequest(IntakeRequest request) {
        if (request.microchipId() == null || request.microchipId().isBlank()
                || request.breed() == null || request.breed().isBlank()
                || request.estimatedAge() == null || request.estimatedAge().isBlank()
                || request.photoPath() == null || request.photoPath().isBlank()
                || request.intakeDate() == null
                || request.type() == null || request.type().isBlank()
                || request.healthStatus() == null
                || request.intakeSpaceId() == null || request.intakeSpaceId().isBlank()) {
            throw new IllegalArgumentException("All intake fields are required");
        }

        int age;
        try {
            age = Integer.parseInt(request.estimatedAge().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Estimated age must be numeric");
        }

        if ("Dog".equalsIgnoreCase(request.type())) {
            return new Dog(
                    request.microchipId(),
                    request.breed(),
                    request.intakeDate(),
                    age,
                    request.healthStatus(),
                    request.photoPath(),
                    Animal.Status.IN_OBSERVATION
            );
        }

        return new Cat(
                request.microchipId(),
                request.breed(),
                request.intakeDate(),
                age,
                request.healthStatus(),
                request.photoPath(),
                Animal.Status.IN_OBSERVATION
        );
    }
}
