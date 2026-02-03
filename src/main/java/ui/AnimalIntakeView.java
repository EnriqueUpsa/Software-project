package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import dao.InMemoryAnimalDAO;
import service.AnimalService;
import model.Animal;
import model.Dog;
import model.Cat;

import java.time.LocalDate;

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

        Button registerButton = new Button("Register");


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

                animalService.registerAnimal(animal);

                showAlert(Alert.AlertType.INFORMATION,
                        "Animal registered successfully");

                microchipField.clear();
                breedField.clear();
                intakeDatePicker.setValue(null);
                typeBox.setValue(null);

            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
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

        grid.add(registerButton, 1, 4);

        stage.setScene(new Scene(grid, 400, 300));
        stage.setTitle("Animal Intake");
        stage.show();
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
