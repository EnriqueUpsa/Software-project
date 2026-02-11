package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.InMemoryAdoptionDAO;
import dao.InMemoryAnimalDAO;
import service.AdoptionService;
import service.AnimalService;

import java.time.Month;
import java.time.Year;
import java.util.Map;

public class DashboardView extends Application {
    @Override
    public void start(Stage stage) {
        CategoryAxis monthAxis = new CategoryAxis();
        NumberAxis countAxis = new NumberAxis();
        BarChart<String, Number> adoptionChart =
                new BarChart<>(monthAxis, countAxis);
        adoptionChart.setTitle("Monthly Adoptions");
        monthAxis.setLabel("Month");
        countAxis.setLabel("Adoptions");

        PieChart statusChart = new PieChart();
        statusChart.setTitle("Animal Status Distribution");

        HBox charts = new HBox(20, adoptionChart, statusChart);

        VBox root = new VBox(10, new Label("Dashboard"), charts);
        root.setPadding(new Insets(15));

        loadCharts(adoptionChart, statusChart);

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("Adoption Dashboard");
        stage.show();
    }

    private void loadCharts(BarChart<String, Number> adoptionChart,
                            PieChart statusChart) {
        AdoptionService adoptionService =
                new AdoptionService(new InMemoryAdoptionDAO());
        AnimalService animalService =
                new AnimalService(new InMemoryAnimalDAO());

        Map<Month, Integer> monthly = adoptionService.getMonthlyAdoptions(
                Year.now().getValue());
        if (monthly.isEmpty()) {
            adoptionChart.setTitle("Monthly Adoptions (no data)");
        } else {
            var series = new javafx.scene.chart.XYChart.Series<String, Number>();
            for (Month month : Month.values()) {
                Integer count = monthly.get(month);
                if (count != null) {
                    series.getData().add(
                            new javafx.scene.chart.XYChart.Data<>(
                                    month.name(), count));
                }
            }
            adoptionChart.getData().add(series);
        }

        Map<String, Integer> statusDistribution =
                animalService.getAnimalStatusDistribution();
        if (statusDistribution.isEmpty()) {
            statusChart.setTitle("Animal Status Distribution (no data)");
        } else {
            for (Map.Entry<String, Integer> entry
                    : statusDistribution.entrySet()) {
                statusChart.getData().add(
                        new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
