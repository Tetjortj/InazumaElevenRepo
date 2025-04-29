package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import main.Formation;
import main.PlayerPool;

public class DraftView extends BorderPane {

    private final DraftField draftField;
    private final StatsPanel statsPanel;
    private final HBox banquilloBox = new HBox(10);

    public DraftView(Formation formation, PlayerPool playerPool, StatsPanel statsPanel) {
        this.draftField = new DraftField(formation, playerPool, statsPanel);
        this.statsPanel = statsPanel;

        inicializarVista();
    }

    private void inicializarVista() {
        // Fondo general del draft (adaptable)
        try {
            Image fondo = new Image(getClass().getResource("/images/draft_background.png").toExternalForm());
            BackgroundImage backgroundImage = new BackgroundImage(
                    fondo,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true) // fondo al 100% de ancho y alto
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar el fondo del draft: " + e.getMessage());
            this.setStyle("-fx-background-color: linear-gradient(to bottom, #4e9a06, #3b7c03);");
        }

        // --- Centro: Campo de juego
        StackPane centro = new StackPane(draftField);
        centro.setPadding(new Insets(20));
        centro.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        draftField.prefWidthProperty().bind(centro.widthProperty());
        draftField.prefHeightProperty().bind(centro.heightProperty());
        this.setCenter(centro);

        // --- Derecha: Panel de estadísticas (expandible)
        VBox derecha = new VBox(statsPanel);
        derecha.setPadding(new Insets(20));
        derecha.setAlignment(Pos.TOP_CENTER);
        derecha.setPrefWidth(300);
        derecha.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(statsPanel, Priority.ALWAYS);
        this.setRight(derecha);

        // --- Abajo: Zona de banquillo
        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));
        banquilloBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-border-color: white; -fx-border-width: 2px;");
        banquilloBox.prefWidthProperty().bind(this.widthProperty());
        banquilloBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        this.setBottom(banquilloBox);
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}