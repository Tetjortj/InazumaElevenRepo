package main.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Card;

import java.util.List;
import java.util.function.Consumer;

public class CardSelectorModal extends Stage {

    public CardSelectorModal(List<Card> opciones, Consumer<Card> onSelect) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Selecciona una carta");

        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 20;");
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        for (Card carta : opciones) {
            Button btn = new Button(carta.getName() + " | " + carta.getTeam() + " | Puntuación: " + carta.calcularScore());
            btn.setPrefWidth(400);
            btn.setOnAction(e -> {
                onSelect.accept(carta);
                this.close();
            });

            layout.getChildren().add(btn);
        }

        this.setScene(new Scene(layout, 500, 400));
    }
}

