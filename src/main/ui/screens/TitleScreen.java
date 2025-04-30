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
        // 1) Start music (unchanged)
        MusicManager.playMusic("/music/menu-theme.wav");

        // 2) Background
        ImageView backgroundView = new ImageView(new Image(
                getClass().getResource("/images/background2.png").toExternalForm(), true));
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(stage.widthProperty());
        backgroundView.fitHeightProperty().bind(stage.heightProperty());

        // 3) Logo
        ImageView logoView = new ImageView(new Image(
                getClass().getResource("/images/logo.png").toExternalForm()));
        logoView.setPreserveRatio(true);
        logoView.fitWidthProperty().bind(stage.widthProperty().multiply(0.3));

        // 4) Buttons
        Button playButton = new Button("Jugar Draft");
        Button exitButton = new Button("Salir");
        String botonEstilo = "-fx-font-size:32px;-fx-padding:30px 70px;"
                + "-fx-background-color:#0096C9;-fx-text-fill:white;-fx-background-radius:10;";
        playButton.setStyle(botonEstilo);
        exitButton.setStyle(botonEstilo);

        // 5) Volume slider
        Slider volumeSlider = new Slider(0,1, MusicManager.getVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setPrefWidth(400);
        volumeSlider.valueProperty().addListener((o,__,nv) ->
                MusicManager.setVolume(nv.doubleValue())
        );

        // 6) Layout of logo + buttons, shifted upward
        VBox botonesBox = new VBox(60, playButton, exitButton);
        botonesBox.setAlignment(Pos.CENTER);

        VBox menuVBox = new VBox(30, logoView, botonesBox);
        menuVBox.setAlignment(Pos.TOP_CENTER);
        // push it down a bit from very top:
        BorderPane.setMargin(menuVBox, new Insets(50,0,0,0));

        HBox volumeBox = new HBox(volumeSlider);
        volumeBox.setAlignment(Pos.CENTER_RIGHT);
        volumeBox.setPadding(new Insets(0,40,20,0));

        BorderPane layout = new BorderPane();
        layout.setCenter(menuVBox);
        layout.setBottom(volumeBox);

        StackPane root = new StackPane(backgroundView, layout);

        // 7) set up scene once
        Scene scene = new Scene(root, 1200, 1000);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        // start with transparent and then fade in
        root.setOpacity(0);
        stage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 8) Button transitions (unchanged)
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