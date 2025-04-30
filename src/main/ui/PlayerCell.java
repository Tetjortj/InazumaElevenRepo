package main.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import main.Card;
import main.Position;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlayerCell extends StackPane {
    /** Coincide con MiniCardView.CARD_WIDTH */
    public static final double CELL_WIDTH  = MiniCardView.CARD_WIDTH;
    /** Coincide con MiniCardView.VISIBLE_HEIGHT */
    public static final double CELL_HEIGHT = MiniCardView.VISIBLE_HEIGHT;

    private final StackPane cartaContainer = new StackPane();
    private final Label pivote;
    private boolean unlocked = false;
    private final int index;
    private final Position position;

    public PlayerCell(int index, Position position) {
        this.index    = index;
        this.position = position;

        // 1) Placeholder gris redondeado + logo
        Rectangle fondo = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
        fondo.setFill(Color.GRAY);
        fondo.setArcWidth(20);
        fondo.setArcHeight(20);
        fondo.setStroke(Color.DARKGRAY);

        ImageView logo = new ImageView(
                new Image(getClass().getResource("/images/logo.png").toExternalForm())
        );
        logo.setFitWidth(CELL_WIDTH * 0.5);
        logo.setPreserveRatio(true);

        StackPane placeholder = new StackPane(fondo, logo);
        placeholder.setPrefSize(CELL_WIDTH, CELL_HEIGHT);

        cartaContainer.getChildren().setAll(placeholder);
        cartaContainer.setPrefSize(CELL_WIDTH, CELL_HEIGHT);

        // 2) Pivote justo abajo, sin espacio extra
        pivote = new Label(position.name() + " 0");
        pivote.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        pivote.setTextFill(Color.WHITE);
        pivote.setBackground(new Background(new BackgroundFill(
                Color.rgb(30,30,30), new CornerRadii(6), Insets.EMPTY
        )));
        pivote.setPadding(new Insets(2,8,2,8));

        // 3) Agrupamos sin separación para que pivote toque la mini-carta
        VBox wrapper = new VBox(0, cartaContainer, pivote);
        wrapper.setAlignment(Pos.CENTER);

        getChildren().setAll(wrapper);
        setAlignment(Pos.CENTER);

        // 4) Tamaño total = carta + pivote (alto pivote ≈ fontSize + padding)
        double pivotHeight = pivote.getFont().getSize() + 4;
        setPrefSize(CELL_WIDTH, CELL_HEIGHT + pivotHeight);
        setMinSize (CELL_WIDTH, CELL_HEIGHT + pivotHeight);
        setMaxSize (CELL_WIDTH, CELL_HEIGHT + pivotHeight);

        // 5) Hover anima TODO el PlayerCell (no sólo el texto interno)
        ScaleTransition stIn  = new ScaleTransition(Duration.millis(200), this);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), this);
        stIn .setToX(1.05); stIn .setToY(1.05);
        stOut.setToX(1.00); stOut.setToY(1.00);
        setOnMouseEntered(e -> {
            stOut.stop();
            stIn.playFromStart();
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(e -> {
            stIn.stop();
            stOut.playFromStart();
            setCursor(Cursor.DEFAULT);
        });
    }

    /** Elimina el placeholder, deja la celda “lista” **/
    public void desbloquear(Card card) {
        unlocked = true;
        cartaContainer.getChildren().clear();
    }

    public boolean isUnlocked()            { return unlocked; }
    public int getIndex()                  { return index; }
    public Position getPosition()          { return position; }
    public StackPane getCartaContainer()   { return cartaContainer; }
    public void setQuimica(int q)          { pivote.setText(position.name() + " " + q); }
}