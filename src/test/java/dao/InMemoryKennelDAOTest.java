package dao;

import model.Kennel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryKennelDAOTest {
    @Test
    void incrementOccupiedIfAvailable_incrementsWhenSpace() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        dao.save(new Kennel("K-1", 2, 1));

        boolean result = dao.incrementOccupiedIfAvailable("K-1");

        assertTrue(result);
        assertEquals(2, dao.findById("K-1").orElseThrow().getOccupied());
    }

    @Test
    void incrementOccupiedIfAvailable_returnsFalseWhenFull() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        dao.save(new Kennel("K-1", 1, 1));

        boolean result = dao.incrementOccupiedIfAvailable("K-1");

        assertFalse(result);
        assertEquals(1, dao.findById("K-1").orElseThrow().getOccupied());
    }

    @Test
    void transferOneAnimal_movesOccupancy() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        dao.save(new Kennel("K-1", 2, 1));
        dao.save(new Kennel("K-2", 2, 0));

        boolean result = dao.transferOneAnimal("K-1", "K-2");

        assertTrue(result);
        assertEquals(0, dao.findById("K-1").orElseThrow().getOccupied());
        assertEquals(1, dao.findById("K-2").orElseThrow().getOccupied());
    }

    @Test
    void transferOneAnimal_returnsFalseWhenDestinationFull() {
        InMemoryKennelDAO dao = new InMemoryKennelDAO();
        dao.save(new Kennel("K-1", 2, 1));
        dao.save(new Kennel("K-2", 1, 1));

        boolean result = dao.transferOneAnimal("K-1", "K-2");

        assertFalse(result);
        assertEquals(1, dao.findById("K-1").orElseThrow().getOccupied());
        assertEquals(1, dao.findById("K-2").orElseThrow().getOccupied());
    }
}
