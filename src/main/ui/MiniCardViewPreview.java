package main.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.Card;
import main.CardLoader;

import java.util.List;

public class MiniCardViewPreview extends Application {

    @Override
    public void start(Stage stage) {
        // 1) Carga cartas
        CardLoader loader = new CardLoader();
        List<Card> todas = loader.loadCards();

        // 2) HBox con spacing
        HBox row = new HBox(20);
        row.setPadding(new Insets(20));
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-background-color: #333;");

        // 3) Ponemos 5 mini–cards
        for (int i = 0; i < Math.min(4, todas.size()); i++) {
            MiniCardView mini = new MiniCardView(todas.get(i));
            HBox.setMargin(mini, new Insets(0, 0, 0, 60));
            row.getChildren().add(mini);
        }

        // 4) ScrollPane NO fuerza ancho, solo el alto
        ScrollPane scroll = new ScrollPane(row);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(false);                     // <- importante
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #333; -fx-border-color: transparent;");

        // 5) Escena fija
        Scene scene = new Scene(scroll, 1200, 600, Color.DARKSLATEGRAY);
        stage.setTitle("Preview de MiniCardView");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}