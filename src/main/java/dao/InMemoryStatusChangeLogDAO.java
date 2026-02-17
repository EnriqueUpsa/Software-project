package dao;

import model.StatusChangeLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link StatusChangeLogDAO}.
 */
public class InMemoryStatusChangeLogDAO implements StatusChangeLogDAO {
    private final List<StatusChangeLog> logs = new ArrayList<>();

    @Override
    public void save(StatusChangeLog log) {
        logs.add(log);
    }

    @Override
    public List<StatusChangeLog> findByAnimal(String microchipId) {
        return logs.stream()
                .filter(log -> log.getMicrochipId().equals(microchipId))
                .sorted(Comparator.comparing(StatusChangeLog::getTimestamp))
                .collect(Collectors.toList());
    }
}
