package dao;

import model.StatusChangeLog;

import java.util.List;

/**
 * Data access contract for animal status change audit entries.
 */
public interface StatusChangeLogDAO {
    void save(StatusChangeLog log);

    List<StatusChangeLog> findByAnimal(String microchipId);
}
