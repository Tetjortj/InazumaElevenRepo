package main.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardSelectorModal extends Stage {

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(FxUtils.getCurrentStage());
        this.setTitle("Selecciona una carta");

        // 1) Contenedor raíz
        VBox layout = new VBox(30);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 30;");
        layout.setAlignment(Pos.CENTER);

        Label titulo = new Label("Selecciona una carta");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox cartasBox = new HBox(40);
        cartasBox.setAlignment(Pos.CENTER);
        cartasBox.setPadding(new Insets(0, 40, 0, 40));

        List<CardView> vistasCartas = new ArrayList<>();
        List<Animation> animaciones = new ArrayList<>();
        final boolean[] cartasActivas = {false};
        final boolean[] animacionCancelada = {false};

        for (Card carta : opciones) {
            CardView cardView = new CardView(carta);
            cardView.setOpacity(0);
            cardView.setTranslateY(50);

            // Solo permitir selección si las cartas están activas
            cardView.setOnMouseClicked(e -> {
                if (!cartasActivas[0]) return;
                this.close();
                onSelect.accept(carta);
            });

            cartasBox.getChildren().add(cardView);
            vistasCartas.add(cardView);
        }

        layout.getChildren().addAll(titulo, cartasBox);
        Scene scene = new Scene(layout, 1300, 550);

        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);


        this.setOnCloseRequest(event -> event.consume());

        // fuerza fin de entrada si se clickea rápido
        scene.setOnMouseClicked(event -> {
            if (!cartasActivas[0] && !animacionCancelada[0]) {
                animacionCancelada[0] = true;
                animaciones.forEach(Animation::stop);
                vistasCartas.forEach(cv -> {
                    cv.setOpacity(1);
                    cv.setTranslateY(0);
                });
                cartasActivas[0] = true;
            }
        });

        // 2) ANIMACIÓN DE ENTRADA DEL PANEL
        // Lo movemos primero fuera de pantalla a la izquierda
        layout.setTranslateX(-scene.getWidth());
        // Y en cuanto el Stage esté listo, lo deslizaremos
        this.setOnShown(ev -> {
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), layout);
            slideIn.setFromX(-scene.getWidth());
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.play();
        });

        // 3) Animación de las cartas (igual que antes)
        Platform.runLater(() -> {
            for (int i = 0; i < vistasCartas.size(); i++) {
                CardView cv = vistasCartas.get(i);

                FadeTransition fade = new FadeTransition(Duration.millis(1200), cv);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(Duration.millis(i * 300));

                TranslateTransition slide = new TranslateTransition(Duration.millis(1200), cv);
                slide.setFromY(50);
                slide.setToY(0);
                slide.setDelay(Duration.millis(i * 300));

                ParallelTransition anim = new ParallelTransition(fade, slide);
                animaciones.add(anim);

                if (i == vistasCartas.size() - 1) {
                    anim.setOnFinished(e -> cartasActivas[0] = true);
                }

                anim.play();
            }
        });
    }
}