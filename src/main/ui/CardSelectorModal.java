package main.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

        // 1) Contenedor raíz
        VBox layout = new VBox(30);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 30;");
        layout.setAlignment(Pos.CENTER);

        Label titulo = new Label("Selecciona una carta");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        titulo.setTranslateY(-45);

        HBox cartasBox = new HBox(40);
        cartasBox.setAlignment(Pos.CENTER);
        cartasBox.setPadding(new Insets(0, 40, 0, 40));

        // 2) Preparamos vistas y estados
        List<CardView> vistasCartas = new ArrayList<>();
        List<Animation> cardRevealAnims = new ArrayList<>();
        final boolean[] cartasActivas = {false};

        for (Card carta : opciones) {
            CardView cv = new CardView(carta);

            // girada 180° en Y al inicio
            cv.setRotationAxis(Rotate.Y_AXIS);
            cv.setRotate(180);

            // efecto de glow preparado
            Glow glow = new Glow(0);
            cv.setEffect(glow);

            // click solo tras revelado
            cv.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (!cartasActivas[0]) return;
                close();
                onSelect.accept(carta);
            });

            cartasBox.getChildren().add(cv);
            vistasCartas.add(cv);
        }

        layout.getChildren().addAll(titulo, cartasBox);
        Scene scene = new Scene(layout, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        // impedir cierres prematuros
        setOnCloseRequest(e -> e.consume());

        // 3) Slide-in del panel desde la izquierda
        layout.setTranslateX(-scene.getWidth());
        setOnShown(e -> {
            TranslateTransition slideIn = new TranslateTransition(PANEL_SLIDE_DURATION, layout);
            slideIn.setFromX(-scene.getWidth());
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.setOnFinished(ev -> playCardReveal(vistasCartas, cardRevealAnims, cartasActivas));
            slideIn.play();
        });
    }

    private void playCardReveal(List<CardView> vistas,
                                List<Animation> anims,
                                boolean[] cartasActivas) {
        // Revelado secuencial con brillo
        for (int i = 0; i < vistas.size(); i++) {
            CardView cv = vistas.get(i);
            Glow glow = (Glow) cv.getEffect();

            // rotación Y de 180° → 0°
            RotateTransition rot = new RotateTransition(CARD_REVEAL_DURATION, cv);
            rot.setAxis(Rotate.Y_AXIS);
            rot.setFromAngle(180);
            rot.setToAngle(0);

            // glow rápido: 1 → 0
            Timeline shine = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 1.0)),
                    new KeyFrame(CARD_REVEAL_DURATION, new KeyValue(glow.levelProperty(), 0.0))
            );

            ParallelTransition reveal = new ParallelTransition(rot, shine);
            reveal.setDelay(CARD_REVEAL_STAGGER.multiply(i));
            if (i == vistas.size() - 1) {
                reveal.setOnFinished(e -> cartasActivas[0] = true);
            }
            anims.add(reveal);
            reveal.play();
        }
    }
}