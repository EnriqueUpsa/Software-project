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
}
