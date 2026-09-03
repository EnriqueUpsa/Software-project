package ui;

import controller.AdoptionController;
import controller.DashboardController;
import controller.HealthController;
import controller.HistoryController;
import controller.IntakeController;
import controller.LogisticsController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ui.view.AdoptionTabView;
import ui.view.DashboardTabView;
import ui.view.HealthTabView;
import ui.view.HistoryTabView;
import ui.view.IntakeTabView;
import ui.view.LogisticsTabView;

/**
 * Main JavaFX bootstrap class.
 * Wires controllers and views while keeping business logic in service/controller layers.
 */
public class ShelterManagementApp extends Application {
    private AppContext appContext;
    private DashboardTabView dashboardTabView;
    private IntakeTabView intakeTabView;

    @Override
    public void start(Stage stage) {
        appContext = AppContext.createDefault();
        DemoDataSeeder.seedIfEmpty(appContext);

        IntakeController intakeController = new IntakeController(
                appContext.animalService(),
                appContext.kennelService(),
                appContext.kennelDAO()
        );
        HealthController healthController = new HealthController(appContext.healthService());
        AdoptionController adoptionController = new AdoptionController(
                appContext.adopterService(),
                appContext.animalService(),
                appContext.adoptionService(),
                appContext.kennelService()
        );
        LogisticsController logisticsController = new LogisticsController(appContext.kennelService());
        DashboardController dashboardController = new DashboardController(
                appContext.animalService(),
                appContext.adoptionService(),
                appContext.healthService()
        );
        HistoryController historyController = new HistoryController(appContext.animalService());

        dashboardTabView = new DashboardTabView(dashboardController);

        // Every module reports its changes through the same callback, so an adoption
        // closed in one tab is immediately visible in the dashboard and in the registry.
        Runnable onDataChanged = this::refreshViews;

        intakeTabView = new IntakeTabView(intakeController, onDataChanged);
        HealthTabView healthTabView = new HealthTabView(healthController, onDataChanged);
        AdoptionTabView adoptionTabView = new AdoptionTabView(adoptionController, onDataChanged);
        LogisticsTabView logisticsTabView = new LogisticsTabView(logisticsController, onDataChanged);
        HistoryTabView historyTabView = new HistoryTabView(historyController);

        TabPane tabPane = new TabPane(
                intakeTabView.build(),
                healthTabView.build(),
                adoptionTabView.build(),
                logisticsTabView.build(),
                dashboardTabView.build(),
                historyTabView.build()
        );

        refreshViews();

        stage.setScene(new Scene(tabPane, 900, 620));
        stage.setTitle("Pet Shelter Management");
        stage.show();
    }

    /** Repaints the views that show data: the dashboard and the animal registry. */
    private void refreshViews() {
        dashboardTabView.refresh();
        intakeTabView.refresh();
    }

    @Override
    public void stop() {
        if (appContext != null) {
            appContext.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
