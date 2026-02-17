package controller;

import model.StatusChangeLog;
import service.AnimalService;

import java.util.List;

/**
 * Controller for status change audit history.
 */
public class HistoryController {
    private final AnimalService animalService;

    public HistoryController(AnimalService animalService) {
        this.animalService = animalService;
    }

    public List<StatusChangeLog> getAnimalStatusHistory(String microchipId) {
        return animalService.getStatusHistory(microchipId);
    }
}
