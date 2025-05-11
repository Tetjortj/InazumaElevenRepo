package main.ui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.Card;
import main.ui.screens.utils.FxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardSelectorModal extends Stage {

    private static final Duration PANEL_SLIDE_DURATION   = Duration.millis(400);
    private static final Duration ENTRY_DURATION         = Duration.millis(200);
    private static final Duration CARD_REVEAL_DURATION   = Duration.millis(300);
    private static final Duration CARD_REVEAL_STAGGER    = Duration.millis(50);
    private static final double   INITIAL_SCALE          = 1.6;

    private Consumer<Card> onPeek;
    private Consumer<Card> onSelect;

    private boolean animateOnShow;

    // constructor por defecto (animado)
    public CardSelectorModal(List<Card> opciones,
                             Consumer<Card> onSelect,
                             Consumer<Card> onPeek) {
        this(opciones, onSelect, onPeek, true);
    }

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect,  Consumer<Card> onPeek, boolean animateOnShow) {
        this.onSelect = onSelect;
        this.onPeek   = onPeek;
        this.animateOnShow = animateOnShow;

        initModality(Modality.APPLICATION_MODAL);
        initOwner(FxUtils.getCurrentStage());
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Selecciona una carta");

        // --- 1) Layout base ---
        VBox layout = new VBox(30);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 30;");
        layout.setAlignment(Pos.CENTER);

        Label titulo = new Label("Selecciona una carta");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        titulo.setTranslateY(-45);

        HBox cartasBox = new HBox(40);
        cartasBox.setAlignment(Pos.CENTER);

        // --- 2) Creamos placeholders invisibles ---
        List<PlayerCell> placeholders = new ArrayList<>();
        for (int i = 0; i < opciones.size(); i++) {
            Card carta = opciones.get(i);
            PlayerCell placeholder = new PlayerCell(i, carta.getPosition(), true);

            // Inicialmente invisible y escalado para entrada
            placeholder.setOpacity(0);
            placeholder.setScaleX(INITIAL_SCALE);
            placeholder.setScaleY(INITIAL_SCALE);

            // Giramos en Y (frontal)
            placeholder.setRotationAxis(Rotate.Y_AXIS);
            placeholder.setRotate(0);

            // Click bloqueado hasta flip final
            placeholder.setOnMouseClicked(e -> { /* no-op */ });

            placeholders.add(placeholder);
            cartasBox.getChildren().add(placeholder);
        }

        layout.getChildren().addAll(titulo, cartasBox);

        // 1) Preparamos el flag y la referencia al master transition
        final boolean[] animationsDone = { false };
        final SequentialTransition[] masterRef = { null };

        // 2) Skip-on-click: si pinchas antes de terminar, forzamos estado final
        layout.setOnMouseClicked(evt -> {
            if (!animationsDone[0] && masterRef[0] != null) {
                masterRef[0].jumpTo(masterRef[0].getTotalDuration());  // detenemos TODO

                for (int j = 0; j < placeholders.size(); j++) {
                    PlayerCell pc = placeholders.get(j);
                    Card c        = opciones.get(j);
                    StackPane cont = pc.getCartaContainer();

                    // Forzamos visibilidad y escala
                    pc.setOpacity(1);
                    pc.setScaleX(1);
                    pc.setScaleY(1);
                    pc.setRotate(0);

                    // Reemplazamos contenedor por la CardView real
                    cont.getChildren().clear();
                    CardView real = new CardView(c);
                    double tw = cont.getPrefWidth(), th = cont.getPrefHeight();
                    double sx = tw / real.getPrefWidth(), sy = th / real.getPrefHeight();
                    double sc = Math.min(sx, sy);
                    real.setScaleX(sc);
                    real.setScaleY(sc);
                    cont.getChildren().add(real);

                    // Activamos clic definitivo
                    instalarClickHandler(pc, c, onSelect, onPeek);
                }

                animationsDone[0] = true;
            }
        });

        Scene scene = new Scene(layout, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        // Impedir cierre con X o ESC
        setOnCloseRequest(e -> e.consume());

        if (animateOnShow) {
            setOnShown(e -> {
                TranslateTransition slideIn = new TranslateTransition(PANEL_SLIDE_DURATION, layout);
                slideIn.setFromX(-scene.getWidth());
                slideIn.setToX(0);
                slideIn.setInterpolator(Interpolator.EASE_OUT);
                slideIn.setOnFinished(ev -> {
                    // Capturamos el master transition para poder pararlo
                    masterRef[0] = playCardReveal(placeholders, opciones, onSelect, animationsDone);
                });
                slideIn.play();
            });
        } else {
            setOnShown(e -> {
                // ✋ en vez de playCardReveal, ejecuta este código:
                for (int j = 0; j < placeholders.size(); j++) {
                    PlayerCell pc = placeholders.get(j);
                    Card c = opciones.get(j);
                    StackPane cont = pc.getCartaContainer();

                    pc.setOpacity(1);
                    pc.setScaleX(1);
                    pc.setScaleY(1);
                    pc.setRotate(0);

                    cont.getChildren().clear();
                    CardView real = new CardView(c);
                    // …calcula sc…

                    double tw = cont.getPrefWidth(), th = cont.getPrefHeight();
                    double sx = tw / real.getPrefWidth(), sy = th / real.getPrefHeight();
                    double sc = Math.min(sx, sy);
                    real.setScaleX(sc);
                    real.setScaleY(sc);
                    cont.getChildren().add(real);

                    instalarClickHandler(pc, c, onSelect, onPeek);
                }
                // marca animationsDone para que skip-on-click no haga nada
                animationsDone[0] = true;
            });
        }
    }

    private SequentialTransition playCardReveal(List<PlayerCell> placeholders,
                                List<Card> cards,
                                Consumer<Card> onSelect,
                                boolean[] animationsDone) {
        int n = placeholders.size();

        // 1) Construimos todas las animaciones de entrada en paralelo
        List<Animation> entryAnims = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PlayerCell pc = placeholders.get(i);

            // fade+scale con delay i * CARD_REVEAL_STAGGER
            FadeTransition fade = new FadeTransition(ENTRY_DURATION, pc);
            fade.setFromValue(0);
            fade.setToValue(1);

            ScaleTransition scale = new ScaleTransition(ENTRY_DURATION, pc);
            scale.setFromX(INITIAL_SCALE);
            scale.setFromY(INITIAL_SCALE);
            scale.setToX(1);
            scale.setToY(1);

            ParallelTransition entry = new ParallelTransition(fade, scale);
            entry.setDelay(CARD_REVEAL_STAGGER.multiply(i));
            entryAnims.add(entry);
        }
        ParallelTransition allEntries = new ParallelTransition();
        allEntries.getChildren().addAll(entryAnims);

        // 2) Construimos todos los flips en secuencia, uno detrás de otro
        SequentialTransition allFlips = new SequentialTransition();
        for (int i = 0; i < n; i++) {
            PlayerCell placeholder = placeholders.get(i);
            Card carta            = cards.get(i);
            StackPane container   = placeholder.getCartaContainer();

            // Preparamos la CardView “real” pero oculta
            CardView real = new CardView(carta);
            double tw = container.getPrefWidth(), th = container.getPrefHeight();
            double sx = tw / real.getPrefWidth(), sy = th / real.getPrefHeight();
            double sc = Math.min(sx, sy);
            real.setScaleX(sc);
            real.setScaleY(sc);
            real.setVisible(false);
            container.getChildren().add(real);

            // mitad1: 0° → 90°
            RotateTransition half1 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half1.setAxis(Rotate.Y_AXIS);
            half1.setFromAngle(0);
            half1.setToAngle(90);
            half1.setOnFinished(evt -> {
                // swap placeholder → real
                container.getChildren().get(0).setVisible(false);
                real.setVisible(true);
            });

            // mitad2: 90° → 0°
            RotateTransition half2 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half2.setAxis(Rotate.Y_AXIS);
            half2.setFromAngle(90);
            half2.setToAngle(0);
            half2.setOnFinished(evt -> {
                instalarClickHandler(placeholder, carta, onSelect, onPeek);
            });

            allFlips.getChildren().addAll(half1, half2);
        }

        // 3) Encadenamos TODO: primero entradas, luego flips
        SequentialTransition master = new SequentialTransition(allEntries, allFlips);
        master.setOnFinished(ev -> animationsDone[0] = true);
        master.play();
        return master;
    }

    private void instalarClickHandler(PlayerCell pc, Card carta,
                                      Consumer<Card> onSelect,
                                      Consumer<Card> onPeek) {
        pc.setOnMouseClicked(e -> {
            if (e.getButton()==MouseButton.PRIMARY) {
                System.out.println("SELECT ➞ " + carta);
                onSelect.accept(carta);
                close();
            } else if (e.getButton()==MouseButton.SECONDARY) {
                System.out.println("PEEK ➞ " + carta);
                onPeek.accept(carta);
                close();
            }
            e.consume();
        });
    }
}