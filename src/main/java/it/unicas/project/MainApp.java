package it.unicas.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label title = new Label("UNICAS Messenger - Final Project");
        TextArea chatArea = new TextArea();
        TextField input = new TextField();
        Button btn = new Button("Enviar");

        btn.setOnAction(e -> {
            Mensaje m = new Mensaje(input.getText(), "User1");
            chatArea.appendText(m.toString() + "\n");
            input.clear();
        });

        VBox layout = new VBox(10, title, chatArea, input, btn);
        Scene scene = new Scene(layout, 400, 450);
        stage.setTitle("Software Engineering 2026");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}