package main.ui.runneable;

import javafx.application.Application;
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