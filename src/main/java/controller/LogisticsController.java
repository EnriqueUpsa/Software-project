package controller;

import model.Kennel;
import service.KennelService;

import java.util.List;

/**
 * Controller for shelter logistics and occupancy movements.
 */
public class LogisticsController {
    private final KennelService kennelService;

    public LogisticsController(KennelService kennelService) {
        this.kennelService = kennelService;
    }

    public void createSpace(String spaceId, Kennel.SpaceType spaceType, String maxCapacity) {
        if (maxCapacity == null || maxCapacity.isBlank()) {
            throw new IllegalArgumentException("Capacity is required");
        }

        int capacity;
        try {
            capacity = Integer.parseInt(maxCapacity.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Capacity must be numeric");
        }

        kennelService.createKennel(new Kennel(spaceId, spaceType, capacity));
    }

    public void assignOccupancy(String spaceId) {
        kennelService.assignAnimalToKennel(spaceId);
    }

    public void releaseOccupancy(String spaceId) {
        kennelService.releaseAnimalFromKennel(spaceId);
    }

    public void transferOccupancy(String sourceSpaceId, String destinationSpaceId) {
        kennelService.transferAnimal(sourceSpaceId, destinationSpaceId);
    }

    public List<Kennel> getAllSpaces() {
        return kennelService.getAllKennels();
    }
}
