package dao;

import model.HealthRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryHealthRecordDAO implements HealthRecordDAO {
    private final List<HealthRecord> records = new ArrayList<>();

    @Override
    public void save(HealthRecord record) {
        records.add(record);
    }

    @Override
    public List<HealthRecord> findByMicrochipId(String microchipId) {
        return records.stream()
                .filter(record -> record.getMicrochipId().equals(microchipId))
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthRecord> findAll() {
        return records;
    }
}
