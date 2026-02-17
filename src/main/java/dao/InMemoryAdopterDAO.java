package dao;

import model.Adopter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of {@link AdopterDAO} for tests and local demos.
 */
public class InMemoryAdopterDAO implements AdopterDAO {
    private final List<Adopter> adopters = new ArrayList<>();

    @Override
    public void save(Adopter adopter) {
        adopters.add(adopter);
    }

    @Override
    public Optional<Adopter> findById(String adopterId) {
        return adopters.stream()
                .filter(a -> a.getAdopterId().equals(adopterId))
                .findFirst();
    }

    @Override
    public List<Adopter> findAll() {
        return adopters;
    }
}
