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

    private static final Duration PANEL_SLIDE_DURATION = Duration.millis(400);
    private static final Duration CARD_REVEAL_DURATION = Duration.millis(600);
    private static final Duration CARD_REVEAL_STAGGER  = Duration.millis(300);

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect) {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(FxUtils.getCurrentStage());
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Selecciona una carta");

        // 1) Layout base
        VBox layout = new VBox(30);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 30;");
        layout.setAlignment(Pos.CENTER);

        Label titulo = new Label("Selecciona una carta");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        titulo.setTranslateY(-45);

        HBox cartasBox = new HBox(40);
        cartasBox.setAlignment(Pos.CENTER);

        // 2) Creamos un placeholder full–size para cada carta
        List<PlayerCell> placeholders = new ArrayList<>();
        for (int i = 0; i < opciones.size(); i++) {
            Card carta = opciones.get(i);

            // nuevo constructor fullSize=true: sin pivote
            PlayerCell placeholder = new PlayerCell(i, carta.getPosition(), true);

            // hacemos girar TODO el PlayerCell
            placeholder.setRotationAxis(Rotate.Y_AXIS);
            placeholder.setRotate(0); // parte frontal al inicio

            // hasta que no revelemos, no permitimos click
            placeholder.setOnMouseClicked(e -> {/* nada */});

            placeholders.add(placeholder);
            cartasBox.getChildren().add(placeholder);
        }

        layout.getChildren().addAll(titulo, cartasBox);
        Scene scene = new Scene(layout, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        // impedir cierre con X o ESC
        setOnCloseRequest(e -> e.consume());

        // 3) Slide-in y luego revelado
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
        // 1) PRE-CREACIÓN de todos los CardView
        List<CardView> reales = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            Card carta = cards.get(i);                // <-- carta es efectivamente final
            PlayerCell placeholder = placeholders.get(i);
            StackPane container = placeholder.getCartaContainer();

            CardView cv = new CardView(carta);
            double tw = container.getPrefWidth(), th = container.getPrefHeight();
            double sx = tw / cv.getPrefWidth(), sy = th / cv.getPrefHeight();
            double scale = Math.min(sx, sy);
            cv.setScaleX(scale);
            cv.setScaleY(scale);
            cv.setVisible(false);
            reales.add(cv);
            container.getChildren().add(cv);
        }

        // 2) Girado y swap usando variables locales finales
        for (int i = 0; i < placeholders.size(); i++) {
            final PlayerCell placeholder = placeholders.get(i); // final
            final Card          carta       = cards.get(i);    // final
            final CardView      real        = reales.get(i);

            // media vuelta 0° → 90°
            RotateTransition half1 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half1.setAxis(Rotate.Y_AXIS);
            half1.setFromAngle(0);  half1.setToAngle(90);
            half1.setDelay(CARD_REVEAL_STAGGER.multiply(i));
            half1.setOnFinished(evt -> {
                // sólo alternamos visibilidad, nada más
                placeholder.getCartaContainer().getChildren().get(0).setVisible(false);
                real.setVisible(true);
            });

            // media vuelta 90° → 0°
            RotateTransition half2 = new RotateTransition(CARD_REVEAL_DURATION.divide(2), placeholder);
            half2.setAxis(Rotate.Y_AXIS);
            half2.setFromAngle(90); half2.setToAngle(0);
            half2.setOnFinished(evt -> {
                // ya podemos clicar y devolver la carta seleccionada
                placeholder.setOnMouseClicked(e2 -> {
                    close();
                    onSelect.accept(carta);
                });
            });

            new SequentialTransition(half1, half2).play();
        }
    }
}