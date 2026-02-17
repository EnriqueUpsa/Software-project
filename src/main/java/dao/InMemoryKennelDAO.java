package dao;

import model.Kennel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of {@link KennelDAO}.
 */
public class InMemoryKennelDAO implements KennelDAO {
    private final List<Kennel> kennels = new ArrayList<>();

    @Override
    public void save(Kennel kennel) {
        kennels.add(kennel);
    }

    @Override
    public Optional<Kennel> findById(String kennelId) {
        return kennels.stream()
                .filter(k -> k.getKennelId().equals(kennelId))
                .findFirst();
    }

    @Override
    public List<Kennel> findAll() {
        return kennels;
    }

    @Override
    public void update(Kennel kennel) {
        for (int i = 0; i < kennels.size(); i++) {
            if (kennels.get(i).getKennelId().equals(kennel.getKennelId())) {
                kennels.set(i, kennel);
                return;
            }
        }
    }

    @Override
    public boolean incrementOccupiedIfAvailable(String kennelId) {
        synchronized (kennels) {
            Kennel kennel = findById(kennelId)
                    .orElseThrow(() -> new IllegalArgumentException("Kennel not found"));
            if (kennel.getAvailableCapacity() <= 0) {
                return false;
            }
            kennel.setOccupied(kennel.getOccupied() + 1);
            return true;
        }
    }

    @Override
    public boolean transferOneAnimal(String sourceKennelId, String destinationKennelId) {
        synchronized (kennels) {
            Kennel source = findById(sourceKennelId)
                    .orElseThrow(() -> new IllegalArgumentException("Source kennel not found"));
            Kennel destination = findById(destinationKennelId)
                    .orElseThrow(() -> new IllegalArgumentException("Destination kennel not found"));

            if (source.getOccupied() <= 0) {
                throw new IllegalStateException("Source kennel is empty");
            }

            if (destination.getAvailableCapacity() <= 0) {
                return false;
            }

            source.setOccupied(source.getOccupied() - 1);
            destination.setOccupied(destination.getOccupied() + 1);
            return true;
        }
    }
}
