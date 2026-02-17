package dao;

import model.HealthRecord;

import java.util.List;

/**
 * Data access contract for medical and nutrition records.
 */
public interface HealthRecordDAO {
    void save(HealthRecord record);

    List<HealthRecord> findByMicrochipId(String microchipId);

    List<HealthRecord> findAll();
}
