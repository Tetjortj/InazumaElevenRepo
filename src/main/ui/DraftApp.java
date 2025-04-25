package main.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DraftApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inazuma Draft FX");

        Label greetingLabel = new Label("Bienvenido al Inazuma Draft ⚡");
        TextField nameInput = new TextField();
        nameInput.setPromptText("Escribe tu nombre");

        Button continueButton = new Button("Continuar");
        Label resultLabel = new Label();

        continueButton.setOnAction(e -> {
            String nombre = nameInput.getText();
            if (!nombre.isBlank()) {
                resultLabel.setText("Hola, " + nombre + ". ¡Prepárate para el draft!");
            } else {
                resultLabel.setText("Por favor, escribe tu nombre.");
            }
        });

        VBox layout = new VBox(10); // 10 px de espacio entre elementos
        layout.getChildren().addAll(greetingLabel, nameInput, continueButton, resultLabel);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}