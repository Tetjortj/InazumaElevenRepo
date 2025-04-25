package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
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
import main.ui.MusicManager;

public class DraftScreen {

    public void show(Stage stage) {
        // Music manager
        MusicManager.playMusic("/music/draft-theme.wav");

        BorderPane root = new BorderPane();

        Label title = new Label("¡Elige tu equipo!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        Label draftInfo = new Label("Aquí irá el campo de juego y las cartas.");
        draftInfo.setFont(Font.font(18));
        draftInfo.setTextFill(Color.LIGHTGRAY);
        BorderPane.setAlignment(draftInfo, Pos.CENTER);
        root.setCenter(draftInfo);

        root.setStyle("-fx-background-color: #222;");

        Scene scene = new Scene(root);

        // 🔥 Establecer la escena
        stage.setScene(scene);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();


        // 🔥 Forzar tamaño igual que la anterior
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());

        stage.setX(0);
        stage.setY(0);

        stage.setTitle("Pantalla de Draft");
        stage.show();
    }
}