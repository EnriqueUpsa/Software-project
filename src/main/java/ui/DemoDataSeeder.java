package ui;

import model.Adopter;
import model.Animal;
import model.Cat;
import model.Dog;
import model.HealthRecord;
import util.LoggerConfig;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fills an empty database with a small demo shelter.
 *
 * <p>The application is useless to look at on a database that has just been
 * created: the dashboard shows no animals, no adoptions and no medical
 * deadlines, and the history is empty. This seeder writes one realistic
 * scenario so that every screen has something to show from the first run.</p>
 *
 * <p>It is deliberately conservative: it only writes when the registry is
 * empty, so it never touches data an operator has entered, and it can be
 * switched off with {@code -Dshelter.demo=false}. It uses the service layer,
 * not the DAOs, so the demo data goes through the same validations, the same
 * transactions and the same audit trail as data typed by hand.</p>
 */
public final class DemoDataSeeder {

    private static final Logger logger = LoggerConfig.getLogger(DemoDataSeeder.class);

    private DemoDataSeeder() {
    }

    /**
     * Writes the demo shelter if the animal registry is empty.
     *
     * @param context application context with the services already wired.
     * @return true when the demo data was written, false when it was skipped.
     */
    public static boolean seedIfEmpty(AppContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Context is required");
        }
        if (!Boolean.parseBoolean(System.getProperty("shelter.demo", "true"))) {
            logger.info("event=demo_seed_disabled");
            return false;
        }
        if (!context.animalService().getAllAnimals().isEmpty()) {
            logger.info("event=demo_seed_skipped reason=registry_not_empty");
            return false;
        }

        try {
            seed(context);
            logger.info("event=demo_seed_success animals=5 adopters=2 adoptions=1");
            return true;
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "event=demo_seed_failed reason=" + e.getMessage());
            return false;
        }
    }

    private static void seed(AppContext context) {
        LocalDate today = LocalDate.now();

        // Five animals covering the four lifecycle statuses of the specification.
        context.animalService().registerAnimal(new Dog("MC-1001", "Beagle",
                today.minusDays(40), 3, Animal.HealthStatus.HEALTHY,
                "photos/beagle.jpg", Animal.Status.READY_FOR_ADOPTION));
        context.animalService().registerAnimal(new Dog("MC-1002", "Labrador",
                today.minusDays(12), 5, Animal.HealthStatus.CHRONIC_CONDITION,
                "photos/labrador.jpg", Animal.Status.UNDER_TREATMENT));
        context.animalService().registerAnimal(new Cat("MC-1003", "Siamese",
                today.minusDays(25), 2, Animal.HealthStatus.HEALTHY,
                "photos/siamese.jpg", Animal.Status.READY_FOR_ADOPTION));
        context.animalService().registerAnimal(new Cat("MC-1004", "European",
                today.minusDays(3), 1, Animal.HealthStatus.UNDER_OBSERVATION,
                "photos/european.jpg", Animal.Status.IN_OBSERVATION));
        context.animalService().registerAnimal(new Dog("MC-1005", "Border Collie",
                today.minusDays(60), 4, Animal.HealthStatus.HEALTHY,
                "photos/collie.jpg", Animal.Status.READY_FOR_ADOPTION));

        // Two adopters with different preferences, so the guided matching has
        // something to filter.
        context.adopterService().registerAdopter(new Adopter("ADO-1001",
                "Giulia Rossi", "3291110001", "Dog", ""));
        context.adopterService().registerAdopter(new Adopter("ADO-1002",
                "Marco Bianchi", "3291110002", "Cat", "Siamese"));

        // Health history, including one vaccine due tomorrow so the 48 hour
        // alert has a reason to fire on the dashboard.
        context.healthService().registerHealthRecord(new HealthRecord("MC-1002",
                HealthRecord.TreatmentType.VACCINE, "Rabies booster",
                today.plusDays(1), "1"));
        context.healthService().registerHealthRecord(new HealthRecord("MC-1002",
                HealthRecord.TreatmentType.PARASITE_TREATMENT, "Deworming",
                today.minusDays(10), "2"));
        context.healthService().registerHealthRecord(new HealthRecord("MC-1001",
                HealthRecord.TreatmentType.VETERINARY_VISIT, "Intake check-up",
                today.minusDays(38), "1"));
        context.healthService().registerHealthRecord(new HealthRecord("MC-1003",
                HealthRecord.TreatmentType.DIET, "Low fat diet",
                today.minusDays(20), "1"));

        // Occupancy of the shelter spaces created by the context.
        for (int i = 0; i < 4; i++) {
            context.kennelService().assignAnimalToKennel(AppContext.DEFAULT_KENNEL_ID);
        }
        context.kennelService().assignAnimalToKennel(AppContext.DEFAULT_CAGE_ID);

        // One completed placement, so the monthly adoption report and the audit
        // trail are not empty either.
        context.adoptionService().processAdoption("MC-1005", "ADO-1001", today.minusDays(2));
    }
}
