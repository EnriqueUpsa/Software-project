package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import dao.InMemoryAnimalDAO;
import dao.InMemoryKennelDAO;
import service.AnimalService;
import service.KennelService;
import model.Animal;
import model.Dog;
import model.Cat;
import model.Kennel;

import java.time.LocalDate;
import java.util.Optional;

public class AnimalIntakeView extends Application {

    @Override
    public void start(Stage stage) {

        TextField microchipField = new TextField();
        TextField breedField = new TextField();
        DatePicker intakeDatePicker = new DatePicker();

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Dog", "Cat");

        AnimalService animalService =
                new AnimalService(new InMemoryAnimalDAO());

        InMemoryKennelDAO kennelDAO = new InMemoryKennelDAO();
        KennelService kennelService = new KennelService(kennelDAO);
        String defaultKennelId = "DEFAULT";
        kennelService.createKennel(new Kennel(defaultKennelId, 5));

        Button registerButton = new Button("Register");

        Label capacityLabel = new Label();
        Label capacityWarningLabel = new Label("Kennel at 100% capacity");
        capacityWarningLabel.setStyle("-fx-text-fill: red;");
        capacityWarningLabel.setVisible(false);

        registerButton.setOnAction(e -> {
            try {
                System.out.println("Botón pulsado");

                String microchipId = microchipField.getText();
                String breed = breedField.getText();
                LocalDate intakeDate = intakeDatePicker.getValue();
                String type = typeBox.getValue();

                if (microchipId.isEmpty() || breed.isEmpty()
                        || intakeDate == null || type == null) {
                    showAlert(Alert.AlertType.ERROR,
                            "All fields are required");
                    return;
                }

                Animal animal;
                if (type.equals("Dog")) {
                    animal = new Dog(microchipId, breed, intakeDate);
                } else {
                    animal = new Cat(microchipId, breed, intakeDate);
                }

                kennelService.assignAnimalToKennel(defaultKennelId);
                try {
                    animalService.registerAnimal(animal);
                } catch (IllegalArgumentException ex) {
                    kennelService.releaseAnimalFromKennel(defaultKennelId);
                    throw ex;
                }

                showAlert(Alert.AlertType.INFORMATION,
                        "Animal registered successfully");

                microchipField.clear();
                breedField.clear();
                intakeDatePicker.setValue(null);
                typeBox.setValue(null);

            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            } catch (IllegalStateException ex) {
                showAlert(Alert.AlertType.WARNING, ex.getMessage());
            } finally {
                updateCapacityStatus(kennelDAO, defaultKennelId,
                        capacityLabel, capacityWarningLabel, registerButton);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);

        grid.add(new Label("Breed:"), 0, 1);
        grid.add(breedField, 1, 1);

        grid.add(new Label("Intake Date:"), 0, 2);
        grid.add(intakeDatePicker, 1, 2);

        grid.add(new Label("Animal Type:"), 0, 3);
        grid.add(typeBox, 1, 3);

        grid.add(new Label("Kennel Capacity:"), 0, 4);
        grid.add(capacityLabel, 1, 4);

        grid.add(capacityWarningLabel, 1, 5);

        grid.add(registerButton, 1, 6);

        updateCapacityStatus(kennelDAO, defaultKennelId,
                capacityLabel, capacityWarningLabel, registerButton);

        stage.setScene(new Scene(grid, 400, 300));
        stage.setTitle("Animal Intake");
        stage.show();
    }

    private void updateCapacityStatus(InMemoryKennelDAO kennelDAO,
                                      String kennelId,
                                      Label capacityLabel,
                                      Label warningLabel,
                                      Button registerButton) {
        Optional<Kennel> kennel = kennelDAO.findById(kennelId);
        if (kennel.isEmpty()) {
            capacityLabel.setText("N/A");
            warningLabel.setVisible(false);
            registerButton.setDisable(false);
            return;
        }

        Kennel current = kennel.get();
        int occupied = current.getOccupied();
        int maxCapacity = current.getMaxCapacity();
        capacityLabel.setText(occupied + " / " + maxCapacity);

        boolean full = occupied >= maxCapacity;
        warningLabel.setVisible(full);
        registerButton.setDisable(full);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
