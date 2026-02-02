package dao;

import model.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryAnimalDAO implements AnimalDAO {

    private final List<Animal> animals = new ArrayList<>();

    @Override
    public void save(Animal animal) {
        animals.add(animal);
    }

    @Override
    public Optional<Animal> findByMicrochipId(String microchipId) {
        return animals.stream()
                .filter(a -> a.getMicrochipId().equals(microchipId))
                .findFirst();
    }

    @Override
    public List<Animal> findAll() {
        return animals;
    }

    @Override
    public void update(Animal animal) {
        // not needed for Sprint 1
    }
}

