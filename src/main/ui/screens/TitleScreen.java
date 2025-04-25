package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

//import javax.print.attribute.standard.Media;

public class TitleScreen {

    public void show(Stage stage) {
        // Ruta correcta al recurso
        URL resource = getClass().getResource("/music/menu-theme.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
            mediaPlayer.play();
        } else {
            System.err.println("No se encontró el archivo de audio.");
        }

        // ---------- Imagen de fondo ----------
        Image backgroundImage = new Image(getClass().getResource("/images/background2.png").toExternalForm(), true);
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setSmooth(true);
        backgroundView.setCache(true);

        backgroundView.fitWidthProperty().bind(stage.widthProperty());
        backgroundView.fitHeightProperty().bind(stage.heightProperty());

        // ---------- Logo ----------
        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(500); // más grande
        logoView.setPreserveRatio(true);

        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(3), logoView);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);
        fadeLogo.play();

        // ---------- Botones de menú (derecha) ----------
        Button playButton = new Button("Jugar Draft");
        Button exitButton = new Button("Salir");

        // Personalización básica
        String botonEstilo = "-fx-font-size: 36px; -fx-padding: 40px 80px; -fx-background-color: #0096C9; -fx-text-fill: white; -fx-background-radius: 10;";
        playButton.setStyle(botonEstilo);
        exitButton.setStyle(botonEstilo);

        playButton.setOnAction(e -> {
            // Animación de rebote (escala)
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playButton);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.1);  // Aumenta tamaño
            st.setToY(1.1);
            st.setAutoReverse(true);
            st.setCycleCount(2); // Hace el rebote ida y vuelta
            st.play();

            // Acción después de la animación
            st.setOnFinished(ev -> {
                System.out.println("Jugar Draft pulsado");
                // Aquí puedes pasar a la siguiente pantalla en el futuro
                new DraftScreen().show(stage);
            });
        });


        exitButton.setOnAction(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), exitButton);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.1);
            st.setToY(1.1);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();

            st.setOnFinished(ev -> {
                Platform.exit(); // Cierra la app
            });
        });


        VBox menuDerecha = new VBox(80, logoView, playButton, exitButton);
        menuDerecha.setAlignment(Pos.CENTER);
        menuDerecha.setPadding(new Insets(0, 300, 100, 0)); // Alineado a la derecha

        // ---------- Layout general con fondo ----------
        BorderPane layout = new BorderPane();
        layout.setRight(menuDerecha);

        StackPane root = new StackPane(backgroundView, layout);

        Scene scene = new Scene(root, 1200, 1000);
        stage.setScene(scene);
        stage.setTitle("Inazuma Draft ⚡");
        stage.setMaximized(true);
        stage.show();
    }
}
