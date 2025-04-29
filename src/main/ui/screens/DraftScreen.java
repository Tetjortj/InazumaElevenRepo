package main.ui.screens;

import javafx.scene.Scene;
import javafx.stage.Stage;

import main.Formation;
import main.FormationRepository;
import main.ui.MusicManager;

import java.util.List;
import main.Card;
import main.CardLoader;
import main.PlayerPool;
import main.ui.StatsPanel;
import main.ui.DraftView;

public class DraftScreen {

    public void show(Stage stage) {
        // Música inicial
        MusicManager.playMusic("/music/menu-theme.wav");

        // Obtener formaciones disponibles
        FormationRepository repository = new FormationRepository();
        List<Formation> opciones = repository.getRandomFormations(3);

        // Crear vista de selección de formación
        FormationSelectionScreen selector = new FormationSelectionScreen(opciones, selectedFormation -> {
            cargarPantallaDraft(stage, selectedFormation);
        });

        Scene seleccionScene = new Scene(selector);
        stage.setScene(seleccionScene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private void cargarPantallaDraft(Stage stage, Formation selectedFormation) {
        // Cambiar música
        MusicManager.playMusic("/music/draft-theme.wav");

        // Preparar datos
        CardLoader loader = new CardLoader();
        List<Card> todasLasCartas = loader.loadCards();
        PlayerPool playerPool = new PlayerPool(todasLasCartas);
        StatsPanel statsPanel = new StatsPanel();

        // Crear vista principal del draft
        DraftView draftView = new DraftView(selectedFormation, playerPool, statsPanel);

        Scene draftScene = new Scene(draftView);
        draftView.prefWidthProperty().bind(draftScene.widthProperty());
        draftView.prefHeightProperty().bind(draftScene.heightProperty());

        stage.setScene(draftScene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }
}