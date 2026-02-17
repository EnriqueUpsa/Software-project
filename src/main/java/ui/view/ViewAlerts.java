package ui.view;

import javafx.scene.control.Alert;

/**
 * Shared JavaFX alert helpers for view classes.
 */
public final class ViewAlerts {
    private ViewAlerts() {
    }

    public static void info(String message) {
        show(Alert.AlertType.INFORMATION, message);
    }

    public static void error(String message) {
        show(Alert.AlertType.ERROR, message);
    }

    private static void show(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
