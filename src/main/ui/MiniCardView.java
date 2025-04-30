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
    /** Ancho fijo de la carta (idéntico a CardView) */
    public static final double CARD_WIDTH     = 180;
    /**
     * Altura visible: ahora ampliada para incluir nombre + primer stat
     * Ajusta este valor hasta que veas todo lo que quieras mostrar.
     */
    public static final double VISIBLE_HEIGHT = 160;

    /**
     * Desplazamiento vertical de la carta completa hacia arriba
     * para que el clip muestre la zona correcta.
     * Ajusta este valor si te sigue cortando algo.
     */
    private static final double SHIFT_Y        = 0;

    public MiniCardView(Card card) {
        // 1) Creamos la CardView completa
        CardView fullCard = new CardView(card);
        fullCard.setPrefWidth(CARD_WIDTH);
        fullCard.setMaxWidth(CARD_WIDTH);
        fullCard.setMinWidth(CARD_WIDTH);

        // Desactivamos el hover interno de CardView
        fullCard.setOnMouseEntered(null);
        fullCard.setOnMouseExited(null);

        // 2) Preparamos un Pane actúe como "viewport" con clip redondeado
        Pane clipPane = new Pane();
        clipPane.setPrefSize(CARD_WIDTH, VISIBLE_HEIGHT);
        clipPane.setMaxSize(CARD_WIDTH, VISIBLE_HEIGHT);
        clipPane.setMinSize(CARD_WIDTH, VISIBLE_HEIGHT);

        Rectangle clip = new Rectangle(CARD_WIDTH, VISIBLE_HEIGHT);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clipPane.setClip(clip);

        // 3) Desplazamos la carta hacia arriba para ajustar el recorte
        fullCard.setLayoutY(-SHIFT_Y);

        // 4) Añadimos la carta recortada al viewport
        clipPane.getChildren().add(fullCard);

        // 5) Este StackPane mide exactamente el área visible
        setPrefSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setMaxSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setMinSize(CARD_WIDTH, VISIBLE_HEIGHT);
        setAlignment(Pos.TOP_CENTER);

        // 6) Montamos todo
        getChildren().add(clipPane);
    }
}