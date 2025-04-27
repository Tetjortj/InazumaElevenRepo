package main.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import main.*;

import java.util.Collections;
import java.util.List;

public class CardViewTestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Cargar jugadores
        CardLoader loader = new CardLoader();
        List<Card> todasLasCartas = loader.loadCards();

        PlayerPool playerPool = new PlayerPool(todasLasCartas);
        List<Card> delanteros = playerPool.getByPosition(Position.FW);

        // Barajamos y elegimos algunos delanteros
        Collections.shuffle(delanteros);
        List<Card> delanterosParaMostrar = delanteros.stream().limit(6).toList();

        // Grid de cartas
        TilePane grid = new TilePane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefColumns(3); // 3 columnas de cartas

        // Fondo general bonito para todas las cartas
        grid.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #d3d3d3);");

        for (Card card : delanterosParaMostrar) {
            CardView cardView = new CardView(card);
            grid.getChildren().add(cardView);
        }

        Scene scene = new Scene(grid, 800, 600);

        primaryStage.setTitle("Vista de Delanteros - Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}