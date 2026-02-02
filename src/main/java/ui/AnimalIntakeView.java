package ui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AnimalIntakeView  extends Application{
    @Override
    public void start(Stage stage) {

        TextField microchipField = new TextField();
        TextField breedField = new TextField();
        DatePicker intakeDatePicker = new DatePicker();

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Dog", "Cat");

        Button registerButton = new Button("Register");

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

    public static void main(String[] args) {
        launch();
    }
}
