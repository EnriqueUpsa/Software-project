package ui.view;

import controller.AdoptionController;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ui.AppContext;

import java.time.LocalDate;

/**
 * Adoption module view.
 */
public class AdoptionTabView {
    private final AdoptionController adoptionController;
    private final Runnable onDataChanged;

    public AdoptionTabView(AdoptionController adoptionController, Runnable onDataChanged) {
        this.adoptionController = adoptionController;
        this.onDataChanged = onDataChanged;
    }

    public Tab build() {
        TextField adopterIdField = new TextField();
        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> preferredSpeciesBox = new ComboBox<>(
                FXCollections.observableArrayList("", "Dog", "Cat"));
        preferredSpeciesBox.setValue("");
        TextField preferredBreedField = new TextField();

        TextField animalMicrochipField = new TextField();
        TextField sourceSpaceIdField = new TextField(AppContext.DEFAULT_KENNEL_ID);
        DatePicker placementDatePicker = new DatePicker(LocalDate.now());

        Label compatibilityLabel = new Label();

        Button registerAdopterButton = new Button("Register Adopter");
        registerAdopterButton.setOnAction(event -> {
            try {
                adoptionController.registerAdopter(
                        adopterIdField.getText(),
                        fullNameField.getText(),
                        phoneField.getText(),
                        preferredSpeciesBox.getValue(),
                        preferredBreedField.getText()
                );
                ViewAlerts.info("Adopter registered");
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        Button checkCompatibilityButton = new Button("Check Compatibility");
        checkCompatibilityButton.setOnAction(event -> {
            try {
                boolean compatible = adoptionController.checkCompatibility(
                        animalMicrochipField.getText(),
                        adopterIdField.getText()
                );
                compatibilityLabel.setText(compatible ? "Compatible" : "Not compatible");
            } catch (IllegalArgumentException ex) {
                compatibilityLabel.setText("Unknown");
                ViewAlerts.error(ex.getMessage());
            }
        });

        Button processAdoptionButton = new Button("Process Adoption");
        processAdoptionButton.setOnAction(event -> {
            try {
                adoptionController.processAdoption(
                        animalMicrochipField.getText(),
                        adopterIdField.getText(),
                        placementDatePicker.getValue(),
                        sourceSpaceIdField.getText()
                );
                onDataChanged.run();
                ViewAlerts.info("Adoption completed");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Adopter ID:"), 0, 0);
        grid.add(adopterIdField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullNameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Preferred Species:"), 0, 3);
        grid.add(preferredSpeciesBox, 1, 3);
        grid.add(new Label("Preferred Breed:"), 0, 4);
        grid.add(preferredBreedField, 1, 4);
        grid.add(registerAdopterButton, 1, 5);

        grid.add(new Label("Animal Microchip:"), 0, 7);
        grid.add(animalMicrochipField, 1, 7);
        grid.add(new Label("Current Space ID:"), 0, 8);
        grid.add(sourceSpaceIdField, 1, 8);
        grid.add(new Label("Placement Date:"), 0, 9);
        grid.add(placementDatePicker, 1, 9);
        grid.add(checkCompatibilityButton, 1, 10);
        grid.add(new Label("Compatibility:"), 0, 11);
        grid.add(compatibilityLabel, 1, 11);
        grid.add(processAdoptionButton, 1, 12);

        return new Tab("Adoption", grid);
    }
}
