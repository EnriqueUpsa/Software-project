package ui;

import dao.AdopterDAO;
import dao.AdoptionDAO;
import dao.AnimalDAO;
import dao.HealthRecordDAO;
import dao.JdbcAdopterDAO;
import dao.JdbcAdoptionDAO;
import dao.JdbcAnimalDAO;
import dao.JdbcConnectionFactory;
import dao.JdbcHealthRecordDAO;
import dao.JdbcKennelDAO;
import dao.JdbcStatusChangeLogDAO;
import dao.KennelDAO;
import dao.StatusChangeLogDAO;
import model.Kennel;
import service.AdopterService;
import service.AdoptionService;
import service.AnimalService;
import service.HealthService;
import service.KennelService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Application-scoped container for persistence and service wiring.
 */
public final class AppContext implements AutoCloseable {
    public static final String DEFAULT_KENNEL_ID = "KENNEL-A";
    public static final String DEFAULT_CAGE_ID = "CAGE-A";
    public static final String DEFAULT_FENCED_AREA_ID = "FENCED-A";

    private final Connection connection;

    private final AnimalDAO animalDAO;
    private final StatusChangeLogDAO statusChangeLogDAO;
    private final HealthRecordDAO healthRecordDAO;
    private final AdoptionDAO adoptionDAO;
    private final AdopterDAO adopterDAO;
    private final KennelDAO kennelDAO;

    private final AnimalService animalService;
    private final HealthService healthService;
    private final AdopterService adopterService;
    private final AdoptionService adoptionService;
    private final KennelService kennelService;

    private AppContext(Connection connection,
                       AnimalDAO animalDAO,
                       StatusChangeLogDAO statusChangeLogDAO,
                       HealthRecordDAO healthRecordDAO,
                       AdoptionDAO adoptionDAO,
                       AdopterDAO adopterDAO,
                       KennelDAO kennelDAO,
                       AnimalService animalService,
                       HealthService healthService,
                       AdopterService adopterService,
                       AdoptionService adoptionService,
                       KennelService kennelService) {
        this.connection = connection;
        this.animalDAO = animalDAO;
        this.statusChangeLogDAO = statusChangeLogDAO;
        this.healthRecordDAO = healthRecordDAO;
        this.adoptionDAO = adoptionDAO;
        this.adopterDAO = adopterDAO;
        this.kennelDAO = kennelDAO;
        this.animalService = animalService;
        this.healthService = healthService;
        this.adopterService = adopterService;
        this.adoptionService = adoptionService;
        this.kennelService = kennelService;
    }

    /**
     * Builds the default JDBC-backed context and creates baseline shelter spaces.
     *
     * @return initialized context.
     */
    public static AppContext createDefault() {
        Connection connection = JdbcConnectionFactory.createH2FileConnection();

        AnimalDAO animalDAO = new JdbcAnimalDAO(connection);
        StatusChangeLogDAO statusChangeLogDAO = new JdbcStatusChangeLogDAO(connection);
        HealthRecordDAO healthRecordDAO = new JdbcHealthRecordDAO(connection);
        AdoptionDAO adoptionDAO = new JdbcAdoptionDAO(connection);
        AdopterDAO adopterDAO = new JdbcAdopterDAO(connection);
        KennelDAO kennelDAO = new JdbcKennelDAO(connection);

        AnimalService animalService = new AnimalService(animalDAO, statusChangeLogDAO);
        HealthService healthService = new HealthService(healthRecordDAO);
        AdopterService adopterService = new AdopterService(adopterDAO);
        AdoptionService adoptionService = new AdoptionService(
                adoptionDAO, animalDAO, adopterDAO, statusChangeLogDAO, connection
        );
        KennelService kennelService = new KennelService(kennelDAO);

        AppContext context = new AppContext(
                connection,
                animalDAO,
                statusChangeLogDAO,
                healthRecordDAO,
                adoptionDAO,
                adopterDAO,
                kennelDAO,
                animalService,
                healthService,
                adopterService,
                adoptionService,
                kennelService
        );
        context.ensureDefaultSpaces();
        return context;
    }

    private void ensureDefaultSpaces() {
        ensureSpace(DEFAULT_KENNEL_ID, Kennel.SpaceType.KENNEL, 20);
        ensureSpace(DEFAULT_CAGE_ID, Kennel.SpaceType.CAGE, 12);
        ensureSpace(DEFAULT_FENCED_AREA_ID, Kennel.SpaceType.FENCED_AREA, 15);
    }

    private void ensureSpace(String spaceId, Kennel.SpaceType spaceType, int capacity) {
        if (kennelDAO.findById(spaceId).isEmpty()) {
            kennelService.createKennel(new Kennel(spaceId, spaceType, capacity));
        }
    }

    public AnimalService animalService() {
        return animalService;
    }

    public HealthService healthService() {
        return healthService;
    }

    public AdopterService adopterService() {
        return adopterService;
    }

    public AdoptionService adoptionService() {
        return adoptionService;
    }

    public KennelService kennelService() {
        return kennelService;
    }

    public KennelDAO kennelDAO() {
        return kennelDAO;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
