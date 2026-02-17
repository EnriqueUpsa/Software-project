package dao;

import model.Adopter;

import java.util.List;
import java.util.Optional;

/**
 * Data access contract for potential adopters.
 */
public interface AdopterDAO {
    void save(Adopter adopter);

    Optional<Adopter> findById(String adopterId);

    List<Adopter> findAll();
}
