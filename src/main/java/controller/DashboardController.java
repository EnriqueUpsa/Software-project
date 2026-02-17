package controller;

import service.AdoptionService;
import service.AnimalService;
import service.HealthService;

import java.time.Month;
import java.util.Map;

/**
 * Controller for dashboard summary metrics.
 */
public class DashboardController {
    private final AnimalService animalService;
    private final AdoptionService adoptionService;
    private final HealthService healthService;

    public DashboardController(AnimalService animalService,
                               AdoptionService adoptionService,
                               HealthService healthService) {
        this.animalService = animalService;
        this.adoptionService = adoptionService;
        this.healthService = healthService;
    }

    public Map<String, Integer> getAnimalStatusDistribution() {
        return animalService.getAnimalStatusDistribution();
    }

    public Map<Month, Integer> getMonthlyAdoptions(int year) {
        return adoptionService.getMonthlyAdoptions(year);
    }

    public int getUrgentMedicalDeadlineCount() {
        return healthService.getUpcomingMedicalDeadlines().size();
    }
}
