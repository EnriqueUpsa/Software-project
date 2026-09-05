package ui.view;

import controller.HealthController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.HealthRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * Health and nutrition module view.
 *
 * <p>Besides the treatment form, this tab shows the medical history of one
 * animal. Until now the records could be written but not read back, so a
 * veterinarian had no way of checking what had already been done before
 * prescribing again.</p>
 */
public class HealthTabView {
    private final HealthController healthController;
    private final Runnable onDataChanged;

    private final TextField historyMicrochipField = new TextField();
    private final TableView<HealthRecord> historyTable = new TableView<>();

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
                // The treatment that was just saved has to appear in the history
                // without asking the user to load it again.
                if (historyMicrochipField.getText().isBlank()) {
                    historyMicrochipField.setText(microchipField.getText());
                }
                reloadHistory();
                onDataChanged.run();
                ViewAlerts.info("Health record saved");
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        Button loadHistoryButton = new Button("Load History");
        loadHistoryButton.setOnAction(event -> reloadHistory());
        historyMicrochipField.setOnAction(event -> reloadHistory());

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

        grid.add(new Label("History Microchip ID:"), 0, 8);
        grid.add(historyMicrochipField, 1, 8);
        grid.add(loadHistoryButton, 1, 9);

        VBox content = new VBox(12, grid, new Label("Medical history"), buildHistoryTable());
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        return new Tab("Health", content);
    }

    /**
     * Reloads the history table for the microchip typed in the history field.
     *
     * <p>An empty field simply clears the table: asking for the history of
     * nothing is not an error worth an alert.</p>
     */
    private void reloadHistory() {
        String microchipId = historyMicrochipField.getText();
        if (microchipId == null || microchipId.isBlank()) {
            historyTable.getItems().clear();
            return;
        }

        try {
            historyTable.setItems(FXCollections.observableArrayList(
                    healthController.listRecordsFor(microchipId)));
        } catch (IllegalArgumentException ex) {
            historyTable.getItems().clear();
            ViewAlerts.error(ex.getMessage());
        }
    }

    private TableView<HealthRecord> buildHistoryTable() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        historyTable.setPlaceholder(new Label("Load an animal to see its medical history"));

        TableColumn<HealthRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDate() == null ? "-" : c.getValue().getDate().toString()));

        TableColumn<HealthRecord, String> treatmentCol = new TableColumn<>("Treatment");
        treatmentCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getTreatmentType().toString()));

        TableColumn<HealthRecord, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDescription() == null ? "" : c.getValue().getDescription()));

        TableColumn<HealthRecord, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDosage() == null ? "" : c.getValue().getDosage()));

        historyTable.getColumns().setAll(List.of(
                dateCol, treatmentCol, descriptionCol, dosageCol));

        return historyTable;
    }

    private void updateUrgentLabel(Label urgentLabel) {
        urgentLabel.setText(String.valueOf(healthController.getUrgentMedicalDeadlineCount()));
    }
}
