package service;

import dao.AdopterDAO;
import dao.AdoptionDAO;
import dao.AnimalDAO;
import dao.InMemoryStatusChangeLogDAO;
import dao.StatusChangeLogDAO;
import model.Adopter;
import model.Adoption;
import model.Animal;
import util.LoggerConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Application service for adoption rules, compatibility flow and transaction control.
 */
public class AdoptionService {
    private static final Logger logger =
            LoggerConfig.getLogger(AdoptionService.class);

    private final AdoptionDAO adoptionDAO;
    private final AnimalService animalService;
    private final AdopterService adopterService;
    private final Connection connection;

    public AdoptionService(AdoptionDAO adoptionDAO) {
        this(adoptionDAO, null, null);
    }

    public AdoptionService(AdoptionDAO adoptionDAO,
                           AnimalDAO animalDAO,
                           Connection connection) {
        this(adoptionDAO, animalDAO, null, new InMemoryStatusChangeLogDAO(), connection);
    }

    public AdoptionService(AdoptionDAO adoptionDAO,
                           AnimalDAO animalDAO,
                           StatusChangeLogDAO statusChangeLogDAO,
                           Connection connection) {
        this(adoptionDAO, animalDAO, null, statusChangeLogDAO, connection);
    }

    public AdoptionService(AdoptionDAO adoptionDAO,
                           AnimalDAO animalDAO,
                           AdopterDAO adopterDAO,
                           StatusChangeLogDAO statusChangeLogDAO,
                           Connection connection) {
        this.adoptionDAO = adoptionDAO;
        this.animalService = animalDAO == null
                ? null
                : new AnimalService(animalDAO, statusChangeLogDAO);
        this.adopterService = adopterDAO == null
                ? null
                : new AdopterService(adopterDAO);
        this.connection = connection;
    }

    public void validateAdoptionEligibility(Animal animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is required");
        }

        if (animal.getStatus() == Animal.Status.ADOPTED) {
            throw new IllegalArgumentException("Animal already adopted");
        }

        if (animal.getStatus() != Animal.Status.READY_FOR_ADOPTION) {
            throw new IllegalArgumentException(
                    "Animal is not ready for adoption");
        }
    }

    public void processAdoption(Animal animal, Adoption adoption) {
        if (adoptionDAO == null || animalService == null) {
            throw new IllegalStateException(
                    "Adoption processing is not configured");
        }

        validateAdoptionEligibility(animal);
        validateAdoptionData(animal, adoption);
        logger.info("event=adoption_start animalId=" + adoption.getAnimalId()
                + " adopterId=" + adoption.getAdopterId());

        if (connection == null) {
            adoptionDAO.save(adoption);
            animalService.updateStatus(animal, Animal.Status.ADOPTED);
            logger.info("event=adoption_success animalId=" + adoption.getAnimalId()
                    + " adopterId=" + adoption.getAdopterId()
                    + " date=" + adoption.getPlacementDate());
            return;
        }

        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read auto-commit state", e);
        }

        try {
            connection.setAutoCommit(false);
            adoptionDAO.save(adoption);
            animalService.updateStatus(animal, Animal.Status.ADOPTED);
            connection.commit();
            logger.info("event=adoption_success animalId=" + adoption.getAnimalId()
                    + " adopterId=" + adoption.getAdopterId()
                    + " date=" + adoption.getPlacementDate());
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            logger.warning("event=adoption_failed animalId=" + adoption.getAnimalId()
                    + " reason=" + e.getMessage());
            throw new RuntimeException("Failed to process adoption", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }
    }

    public void processAdoption(String animalMicrochipId,
                                String adopterId,
                                LocalDate placementDate) {
        if (animalService == null || adopterService == null) {
            throw new IllegalStateException("Adoption by matching is not configured");
        }

        Animal animal = animalService.getAnimalByMicrochip(animalMicrochipId);
        Adopter adopter = adopterService.getAdopterById(adopterId);
        if (!adopterService.isCompatible(animal, adopter)) {
            throw new IllegalArgumentException("Animal is not compatible with adopter preferences");
        }
        processAdoption(animal, new Adoption(animalMicrochipId, adopterId, placementDate));
    }

    public Map<Month, Integer> getMonthlyAdoptions(int year) {
        return adoptionDAO.getMonthlyAdoptions(year);
    }

    private void validateAdoptionData(Animal animal, Adoption adoption) {
        if (adoption == null) {
            throw new IllegalArgumentException("Adoption is required");
        }

        if (adoption.getAnimalId() == null || adoption.getAnimalId().isBlank()) {
            throw new IllegalArgumentException("Adoption animal ID is required");
        }

        if (!animal.getMicrochipId().equals(adoption.getAnimalId())) {
            throw new IllegalArgumentException("Adoption animal ID does not match");
        }

        if (adoption.getAdopterId() == null || adoption.getAdopterId().isBlank()) {
            throw new IllegalArgumentException("Adopter ID is required");
        }

        LocalDate placementDate = adoption.getPlacementDate();
        if (placementDate == null) {
            throw new IllegalArgumentException("Placement date is required");
        }

        if (animal.getIntakeDate() != null && placementDate.isBefore(animal.getIntakeDate())) {
            throw new IllegalArgumentException("Placement date cannot be before intake date");
        }
    }
}
