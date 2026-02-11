package service;

import dao.InMemoryHealthRecordDAO;
import model.HealthRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HealthServiceTest {

    @Test
    void registerHealthRecord_validRecord_saves() {
        InMemoryHealthRecordDAO dao = new InMemoryHealthRecordDAO();
        HealthService service = new HealthService(dao);

        HealthRecord record = new HealthRecord(
                "MC-100",
                HealthRecord.TreatmentType.VACCINE,
                "Rabies vaccine",
                LocalDate.now(),
                "1.0"
        );

        service.registerHealthRecord(record);

        assertEquals(1, dao.findByMicrochipId("MC-100").size());
    }

    @Test
    void registerHealthRecord_nullDate_throwsException() {
        InMemoryHealthRecordDAO dao = new InMemoryHealthRecordDAO();
        HealthService service = new HealthService(dao);

        HealthRecord record = new HealthRecord(
                "MC-101",
                HealthRecord.TreatmentType.DIET,
                "Recovery diet",
                null,
                "0.5"
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.registerHealthRecord(record));
    }

    @Test
    void registerHealthRecord_invalidDosage_throwsException() {
        InMemoryHealthRecordDAO dao = new InMemoryHealthRecordDAO();
        HealthService service = new HealthService(dao);

        HealthRecord record = new HealthRecord(
                "MC-102",
                HealthRecord.TreatmentType.PARASITE_TREATMENT,
                "Deworming",
                LocalDate.now(),
                "abc"
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.registerHealthRecord(record));
    }

    @Test
    void registerHealthRecord_emptyMicrochip_throwsException() {
        InMemoryHealthRecordDAO dao = new InMemoryHealthRecordDAO();
        HealthService service = new HealthService(dao);

        HealthRecord record = new HealthRecord(
                "  ",
                HealthRecord.TreatmentType.VACCINE,
                "Booster",
                LocalDate.now(),
                "1.0"
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.registerHealthRecord(record));
    }
}
