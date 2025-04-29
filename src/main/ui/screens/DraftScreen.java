package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
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
import main.Formation;
import main.FormationRepository;
import main.ui.MusicManager;
import javafx.stage.StageStyle;

import java.util.List;
import main.Card;
import main.CardLoader;
import main.PlayerPool;
import main.ui.DraftField;
import main.ui.StatsPanel;
import main.ui.DraftView;

public class DraftScreen {

    private Formation selectedFormation;

    public void show(Stage stage) {
        // Música
        MusicManager.playMusic("/music/draft-theme.wav");

        // Selección de alineación
        Platform.runLater(() -> {
            mostrarSeleccionAlineacion(stage);
            if (selectedFormation != null) {
                cargarPantallaDraft(stage);
            } else {
                System.err.println("No se seleccionó ninguna formación.");
            }
        });
    }

    private void cargarPantallaDraft(Stage stage) {
        // Cargar cartas
        CardLoader loader = new CardLoader();
        List<Card> todasLasCartas = loader.loadCards();
        PlayerPool playerPool = new PlayerPool(todasLasCartas);

        // Stats
        StatsPanel statsPanel = new StatsPanel();

        // Crear vista final (fusionada)
        DraftView draftView = new DraftView(selectedFormation, playerPool, statsPanel);

        // Crear escena con DraftView
        Scene draftScene = new Scene(draftView);
        draftView.prefWidthProperty().bind(draftScene.widthProperty());
        draftView.prefHeightProperty().bind(draftScene.heightProperty());

        // Mostrar en pantalla completa
        stage.setScene(draftScene);
        stage.setMaximized(true);
        stage.setFullScreenExitHint("");
    }

    private void mostrarSeleccionAlineacion(Stage ownerStage) {
        FormationRepository repository = new FormationRepository();
        List<Formation> opciones = repository.getRandomFormations(3);

        Stage seleccionStage = new Stage();
        seleccionStage.initModality(Modality.APPLICATION_MODAL);
        seleccionStage.initOwner(ownerStage);
        seleccionStage.initStyle(StageStyle.UNDECORATED);
        seleccionStage.setTitle("Selecciona tu alineación");

        seleccionStage.setOnCloseRequest(event -> event.consume());

        Label titulo = new Label("Escoge tu formación");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setTextFill(Color.WHITE);
        titulo.setAlignment(Pos.CENTER);

        VBox botonesBox = new VBox(20);
        botonesBox.setAlignment(Pos.CENTER);

        for (Formation formacion : opciones) {
            Button btn = new Button(formacion.getName());
            btn.setPrefWidth(400);
            btn.setPrefHeight(50);
            btn.setFont(Font.font(18));

            btn.setOnAction(e -> {
                selectedFormation = formacion;
                System.out.println("Formación seleccionada: " + selectedFormation.getName());
                seleccionStage.close();
            });

            botonesBox.getChildren().add(btn);
        }

        VBox layout = new VBox(40, titulo, botonesBox);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #333; -fx-border-color: white; -fx-border-width: 3px;");

        Scene scene = new Scene(layout, 800, 600);
        seleccionStage.setScene(scene);
        seleccionStage.showAndWait();
    }
}