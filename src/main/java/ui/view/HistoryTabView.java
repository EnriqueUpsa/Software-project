package ui.view;

import controller.HistoryController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.StatusChangeLog;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Audit history view.
 */
public class HistoryTabView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HistoryController historyController;

    public HistoryTabView(HistoryController historyController) {
        this.historyController = historyController;
    }

    public Tab build() {
        TextField microchipField = new TextField();
        TableView<StatusChangeLog> table = new TableView<>();
        // Without an explicit text JavaFX shows its own placeholder in the language of
        // the machine, which would mix languages inside an English application.
        table.setPlaceholder(new Label("Load an animal to see its status history"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<StatusChangeLog, String> oldStatusCol = new TableColumn<>("Old");
        oldStatusCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getOldStatus() == null ? "-" : c.getValue().getOldStatus().name()));

        TableColumn<StatusChangeLog, String> newStatusCol = new TableColumn<>("New");
        newStatusCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNewStatus().name()));

        TableColumn<StatusChangeLog, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getTimestamp().format(DATE_TIME_FORMATTER)));

        table.getColumns().setAll(List.of(oldStatusCol, newStatusCol, timestampCol));

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> {
            try {
                table.setItems(FXCollections.observableArrayList(
                        historyController.getAnimalStatusHistory(microchipField.getText())));
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Microchip ID:"), 0, 0);
        grid.add(microchipField, 1, 0);
        grid.add(loadButton, 2, 0);
        grid.add(table, 0, 1, 3, 1);

        return new Tab("History", grid);
    }
}
