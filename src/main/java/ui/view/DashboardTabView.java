package ui.view;

import controller.DashboardController;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard summary view.
 *
 * <p>The director follows the shelter through three indicators: how the animals are
 * distributed over the lifecycle statuses, how many adoptions were closed in each month
 * of the current year, and how many veterinary deadlines fall inside the next 48 hours.
 * The first two are shown as bar charts, the third as a highlighted alert.</p>
 *
 * <p>The view owns no business logic: it reads the three values from
 * {@link DashboardController} and paints them.</p>
 */
public class DashboardTabView {

    /** The alert turns red only while there is a deadline to attend. */
    private static final String ALERT_STYLE = "-fx-text-fill: #b00020; -fx-font-weight: bold;";
    private static final String CALM_STYLE = "-fx-text-fill: #2e7d32;";

    private final DashboardController dashboardController;

    private final BarChart<String, Number> statusChart =
            createBarChart("Animals by status", "Status", "Animals");
    private final BarChart<String, Number> adoptionsChart =
            createBarChart("Adoptions per month", "Month", "Adoptions");

    private final Label shelterSizeLabel = new Label();
    private final Label urgentLabel = new Label();

    public DashboardTabView(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public Tab build() {
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refresh());

        HBox charts = new HBox(12, statusChart, adoptionsChart);
        HBox.setHgrow(statusChart, Priority.ALWAYS);
        HBox.setHgrow(adoptionsChart, Priority.ALWAYS);

        VBox content = new VBox(10, shelterSizeLabel, urgentLabel, charts, refreshButton);
        content.setPadding(new Insets(12));
        VBox.setVgrow(charts, Priority.ALWAYS);

        return new Tab("Dashboard", content);
    }

    /**
     * Reads the metrics from the controller and repaints both charts.
     *
     * <p>Every module calls this method after changing data, so the dashboard always
     * reflects the current state of the shelter.</p>
     */
    public void refresh() {
        Map<String, Integer> statusDistribution = dashboardController.getAnimalStatusDistribution();
        Map<Month, Integer> monthlyAdoptions =
                dashboardController.getMonthlyAdoptions(Year.now().getValue());
        int urgentCount = dashboardController.getUrgentMedicalDeadlineCount();

        shelterSizeLabel.setText("Animals in the shelter: " + total(statusDistribution));
        urgentLabel.setText("Urgent veterinary needs (next 48 h): " + urgentCount);
        urgentLabel.setStyle(urgentCount > 0 ? ALERT_STYLE : CALM_STYLE);

        paintStatusChart(statusDistribution);
        paintAdoptionsChart(monthlyAdoptions);
    }

    private void paintStatusChart(Map<String, Integer> statusDistribution) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int highest = 0;

        for (Map.Entry<String, Integer> entry : statusDistribution.entrySet()) {
            series.getData().add(new XYChart.Data<>(readable(entry.getKey()), entry.getValue()));
            highest = Math.max(highest, entry.getValue());
        }

        statusChart.getData().setAll(series);
        scaleToWholeAnimals(statusChart, highest);
    }

    private void paintAdoptionsChart(Map<Month, Integer> monthlyAdoptions) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int highest = 0;

        // Every month of the year is drawn, also the empty ones, so that the
        // seasonality of the adoptions can be read from the chart.
        for (Month month : Month.values()) {
            int adoptions = monthlyAdoptions.getOrDefault(month, 0);
            series.getData().add(new XYChart.Data<>(shortName(month), adoptions));
            highest = Math.max(highest, adoptions);
        }

        adoptionsChart.getData().setAll(series);
        scaleToWholeAnimals(adoptionsChart, highest);
    }

    private static BarChart<String, Number> createBarChart(String title,
                                                           String categoryLabel,
                                                           String valueLabel) {
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setLabel(categoryLabel);

        NumberAxis valueAxis = new NumberAxis();
        valueAxis.setLabel(valueLabel);

        BarChart<String, Number> chart = new BarChart<>(categoryAxis, valueAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        // The chart is repainted on every refresh; the animation would only flicker.
        chart.setAnimated(false);
        return chart;
    }

    /**
     * Both charts count whole animals, so the value axis is forced to integer steps
     * instead of the decimal ticks that automatic ranging produces for small counts.
     */
    private static void scaleToWholeAnimals(BarChart<String, Number> chart, int highestValue) {
        NumberAxis valueAxis = (NumberAxis) chart.getYAxis();
        valueAxis.setAutoRanging(false);
        valueAxis.setLowerBound(0);
        valueAxis.setUpperBound(Math.max(1, highestValue) + 1);
        valueAxis.setTickUnit(1);
        valueAxis.setMinorTickVisible(false);
    }

    private static int total(Map<String, Integer> statusDistribution) {
        int total = 0;
        for (int count : statusDistribution.values()) {
            total += count;
        }
        return total;
    }

    /** Turns the enum constant {@code READY_FOR_ADOPTION} into the axis label "Ready for adoption". */
    private static String readable(String status) {
        if (status == null || status.isBlank()) {
            return "Unknown";
        }

        String text = status.replace('_', ' ').toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private static String shortName(Month month) {
        return month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
