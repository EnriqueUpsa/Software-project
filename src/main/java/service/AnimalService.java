package service;

import dao.AnimalDAO;
import dao.InMemoryStatusChangeLogDAO;
import dao.StatusChangeLogDAO;
import model.Animal;
import model.StatusChangeLog;
import util.LoggerConfig;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Application service for animal intake, status updates and audit history.
 */
public class AnimalService {
    private static final Logger logger =
            LoggerConfig.getLogger(AnimalService.class);

    private final AnimalDAO animalDAO;
    private final StatusChangeLogDAO statusChangeLogDAO;

    public AnimalService(AnimalDAO animalDAO) {
        this(animalDAO, new InMemoryStatusChangeLogDAO());
    }

    public AnimalService(AnimalDAO animalDAO, StatusChangeLogDAO statusChangeLogDAO) {
        this.animalDAO = animalDAO;
        this.statusChangeLogDAO = statusChangeLogDAO;
    }

    public void registerAnimal(Animal animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is required");
        }
        validateMicrochipId(animal.getMicrochipId());

        logger.info("event=intake_register_start microchipId=" + animal.getMicrochipId()
                + " species=" + animal.getSpecies()
                + " status=" + animal.getStatus().name());

        if (animalDAO.findByMicrochipId(animal.getMicrochipId()).isPresent()) {
            logger.warning("event=intake_register_rejected reason=duplicate_microchip microchipId="
                    + animal.getMicrochipId());
            throw new IllegalArgumentException("Microchip ID already exists");
        }

        animalDAO.save(animal);
        statusChangeLogDAO.save(new StatusChangeLog(
                animal.getMicrochipId(),
                null,
                animal.getStatus()
        ));

        logger.info("event=intake_register_success microchipId=" + animal.getMicrochipId());
    }

    public Map<String, Integer> getAnimalStatusDistribution() {
        return animalDAO.getStatusDistribution();
    }

    public List<Animal> getAllAnimals() {
        return animalDAO.findAll();
    }

    public Animal getAnimalByMicrochip(String microchipId) {
        validateMicrochipId(microchipId);
        return animalDAO.findByMicrochipId(microchipId)
                .orElseThrow(() -> new IllegalArgumentException("Animal not found"));
    }

    public void updateStatus(Animal animal, Animal.Status newStatus) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }
        validateMicrochipId(animal.getMicrochipId());

        Animal persistedAnimal = animalDAO.findByMicrochipId(animal.getMicrochipId())
                .orElseThrow(() -> new IllegalArgumentException("Animal not found"));
        applyStatusUpdate(persistedAnimal, newStatus);
        if (persistedAnimal != animal) {
            animal.setStatus(newStatus);
        }
    }

    public void updateStatus(String microchipId, Animal.Status newStatus) {
        validateMicrochipId(microchipId);
        Animal animal = animalDAO.findByMicrochipId(microchipId)
                .orElseThrow(() -> new IllegalArgumentException("Animal not found"));
        applyStatusUpdate(animal, newStatus);
    }

    public List<StatusChangeLog> getStatusHistory(String microchipId) {
        validateMicrochipId(microchipId);
        return statusChangeLogDAO.findByAnimal(microchipId);
    }

    private void applyStatusUpdate(Animal animal, Animal.Status newStatus) {
        Animal.Status oldStatus = animal.getStatus();
        if (oldStatus == newStatus) {
            return;
        }

        animal.setStatus(newStatus);
        animalDAO.update(animal);
        statusChangeLogDAO.save(new StatusChangeLog(
                animal.getMicrochipId(),
                oldStatus,
                newStatus
        ));
        logger.info("event=status_change microchipId=" + animal.getMicrochipId()
                + " from=" + oldStatus.name() + " to=" + newStatus.name());
    }

    private void validateMicrochipId(String microchipId) {
        if (microchipId == null || microchipId.isBlank()) {
            throw new IllegalArgumentException("Microchip ID is required");
        }
    }


}
