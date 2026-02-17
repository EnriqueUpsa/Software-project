package dao;

import model.Animal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Data access contract for animal profiles and status metrics.
 */
public interface AnimalDAO {
    void save(Animal animal);

    Optional<Animal> findByMicrochipId(String microchipId);

    List<Animal> findAll();

    void update(Animal animal);

    Map<String, Integer> getStatusDistribution();
}
