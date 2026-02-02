package service;

import dao.AnimalDAO;
import model.Animal;
import java.util.Optional;


public class AnimalService {
    private final AnimalDAO animalDAO;

    public AnimalService(AnimalDAO animalDAO) {
        this.animalDAO = animalDAO;
    }

    public void registerAnimal(Animal animal) {
        if (animalDAO.findByMicrochipId(animal.getMicrochipId()).isPresent()) {
            throw new IllegalArgumentException("Microchip ID already exists");
        }
        animalDAO.save(animal);
    }
}
