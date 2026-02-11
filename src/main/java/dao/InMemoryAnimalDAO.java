package dao;

import model.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

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
        for (int i = 0; i < animals.size(); i++) {
            Animal current = animals.get(i);
            if (current.getMicrochipId().equals(animal.getMicrochipId())) {
                animals.set(i, animal);
                return;
            }
        }
    }

    @Override
    public Map<String, Integer> getStatusDistribution() {
        Map<String, Integer> counts = new HashMap<>();
        for (Animal animal : animals) {
            String key = animal.getStatus().toString();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }
}
