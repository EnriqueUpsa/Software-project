package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnimalProfileTest {

    @Test
    void fullProfileConstructor_setsAllFields() {
        Dog dog = new Dog(
                "P-1",
                "Labrador",
                LocalDate.of(2026, 2, 1),
                4,
                Animal.HealthStatus.HEALTHY,
                "photos/p1.jpg",
                Animal.Status.IN_OBSERVATION
        );

        assertEquals("P-1", dog.getMicrochipId());
        assertEquals(4, dog.getEstimatedAgeYears());
        assertEquals(Animal.HealthStatus.HEALTHY, dog.getHealthStatus());
        assertEquals("photos/p1.jpg", dog.getPhotoPath());
        assertEquals("Dog", dog.getSpecies());
    }

    @Test
    void constructor_negativeAge_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Cat(
                "P-2",
                "Siamese",
                LocalDate.now(),
                -1,
                Animal.HealthStatus.HEALTHY,
                "photos/p2.jpg",
                Animal.Status.IN_OBSERVATION
        ));
    }
}
