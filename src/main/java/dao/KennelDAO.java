package dao;

import model.Kennel;

import java.util.List;
import java.util.Optional;

public interface KennelDAO {
    void save(Kennel kennel);

    Optional<Kennel> findById(String kennelId);

    List<Kennel> findAll();

    void update(Kennel kennel);

    boolean incrementOccupiedIfAvailable(String kennelId);
}
