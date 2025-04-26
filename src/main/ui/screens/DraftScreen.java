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

public class DraftScreen {

    private Formation selectedFormation; // Guardar aquí la formación elegida

    public void show(Stage stage) {
        // Music manager
        MusicManager.playMusic("/music/draft-theme.wav");

        // ---------- Volumen ----------
        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setMinorTickCount(1);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setPrefWidth(200);
        volumeSlider.setMaxWidth(200);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            MusicManager.setVolume(newVal.doubleValue());
        });

        HBox volumenBox = new HBox(volumeSlider);
        volumenBox.setAlignment(Pos.BOTTOM_RIGHT);
        volumenBox.setPadding(new Insets(0, 20, 20, 0));

        StackPane root = new StackPane();
        root.getChildren().add(volumenBox);

        root.setStyle("-fx-background-color: #222;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.setX(0);
        stage.setY(0);
        stage.setTitle("Pantalla de Draft");
        stage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // 🔥 Justo después, lanzar selección de alineación y luego cargar el campo
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
        // Crear el pool de cartas
        CardLoader loader = new CardLoader();
        List<Card> todasLasCartas = loader.loadCards();
        PlayerPool playerPool = new PlayerPool(todasLasCartas);

        // Crear el panel de stats
        StatsPanel statsPanel = new StatsPanel();

        // Crear el campo de draft usando la formación elegida
        DraftField draftField = new DraftField(selectedFormation, playerPool, statsPanel);

        // Mostrar todo en un BorderPane
        BorderPane pantallaPrincipal = new BorderPane();
        pantallaPrincipal.setCenter(draftField);
        pantallaPrincipal.setRight(statsPanel);

        Scene draftScene = new Scene(pantallaPrincipal, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        stage.setScene(draftScene);
    }

    private void mostrarSeleccionAlineacion(Stage ownerStage) {
        FormationRepository repository = new FormationRepository();
        List<Formation> opciones = repository.getRandomFormations(3);

        Stage seleccionStage = new Stage();
        seleccionStage.initModality(Modality.APPLICATION_MODAL);
        seleccionStage.initOwner(ownerStage);
        seleccionStage.initStyle(StageStyle.UNDECORATED);
        seleccionStage.setTitle("Selecciona tu alineación");

        seleccionStage.setOnCloseRequest(event -> {
            event.consume(); // No permitir cerrar
        });

        // Título arriba
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
        seleccionStage.showAndWait(); // 🔥 Espera a que el jugador elija
    }
}