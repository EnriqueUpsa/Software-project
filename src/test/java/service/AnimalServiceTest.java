package service;

import dao.InMemoryAnimalDAO;
import dao.InMemoryStatusChangeLogDAO;
import model.Animal;
import model.Dog;
import model.StatusChangeLog;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalServiceTest {
    @Test
    void registerAnimal_success() {
        InMemoryAnimalDAO dao = new InMemoryAnimalDAO();
        AnimalService service = new AnimalService(dao);

        Dog dog = new Dog("123", "Labrador", LocalDate.now());

        service.registerAnimal(dog);

        assertTrue(dao.findByMicrochipId("123").isPresent());
    }

    @Test
    void registerAnimal_logsInitialStatus() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);

        Dog dog = new Dog("MC-0", "Labrador", LocalDate.now());

        service.registerAnimal(dog);

        List<StatusChangeLog> logs = service.getStatusHistory("MC-0");
        assertEquals(1, logs.size());
        assertEquals("MC-0", logs.getFirst().getMicrochipId());
        assertNull(logs.getFirst().getOldStatus());
        assertEquals(Animal.Status.IN_OBSERVATION, logs.getFirst().getNewStatus());
    }

    @Test
    void registerAnimal_duplicateMicrochip_throwsException() {
        InMemoryAnimalDAO dao = new InMemoryAnimalDAO();
        AnimalService service = new AnimalService(dao);

        Dog dog1 = new Dog("123", "Labrador", LocalDate.now());
        Dog dog2 = new Dog("123", "Beagle", LocalDate.now());

        service.registerAnimal(dog1);

        assertThrows(IllegalArgumentException.class, () -> {
            service.registerAnimal(dog2);
        });
    }

    @Test
    void updateStatus_logsChange() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);

        Dog dog = new Dog("MC-1", "Labrador", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);

        service.updateStatus(dog, Animal.Status.ADOPTED);

        List<model.StatusChangeLog> logs = service.getStatusHistory("MC-1");
        assertEquals(1, logs.size());
        assertEquals(Animal.Status.READY_FOR_ADOPTION, logs.getFirst().getOldStatus());
        assertEquals(Animal.Status.ADOPTED, logs.getFirst().getNewStatus());
    }

    @Test
    void updateStatus_sameStatus_doesNotCreateNewLog() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);

        Dog dog = new Dog("MC-1A", "Labrador", LocalDate.now());
        service.registerAnimal(dog);

        service.updateStatus("MC-1A", Animal.Status.IN_OBSERVATION);

        List<StatusChangeLog> logs = service.getStatusHistory("MC-1A");
        assertEquals(1, logs.size());
    }

    @Test
    void updateStatus_animalNotPersisted_throwsException() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);
        Dog dog = new Dog("MC-NOT-SAVED", "Labrador", LocalDate.now());

        assertThrows(IllegalArgumentException.class, () ->
                service.updateStatus(dog, Animal.Status.ADOPTED));
    }

    @Test
    void getStatusHistory_returnsOnlyRequestedAnimalLogs() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);

        Dog dogOne = new Dog("MC-2", "Beagle", LocalDate.now(),
                Animal.Status.IN_OBSERVATION);
        Dog dogTwo = new Dog("MC-3", "Pug", LocalDate.now(),
                Animal.Status.IN_OBSERVATION);
        animalDAO.save(dogOne);
        animalDAO.save(dogTwo);

        service.updateStatus("MC-2", Animal.Status.UNDER_TREATMENT);
        service.updateStatus("MC-2", Animal.Status.READY_FOR_ADOPTION);
        service.updateStatus("MC-3", Animal.Status.UNDER_TREATMENT);

        List<model.StatusChangeLog> mc2Logs = service.getStatusHistory("MC-2");
        assertEquals(2, mc2Logs.size());
        assertEquals("MC-2", mc2Logs.getFirst().getMicrochipId());
    }

    @Test
    void getStatusHistory_blankMicrochip_throwsException() {
        InMemoryAnimalDAO animalDAO = new InMemoryAnimalDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AnimalService service = new AnimalService(animalDAO, logDAO);

        assertThrows(IllegalArgumentException.class, () -> service.getStatusHistory(" "));
    }

}
