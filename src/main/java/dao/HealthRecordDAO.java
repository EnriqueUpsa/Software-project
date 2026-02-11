package dao;

import model.HealthRecord;

import java.util.List;

public interface HealthRecordDAO {
    void save(HealthRecord record);

    List<HealthRecord> findByMicrochipId(String microchipId);

    List<HealthRecord> findAll();
}
