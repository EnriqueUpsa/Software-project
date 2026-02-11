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

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("Adoption Dashboard");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
