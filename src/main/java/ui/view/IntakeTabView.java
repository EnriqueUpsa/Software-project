package ui.view;

import controller.IntakeController;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Animal;
import ui.AppContext;

import java.time.LocalDate;

/**
 * Intake module view.
 */
public class IntakeTabView {
    private final IntakeController intakeController;
    private final Runnable onDataChanged;

    public IntakeTabView(IntakeController intakeController, Runnable onDataChanged) {
        this.intakeController = intakeController;
        this.onDataChanged = onDataChanged;
    }

    public Tab build() {
        TextField microchipField = new TextField();
        TextField breedField = new TextField();
        TextField ageField = new TextField();
        TextField photoPathField = new TextField("photos/unknown.jpg");
        DatePicker intakeDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Dog", "Cat"));
        typeBox.setValue("Dog");

        ComboBox<Animal.HealthStatus> healthStatusBox = new ComboBox<>(
                FXCollections.observableArrayList(Animal.HealthStatus.values()));
        healthStatusBox.setValue(Animal.HealthStatus.UNDER_OBSERVATION);

        TextField intakeSpaceIdField = new TextField(AppContext.DEFAULT_KENNEL_ID);
        Label occupancyLabel = new Label();
        Button registerButton = new Button("Register Animal");

        TextField statusMicrochipField = new TextField();
        ComboBox<Animal.Status> statusBox = new ComboBox<>(
                FXCollections.observableArrayList(Animal.Status.values()));
        statusBox.setValue(Animal.Status.READY_FOR_ADOPTION);
        Button updateStatusButton = new Button("Update Status");

        registerButton.setOnAction(event -> {
            try {
                intakeController.registerAnimal(new IntakeController.IntakeRequest(
                        microchipField.getText(),
                        breedField.getText(),
                        ageField.getText(),
                        photoPathField.getText(),
                        intakeDatePicker.getValue(),
                        typeBox.getValue(),
                        healthStatusBox.getValue(),
                        intakeSpaceIdField.getText()
                ));
                updateOccupancyLabel(occupancyLabel, intakeSpaceIdField.getText());
                onDataChanged.run();
                ViewAlerts.info("Animal registered");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        updateStatusButton.setOnAction(event -> {
            try {
                intakeController.updateStatus(statusMicrochipField.getText(), statusBox.getValue());
                onDataChanged.run();
                ViewAlerts.info("Status updated");
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        intakeSpaceIdField.setOnAction(event -> updateOccupancyLabel(occupancyLabel, intakeSpaceIdField.getText()));
        updateOccupancyLabel(occupancyLabel, intakeSpaceIdField.getText());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Breed:"), 0, 2);
        grid.add(breedField, 1, 2);
        grid.add(new Label("Estimated Age:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Health Status:"), 0, 4);
        grid.add(healthStatusBox, 1, 4);
        grid.add(new Label("Photo Path:"), 0, 5);
        grid.add(photoPathField, 1, 5);
        grid.add(new Label("Intake Date:"), 0, 6);
        grid.add(intakeDatePicker, 1, 6);
        grid.add(new Label("Intake Space ID:"), 0, 7);
        grid.add(intakeSpaceIdField, 1, 7);
        grid.add(new Label("Space Occupancy:"), 0, 8);
        grid.add(occupancyLabel, 1, 8);
        grid.add(registerButton, 1, 9);

        grid.add(new Label("Status Microchip ID:"), 0, 11);
        grid.add(statusMicrochipField, 1, 11);
        grid.add(new Label("New Status:"), 0, 12);
        grid.add(statusBox, 1, 12);
        grid.add(updateStatusButton, 1, 13);

        return new Tab("Intake", grid);
    }

    private void updateOccupancyLabel(Label occupancyLabel, String spaceId) {
        try {
            occupancyLabel.setText(intakeController.getOccupancyText(spaceId));
        } catch (IllegalArgumentException ex) {
            occupancyLabel.setText("-");
        }
    }
}
