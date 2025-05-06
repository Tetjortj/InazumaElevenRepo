package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.util.Duration;
import main.Formation;
import main.FormationRepository;
import main.ui.screens.utils.MusicManager;

import java.util.List;
import main.Card;
import main.CardLoader;
import main.PlayerPool;
import main.ui.StatsPanel;
import main.ui.DraftView;

public class DraftScreen {

    public void show(Stage stage) {
        // arrancamos música de selección
        MusicManager.playMusic("/music/draft-theme.wav");

        // obtenemos 3 formaciones al azar
        List<Formation> opciones = new FormationRepository().getRandomFormations(3);

        // selector llama a cargarPantallaDraft(...) al elegir
        FormationSelectionScreen selector =
                new FormationSelectionScreen(opciones, chosen -> cargarPantallaDraft(stage, chosen));

        // **Title → Selection**: slide+fade
        animateSceneTransition(stage, selector);
    }

    private void cargarPantallaDraft(Stage stage, Formation selectedFormation) {
        // cambiamos música
        MusicManager.playMusic("/music/draft-theme.wav");

        // preparativos
        List<Card> all   = new CardLoader().loadCards();
        PlayerPool pool  = new PlayerPool(all);
        StatsPanel stats = new StatsPanel();
        DraftView draft  = new DraftView(selectedFormation, pool, stats);

        // **Selection → Draft**: cross‐fade
        DraftView draftView = new DraftView(selectedFormation, pool, stats);
        animateFadeTransition(stage, draftView);
    }

    /**
     * Cross‐fade entre la root actual y newRoot.
     * No toca animateSceneTransition para no alterar el Title→Selection.
     */
    private void animateFadeTransition(Stage stage, Parent newRoot) {
        Scene scene    = stage.getScene();
        Parent oldRoot = scene.getRoot();

        // Prepara la nueva pantalla DENTRO del stack (detrás)
        newRoot.setOpacity(1.0);
        StackPane stack = new StackPane(newRoot, oldRoot);
        scene.setRoot(stack);

        // Solo fade-out del viejo root para revelar el newRoot
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1200), oldRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(evt -> {
            // Limpiamos el stack (quitamos both children)
            stack.getChildren().clear();
            // Ya newRoot no tiene padre, lo ponemos de una vez como root
            scene.setRoot(newRoot);
        });

        fadeOut.play();
    }

    // Tu animateSceneTransition original (Title → Selection) queda idéntico
    private void animateSceneTransition(Stage stage, Parent newRoot) {
        Scene scene = stage.getScene();
        Parent oldRoot = scene.getRoot();
        double width = scene.getWidth();

        // slide+fade out/in...
        newRoot.translateXProperty().set(width);
        newRoot.opacityProperty().set(0);
        scene.setRoot(newRoot);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), oldRoot);
        slideOut.setToX(-width);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), oldRoot);
        fadeOut.setToValue(0.3);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newRoot);
        slideIn.setToX(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newRoot);
        fadeIn.setToValue(1.0);

        ParallelTransition out = new ParallelTransition(slideOut, fadeOut);
        ParallelTransition in  = new ParallelTransition(slideIn,  fadeIn);

        out.setOnFinished(e -> in.play());
        out.play();
    }
}