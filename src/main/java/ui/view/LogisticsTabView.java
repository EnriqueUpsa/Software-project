package ui.view;

import controller.LogisticsController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Kennel;

import java.util.List;

/**
 * Shelter logistics module view.
 */
public class LogisticsTabView {
    private final LogisticsController logisticsController;
    private final Runnable onDataChanged;

    public LogisticsTabView(LogisticsController logisticsController, Runnable onDataChanged) {
        this.logisticsController = logisticsController;
        this.onDataChanged = onDataChanged;
    }

    public Tab build() {
        TextField newSpaceIdField = new TextField();
        ComboBox<Kennel.SpaceType> newSpaceTypeBox = new ComboBox<>(
                FXCollections.observableArrayList(Kennel.SpaceType.values()));
        newSpaceTypeBox.setValue(Kennel.SpaceType.KENNEL);
        TextField newSpaceCapacityField = new TextField();

        TextField occupancySpaceIdField = new TextField();
        Button assignButton = new Button("Assign Occupancy");
        Button releaseButton = new Button("Release Occupancy");

        TextField transferSourceField = new TextField();
        TextField transferDestinationField = new TextField();
        Button transferButton = new Button("Transfer Occupancy");

        TableView<Kennel> spaceTable = new TableView<>();
        spaceTable.setPlaceholder(new Label("No space created yet"));
        spaceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Kennel, String> idCol = new TableColumn<>("Space ID");
        idCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getKennelId()));

        TableColumn<Kennel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSpaceType().toString()));

        TableColumn<Kennel, String> occupancyCol = new TableColumn<>("Occupancy");
        occupancyCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getOccupied() + " / " + c.getValue().getMaxCapacity()));

        spaceTable.getColumns().setAll(List.of(idCol, typeCol, occupancyCol));

        Runnable refreshSpaces = () -> spaceTable.setItems(
                FXCollections.observableArrayList(logisticsController.getAllSpaces()));

        Button createSpaceButton = new Button("Create Space");
        createSpaceButton.setOnAction(event -> {
            try {
                logisticsController.createSpace(
                        newSpaceIdField.getText(),
                        newSpaceTypeBox.getValue(),
                        newSpaceCapacityField.getText()
                );
                refreshSpaces.run();
                onDataChanged.run();
                ViewAlerts.info("Space created");
            } catch (IllegalArgumentException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        assignButton.setOnAction(event -> {
            try {
                logisticsController.assignOccupancy(occupancySpaceIdField.getText());
                refreshSpaces.run();
                onDataChanged.run();
                ViewAlerts.info("Occupancy assigned");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        releaseButton.setOnAction(event -> {
            try {
                logisticsController.releaseOccupancy(occupancySpaceIdField.getText());
                refreshSpaces.run();
                onDataChanged.run();
                ViewAlerts.info("Occupancy released");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        transferButton.setOnAction(event -> {
            try {
                logisticsController.transferOccupancy(
                        transferSourceField.getText(),
                        transferDestinationField.getText()
                );
                refreshSpaces.run();
                onDataChanged.run();
                ViewAlerts.info("Occupancy transferred");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ViewAlerts.error(ex.getMessage());
            }
        });

        refreshSpaces.run();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("New Space ID:"), 0, 0);
        grid.add(newSpaceIdField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(newSpaceTypeBox, 1, 1);
        grid.add(new Label("Max Capacity:"), 0, 2);
        grid.add(newSpaceCapacityField, 1, 2);
        grid.add(createSpaceButton, 1, 3);

        grid.add(new Label("Space ID (assign/release):"), 0, 5);
        grid.add(occupancySpaceIdField, 1, 5);
        grid.add(assignButton, 1, 6);
        grid.add(releaseButton, 1, 7);

        grid.add(new Label("Transfer From:"), 0, 9);
        grid.add(transferSourceField, 1, 9);
        grid.add(new Label("Transfer To:"), 0, 10);
        grid.add(transferDestinationField, 1, 10);
        grid.add(transferButton, 1, 11);

        grid.add(spaceTable, 0, 13, 3, 1);

        return new Tab("Logistics", grid);
    }
}
