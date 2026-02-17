package service;

import dao.KennelDAO;
import model.Kennel;
import util.LoggerConfig;

import java.util.List;
import java.util.logging.Logger;

/**
 * Application service for shelter logistics and occupancy operations.
 */
public class KennelService {
    private final KennelDAO kennelDAO;
    private static final Logger logger =
            LoggerConfig.getLogger(KennelService.class);

    public KennelService(KennelDAO kennelDAO) {
        this.kennelDAO = kennelDAO;
    }

    public void createKennel(Kennel kennel) {
        if (kennel == null) {
            throw new IllegalArgumentException("Kennel is required");
        }
        logger.info("event=kennel_create_start kennelId=" + kennel.getKennelId());

        if (kennelDAO.findById(kennel.getKennelId()).isPresent()) {
            logger.warning("event=kennel_create_rejected reason=duplicate_id kennelId="
                    + kennel.getKennelId());
            throw new IllegalArgumentException("Kennel ID already exists");
        }

        kennelDAO.save(kennel);
        logger.info("event=kennel_create_success kennelId=" + kennel.getKennelId()
                + " (maxCapacity=" + kennel.getMaxCapacity()
                + ", occupied=" + kennel.getOccupied() + ")");
    }

    public void assignAnimalToKennel(String kennelId) {
        boolean assigned = kennelDAO.incrementOccupiedIfAvailable(kennelId);
        if (!assigned) {
            throw new IllegalStateException("Kennel is at full capacity");
        }
        logger.info("event=kennel_assign_success kennelId=" + kennelId);
    }

    public void releaseAnimalFromKennel(String kennelId) {
        Kennel kennel = kennelDAO.findById(kennelId)
                .orElseThrow(() -> new IllegalArgumentException("Kennel not found"));

        if (kennel.getOccupied() <= 0) {
            throw new IllegalStateException("Kennel is already empty");
        }

        kennel.setOccupied(kennel.getOccupied() - 1);
        kennelDAO.update(kennel);
        logger.info("event=kennel_release_success kennelId=" + kennelId);
    }

    public void transferAnimal(String sourceKennelId, String destinationKennelId) {
        if (sourceKennelId == null || sourceKennelId.isBlank()
                || destinationKennelId == null || destinationKennelId.isBlank()) {
            throw new IllegalArgumentException("Both source and destination kennel IDs are required");
        }

        if (sourceKennelId.equals(destinationKennelId)) {
            throw new IllegalArgumentException("Source and destination kennels must be different");
        }

        boolean transferred = kennelDAO.transferOneAnimal(sourceKennelId, destinationKennelId);
        if (!transferred) {
            throw new IllegalStateException("Destination kennel is at full capacity");
        }

        logger.info("event=kennel_transfer_success sourceKennelId=" + sourceKennelId
                + " destinationKennelId=" + destinationKennelId);
    }

    public List<Kennel> getAllKennels() {
        return kennelDAO.findAll();
    }

}
