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

        DashboardTabView dashboardTabView = new DashboardTabView(dashboardController);
        Runnable refreshDashboard = dashboardTabView::refresh;

        IntakeTabView intakeTabView = new IntakeTabView(intakeController, refreshDashboard);
        HealthTabView healthTabView = new HealthTabView(healthController, refreshDashboard);
        AdoptionTabView adoptionTabView = new AdoptionTabView(adoptionController, refreshDashboard);
        LogisticsTabView logisticsTabView = new LogisticsTabView(logisticsController, refreshDashboard);
        HistoryTabView historyTabView = new HistoryTabView(historyController);

        TabPane tabPane = new TabPane(
                intakeTabView.build(),
                healthTabView.build(),
                adoptionTabView.build(),
                logisticsTabView.build(),
                dashboardTabView.build(),
                historyTabView.build()
        );

        dashboardTabView.refresh();

        stage.setScene(new Scene(tabPane, 900, 620));
        stage.setTitle("Pet Shelter Management");
        stage.show();
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
