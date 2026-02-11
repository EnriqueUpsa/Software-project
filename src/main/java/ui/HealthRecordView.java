package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import dao.InMemoryHealthRecordDAO;
import model.HealthRecord;
import service.HealthService;

public class HealthRecordView extends Application {
    @Override
    public void start(Stage stage) {
        TextField microchipField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField dosageField = new TextField();
        TextField descriptionField = new TextField();
        ComboBox<HealthRecord.TreatmentType> treatmentBox = new ComboBox<>();
        treatmentBox.setItems(FXCollections.observableArrayList(
                HealthRecord.TreatmentType.values()));

        HealthService healthService =
                new HealthService(new InMemoryHealthRecordDAO());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                HealthRecord record = new HealthRecord(
                        microchipField.getText(),
                        treatmentBox.getValue(),
                        descriptionField.getText(),
                        datePicker.getValue(),
                        dosageField.getText()
                );

                healthService.registerHealthRecord(record);
                showAlert(Alert.AlertType.INFORMATION,
                        "Health record saved successfully");
                showUpcomingVaccineAlert(healthService);

                microchipField.clear();
                datePicker.setValue(null);
                dosageField.clear();
                descriptionField.clear();
                treatmentBox.setValue(null);
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);

        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);

        grid.add(new Label("Dosage:"), 0, 2);
        grid.add(dosageField, 1, 2);

        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        grid.add(new Label("Treatment Type:"), 0, 4);
        grid.add(treatmentBox, 1, 4);

        grid.add(saveButton, 1, 5);

        stage.setScene(new Scene(grid, 420, 260));
        stage.setTitle("Health Records");
        stage.show();

        showUpcomingVaccineAlert(healthService);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showUpcomingVaccineAlert(HealthService service) {
        if (!service.getUpcomingVaccines().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Vaccine due in less than 48 hours");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
