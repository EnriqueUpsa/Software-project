package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.HealthRecord;

public class HealthRecordView extends Application {
    @Override
    public void start(Stage stage) {
        TextField microchipField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField dosageField = new TextField();
        ComboBox<HealthRecord.TreatmentType> treatmentBox = new ComboBox<>();
        treatmentBox.setItems(FXCollections.observableArrayList(
                HealthRecord.TreatmentType.values()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);

        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);

        grid.add(new Label("Dosage:"), 0, 2);
        grid.add(dosageField, 1, 2);

        grid.add(new Label("Treatment Type:"), 0, 3);
        grid.add(treatmentBox, 1, 3);

        stage.setScene(new Scene(grid, 400, 220));
        stage.setTitle("Health Records");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
