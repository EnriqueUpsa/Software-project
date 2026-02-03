package service;

import dao.InMemoryKennelDAO;
import model.Kennel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KennelServiceTest {
    @Test
    void createKennel_success() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        Kennel kennel = new Kennel("K-1", 3);

        service.createKennel(kennel);

        assertTrue(dao.findById("K-1").isPresent());
    }

    @Test
    void createKennel_duplicateId_throwsException() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        service.createKennel(new Kennel("K-1", 3));

        assertThrows(IllegalArgumentException.class, () -> {
            service.createKennel(new Kennel("K-1", 5));
        });
    }

    @Test
    void assignAnimalToKennel_incrementsOccupied() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        service.createKennel(new Kennel("K-1", 2));

        service.assignAnimalToKennel("K-1");

        Kennel updated = dao.findById("K-1").orElseThrow();
        assertEquals(1, updated.getOccupied());
        assertEquals(1, updated.getAvailableCapacity());
    }

    @Test
    void assignAnimalToKennel_fullCapacity_throwsException() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        service.createKennel(new Kennel("K-1", 1, 1));

        assertThrows(IllegalStateException.class, () -> {
            service.assignAnimalToKennel("K-1");
        });
    }

    @Test
    void releaseAnimalFromKennel_decrementsOccupied() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        service.createKennel(new Kennel("K-1", 2, 1));

        service.releaseAnimalFromKennel("K-1");

        Kennel updated = dao.findById("K-1").orElseThrow();
        assertEquals(0, updated.getOccupied());
        assertEquals(2, updated.getAvailableCapacity());
    }

    @Test
    void releaseAnimalFromKennel_empty_throwsException() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        KennelService service = new KennelService(dao);

        service.createKennel(new Kennel("K-1", 2, 0));

        assertThrows(IllegalStateException.class, () -> {
            service.releaseAnimalFromKennel("K-1");
        });
    }
}
