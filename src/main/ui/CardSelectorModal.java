package main.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardSelectorModal extends Stage {

    private static final Duration PANEL_SLIDE_DURATION   = Duration.millis(400);
    private static final Duration ENTRY_DURATION         = Duration.millis(400);
    private static final Duration CARD_REVEAL_DURATION   = Duration.millis(600);
    private static final Duration CARD_REVEAL_STAGGER    = Duration.millis(500);
    private static final double   INITIAL_SCALE          = 1.6;

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect) {
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
        Scene scene = new Scene(layout, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        // Impedir cierre con X o ESC
        setOnCloseRequest(e -> e.consume());

        // --- 3) Slide-in y luego revelado ---
        setOnShown(e -> {
            TranslateTransition slideIn = new TranslateTransition(PANEL_SLIDE_DURATION, layout);
            slideIn.setFromX(-scene.getWidth());
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.setOnFinished(ev -> playCardReveal(placeholders, opciones, onSelect));
            slideIn.play();
        });
    }

    private void playCardReveal(List<PlayerCell> placeholders,
                                List<Card> cards,
                                Consumer<Card> onSelect) {
        int n = placeholders.size();
        Duration totalEntry = ENTRY_DURATION.add(CARD_REVEAL_STAGGER.multiply(n - 1));

        for (int i = 0; i < n; i++) {
            PlayerCell placeholder = placeholders.get(i);
            Card carta            = cards.get(i);
            StackPane container   = placeholder.getCartaContainer();

            // --- 1) Animación de entrada (fade + scale) ---
            FadeTransition fadeIn = new FadeTransition(ENTRY_DURATION, placeholder);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(ENTRY_DURATION, placeholder);
            scaleIn.setFromX(INITIAL_SCALE);
            scaleIn.setFromY(INITIAL_SCALE);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);

            ParallelTransition entry = new ParallelTransition(fadeIn, scaleIn);
            entry.setDelay(CARD_REVEAL_STAGGER.multiply(i));

            entry.play();

            // --- 2) Preparamos CardView oculto ---
            CardView real = new CardView(carta);
            double tw = container.getPrefWidth();
            double th = container.getPrefHeight();
            double sx = tw / real.getPrefWidth();
            double sy = th / real.getPrefHeight();
            double sc = Math.min(sx, sy);
            real.setScaleX(sc);
            real.setScaleY(sc);
            real.setVisible(false);
            container.getChildren().add(real);

            // --- 3) Flip: mitad1 y mitad2 ---
            RotateTransition half1 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half1.setAxis(Rotate.Y_AXIS);
            half1.setFromAngle(0);
            half1.setToAngle(90);
            half1.setOnFinished(evt -> {
                // En 90° swap placeholder → real
                container.getChildren().get(0).setVisible(false);
                real.setVisible(true);
            });

            RotateTransition half2 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half2.setAxis(Rotate.Y_AXIS);
            half2.setFromAngle(90);
            half2.setToAngle(0);
            half2.setOnFinished(evt -> {
                // Activar selección final
                placeholder.setOnMouseClicked(e2 -> {
                    close();
                    onSelect.accept(carta);
                });
            });

            // --- 4) Secuencia completa: entry → half1 → half2 ---
            // 3) Encadenamos con una pausa inicial = totalEntry + stagger(i)
            PauseTransition pause = new PauseTransition(
                    totalEntry.add(CARD_REVEAL_STAGGER.multiply(i))
            );
            SequentialTransition flipSeq = new SequentialTransition(pause, half1, half2);
            flipSeq.play();
        }
    }
}