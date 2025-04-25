package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TitleScreen {

    public void show(Stage stage) {
        // ---------- Fondo ----------
        Image fondo = new Image(getClass().getResource("/images/background.jpg").toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(fondo,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false));

        // ---------- Logo (opcional) ----------
        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(200);
        logoView.setPreserveRatio(true);

        // Fade para logo
        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(2), logoView);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);
        fadeLogo.play();

        // ---------- Título ----------
        Label title = new Label("Inazuma Draft ⚡");
        title.setFont(new Font("Arial", 36));
        title.setTextFill(Color.WHITE);

        // Fade para título
        FadeTransition fadeTitle = new FadeTransition(Duration.seconds(2), title);
        fadeTitle.setFromValue(0);
        fadeTitle.setToValue(1);
        fadeTitle.setDelay(Duration.seconds(0.5));
        fadeTitle.play();

        // ---------- Input ----------
        TextField nameInput = new TextField();
        nameInput.setPromptText("Introduce tu nombre");

        // ---------- Botón ----------
        Button continueButton = new Button("Comenzar");
        Label message = new Label();
        message.setTextFill(Color.LIGHTYELLOW);

        continueButton.setOnAction(e -> {
            String name = nameInput.getText().trim();
            if (!name.isEmpty()) {
                message.setText("¡Hola " + name + ", bienvenido!");
                // TODO: cambiar a la siguiente pantalla
            } else {
                message.setText("Por favor, introduce un nombre.");
            }
        });

        // ---------- Layout ----------
        VBox layout = new VBox(20, logoView, title, nameInput, continueButton, message);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(backgroundImage));
        layout.setPrefSize(600, 500);
        layout.setPadding(new javafx.geometry.Insets(20));

        // ---------- Mostrar ----------
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Inazuma Draft ⚡");
        stage.show();
    }
}

