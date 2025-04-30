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
import javafx.stage.StageStyle;
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
import main.ui.MusicManager;

import java.net.URL;

//import javax.print.attribute.standard.Media;

public class TitleScreen {

    public void show(Stage stage) {
        // 1) Música
        MusicManager.playMusic("/music/menu-theme.wav");

        // 2) Fondo y logo
        Image backgroundImage = new Image(getClass().getResource("/images/background2.png").toExternalForm(), true);
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(stage.widthProperty());
        backgroundView.fitHeightProperty().bind(stage.heightProperty());

        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);
        logoView.fitWidthProperty().bind(stage.widthProperty().multiply(0.3));

        // 3) Botones
        Button playButton = new Button("Jugar Draft");
        Button exitButton = new Button("Salir");
        String botonEstilo = "-fx-font-size:32px;-fx-padding:30px 70px;"
                + "-fx-background-color:#0096C9;-fx-text-fill:white;-fx-background-radius:10;";
        playButton.setStyle(botonEstilo);
        exitButton.setStyle(botonEstilo);

        // 4) Slider de volumen
        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setPrefWidth(400);
        volumeSlider.valueProperty().addListener((o, __, nv) -> MusicManager.setVolume(nv.doubleValue()));

        // 5) Layout
        VBox botonesBox = new VBox(40, playButton, exitButton);
        botonesBox.setAlignment(Pos.CENTER);
        VBox menuVBox   = new VBox(0, logoView, botonesBox);
        menuVBox.setAlignment(Pos.CENTER);
        HBox volumeBox  = new HBox(volumeSlider);
        volumeBox.setAlignment(Pos.CENTER_RIGHT);
        volumeBox.setPadding(new Insets(0,40,20,0));

        BorderPane layout = new BorderPane();
        layout.setCenter(menuVBox);
        layout.setBottom(volumeBox);

        StackPane root = new StackPane(backgroundView, layout);

        // 6) Creamos la escena **UNA VEZ**
        Scene scene = new Scene(root, 1200, 1000);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        // 7) Transición al pulsar Jugar
        playButton.setOnAction(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), playButton);
            st.setFromX(1); st.setFromY(1);
            st.setToX(1.1); st.setToY(1.1);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();

            st.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(700), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    // **Aquí** no hacemos `stage.setScene(...)` ni `stage.show()` de nuevo,
                    // simplemente delegamos al DraftScreen para que cambie el root.
                    new DraftScreen().show(stage);
                    MusicManager.fadeInMusic();
                });
                fadeOut.play();
            });
        });

        exitButton.setOnAction(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), exitButton);
            st.setFromX(1); st.setFromY(1);
            st.setToX(1.1); st.setToY(1.1);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
            st.setOnFinished(ev -> Platform.exit());
        });
    }
}