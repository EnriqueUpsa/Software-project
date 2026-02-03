package service;

import dao.AnimalDAO;
import model.Animal;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;


public class AnimalService {
    private final AnimalDAO animalDAO;

    public AnimalService(AnimalDAO animalDAO) {
        this.animalDAO = animalDAO;
    }

    public void registerAnimal(Animal animal) {

        logger.info("Attempting to register animal with microchip: "
                + animal.getMicrochipId());

        if (animalDAO.findByMicrochipId(animal.getMicrochipId()).isPresent()) {
            logger.warning("Duplicate microchip detected: "
                    + animal.getMicrochipId());
            throw new IllegalArgumentException("Microchip ID already exists");
        }

        animalDAO.save(animal);

        logger.info("Animal registered successfully: "
                + animal.getMicrochipId());
    }

    private static final Logger logger =
            Logger.getLogger(AnimalService.class.getName());

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
