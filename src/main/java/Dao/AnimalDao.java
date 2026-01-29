package Dao;

import model.Animal;
import java.util.List;
import java.util.Optional;

public interface AnimalDao {
    void save(Animal animal);

    Optional<Animal> findByMicrochipId(String microchipId);

    List<Animal> findAll();

    void update(Animal animal);
}
