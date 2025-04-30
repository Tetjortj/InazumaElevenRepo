package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.util.Duration;
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
        // arrancamos música de selección
        MusicManager.playMusic("/music/draft-theme.wav");

        // obtenemos 3 formaciones al azar
        List<Formation> opciones = new FormationRepository().getRandomFormations(3);

        // creamos el componente que pide la formación y cuando se elige,
        // llama a cargarPantallaDraft(...)
        FormationSelectionScreen selector =
                new FormationSelectionScreen(opciones, chosen -> cargarPantallaDraft(stage, chosen));

        animateSceneTransition(stage, selector);
    }

    private void cargarPantallaDraft(Stage stage, Formation selectedFormation) {
        // cambiamos música
        MusicManager.playMusic("/music/draft-theme.wav");

        // preparativos
        List<Card> all = new CardLoader().loadCards();
        PlayerPool pool = new PlayerPool(all);
        StatsPanel stats = new StatsPanel();
        DraftView draftView = new DraftView(selectedFormation, pool, stats);

        animateSceneTransition(stage, draftView);
    }

    private void animateSceneTransition(Stage stage, Parent newRoot) {
        Scene scene = stage.getScene();
        Parent oldRoot = scene.getRoot();

        double width = scene.getWidth();

        // Prepara el newRoot fuera del viewport, a la derecha
        newRoot.translateXProperty().set(width);
        newRoot.opacityProperty().set(0);

        // Ponemos el newRoot *ya* en la escena (pero fuera de pantalla)
        scene.setRoot(newRoot);

        // Creamos la animación de salida del viejo root
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), oldRoot);
        slideOut.setToX(-width);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), oldRoot);
        fadeOut.setToValue(0.3);

        // Animación de entrada del nuevo root
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newRoot);
        slideIn.setToX(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newRoot);
        fadeIn.setToValue(1.0);

        // Las ejecutamos en paralelo
        ParallelTransition out = new ParallelTransition(slideOut, fadeOut);
        ParallelTransition in  = new ParallelTransition(slideIn,  fadeIn);

        // Cuando termine el “out”, lanzamos el “in”
        out.setOnFinished(e -> in.play());
        out.play();
    }

}