package service;

import dao.KennelDAO;
import model.Kennel;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KennelService {
    private final KennelDAO kennelDAO;

    public KennelService(KennelDAO kennelDAO) {
        this.kennelDAO = kennelDAO;
    }

    public void createKennel(Kennel kennel) {
        logger.info("Attempting to create kennel: " + kennel.getKennelId());

        if (kennelDAO.findById(kennel.getKennelId()).isPresent()) {
            logger.warning("Duplicate kennel ID detected: " + kennel.getKennelId());
            throw new IllegalArgumentException("Kennel ID already exists");
        }

        kennelDAO.save(kennel);
        logger.info("Kennel created successfully: " + kennel.getKennelId());
    }

    public void assignAnimalToKennel(String kennelId) {
        Kennel kennel = kennelDAO.findById(kennelId)
                .orElseThrow(() -> new IllegalArgumentException("Kennel not found"));

        if (kennel.getAvailableCapacity() <= 0) {
            throw new IllegalStateException("Kennel is at full capacity");
        }

        kennel.setOccupied(kennel.getOccupied() + 1);
        kennelDAO.update(kennel);
    }

    public void releaseAnimalFromKennel(String kennelId) {
        Kennel kennel = kennelDAO.findById(kennelId)
                .orElseThrow(() -> new IllegalArgumentException("Kennel not found"));

        if (kennel.getOccupied() <= 0) {
            throw new IllegalStateException("Kennel is already empty");
        }

        kennel.setOccupied(kennel.getOccupied() - 1);
        kennelDAO.update(kennel);
    }

    private static final Logger logger =
            Logger.getLogger(KennelService.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
    }

    static {
        logger.setLevel(java.util.logging.Level.INFO);
    }
}
