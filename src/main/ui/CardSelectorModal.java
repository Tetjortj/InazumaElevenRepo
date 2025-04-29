package main.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardSelectorModal extends Stage {

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Selecciona una carta");

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
                if (cartasActivas[0]) {
                    onSelect.accept(carta);
                    this.close();
                }
            });

            cartasBox.getChildren().add(cardView);
            vistasCartas.add(cardView);
        }

        layout.getChildren().addAll(titulo, cartasBox);
        Scene scene = new Scene(layout, 1300, 550);
        this.setScene(scene);

        // Si el usuario hace clic en cualquier parte y aún no han terminado las animaciones, se fuerzan
        scene.setOnMouseClicked(event -> {
            if (!cartasActivas[0] && !animacionCancelada[0]) {
                animacionCancelada[0] = true;
                for (Animation anim : animaciones) anim.stop();
                for (CardView cv : vistasCartas) {
                    cv.setOpacity(1);
                    cv.setTranslateY(0);
                }
                cartasActivas[0] = true;
            }
        });

        // Animación de entrada (más lenta, separadas)
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

                // Si es la última animación, activar selección al terminar
                if (i == vistasCartas.size() - 1) {
                    anim.setOnFinished(e -> cartasActivas[0] = true);
                }

                anim.play();
            }
        });
    }
}