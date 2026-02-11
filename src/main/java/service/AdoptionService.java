package service;

import dao.AdoptionDAO;
import dao.AnimalDAO;
import model.Adoption;
import model.Animal;

import java.sql.Connection;
import java.sql.SQLException;

public class AdoptionService {
    private final AdoptionDAO adoptionDAO;
    private final AnimalDAO animalDAO;
    private final Connection connection;

    public AdoptionService(AdoptionDAO adoptionDAO,
                           AnimalDAO animalDAO,
                           Connection connection) {
        this.adoptionDAO = adoptionDAO;
        this.animalDAO = animalDAO;
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
        if (adoption == null) {
            throw new IllegalArgumentException("Adoption is required");
        }

        validateAdoptionEligibility(animal);

        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read auto-commit state", e);
        }

        try {
            connection.setAutoCommit(false);
            adoptionDAO.save(adoption);
            animal.setStatus(Animal.Status.ADOPTED);
            animalDAO.update(animal);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw new RuntimeException("Failed to process adoption", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }
    }
}
