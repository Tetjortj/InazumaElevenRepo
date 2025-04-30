package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Card;

public class MiniCardView extends StackPane {
    /** Ancho completo de la carta */
    public static final double CARD_WIDTH = 180;
    /** Altura visible de la mini-carta (cortamos justo antes de las stats) */
    public static final double VISIBLE_HEIGHT = 180;

    public MiniCardView(Card card) {
        // 1) Creamos la vista completa
        CardView fullCard = new CardView(card);
        fullCard.setPrefWidth(CARD_WIDTH);
        fullCard.setMaxWidth(CARD_WIDTH);
        fullCard.setMinWidth(CARD_WIDTH);

        // ***** DISABLE the CardView's built-in hover *****
        fullCard.setOnMouseEntered(null);
        fullCard.setOnMouseExited(null);

        // 2) Clip pane con recorte redondeado
        Pane clipPane = new Pane();
        clipPane.setPrefSize(CARD_WIDTH, VISIBLE_HEIGHT);
        Rectangle clip = new Rectangle(0, 0, CARD_WIDTH, VISIBLE_HEIGHT);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clipPane.setClip(clip);

        // 3) Añadimos la carta SIN desplazarla verticalmente
        fullCard.setLayoutY(0);
        clipPane.getChildren().add(fullCard);

        // 4) Este StackPane toma el tamaño visible
        setPrefSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setMaxSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setMinSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setAlignment(Pos.TOP_CENTER);

        // 5) Montamos todo
        getChildren().add(clipPane);
    }
}