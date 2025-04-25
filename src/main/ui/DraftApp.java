package main.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.ui.screens.TitleScreen;

public class DraftApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        TitleScreen titleScreen = new TitleScreen();
        titleScreen.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}