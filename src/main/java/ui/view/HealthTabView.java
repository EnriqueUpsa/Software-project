package ui.view;

import controller.HealthController;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.HealthRecord;

import java.time.LocalDate;

/**
 * Health and nutrition module view.
 */
public class HealthTabView {
    private final HealthController healthController;
    private final Runnable onDataChanged;

    public HealthTabView(HealthController healthController, Runnable onDataChanged) {
        this.healthController = healthController;
        this.onDataChanged = onDataChanged;
    }

    public Tab build() {
        TextField microchipField = new TextField();
        ComboBox<HealthRecord.TreatmentType> treatmentTypeBox = new ComboBox<>(
                FXCollections.observableArrayList(HealthRecord.TreatmentType.values()));
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descriptionField = new TextField();
        TextField dosageField = new TextField();
        Label urgentLabel = new Label();
        Button saveButton = new Button("Save Record");

        saveButton.setOnAction(event -> {
            try {
                healthController.saveRecord(
                        microchipField.getText(),
                        treatmentTypeBox.getValue(),
                        datePicker.getValue(),
                        descriptionField.getText(),
                        dosageField.getText()
                );
                updateUrgentLabel(urgentLabel);
                onDataChanged.run();
                ViewAlerts.info("Health record saved");
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        updateUrgentLabel(urgentLabel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);
        grid.add(new Label("Treatment Type:"), 0, 1);
        grid.add(treatmentTypeBox, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);
        grid.add(new Label("Dosage:"), 0, 4);
        grid.add(dosageField, 1, 4);
        grid.add(new Label("Urgent medical deadlines (48h):"), 0, 5);
        grid.add(urgentLabel, 1, 5);
        grid.add(saveButton, 1, 6);

        return new Tab("Health", grid);
    }

    private void updateUrgentLabel(Label urgentLabel) {
        urgentLabel.setText(String.valueOf(healthController.getUrgentMedicalDeadlineCount()));
    }
}
