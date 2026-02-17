package ui.view;

import controller.DashboardController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.time.Month;
import java.time.Year;
import java.util.Map;

/**
 * Dashboard summary view.
 */
public class DashboardTabView {
    private final DashboardController dashboardController;

    private final Label statusLabel = new Label();
    private final Label adoptionsLabel = new Label();
    private final Label urgentLabel = new Label();

    public DashboardTabView(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public Tab build() {
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refresh());

        VBox content = new VBox(12,
                statusLabel,
                adoptionsLabel,
                urgentLabel,
                refreshButton);

        return new Tab("Dashboard", content);
    }

    public void refresh() {
        Map<String, Integer> statusDistribution = dashboardController.getAnimalStatusDistribution();
        Map<Month, Integer> monthlyAdoptions = dashboardController.getMonthlyAdoptions(Year.now().getValue());
        int urgentCount = dashboardController.getUrgentMedicalDeadlineCount();

        statusLabel.setText("Animals by status: " + statusDistribution);
        adoptionsLabel.setText("Monthly adoptions: " + monthlyAdoptions);
        urgentLabel.setText("Urgent veterinary needs (48h): " + urgentCount);
    }
}
