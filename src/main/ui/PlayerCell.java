package main.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Polygon;
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
    private final Label pivoteLabel;
    private boolean unlocked = false;
    private final int index;
    private final Position position;
    private double baseScale = 1.0;

    private final Polygon pivoteTip;
    private final VBox pivoteContainer;

    private final StackPane placeholderNode;

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

        // ► Asignamos el campo placeholderNode para poder resetear luego
        this.placeholderNode = placeholder;

        // Lo ponemos en el container
        cartaContainer.getChildren().setAll(placeholderNode);

        cartaContainer.getChildren().setAll(placeholder);
        cartaContainer.setPrefSize(CELL_WIDTH, CELL_HEIGHT);

        // 2) Pivote justo abajo, sin espacio extra
        pivoteLabel = new Label(position.name() + " 0");
        pivoteLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        pivoteLabel.setTextFill(Color.WHITE);
        pivoteLabel.setBackground(new Background(new BackgroundFill(
                Color.rgb(30,30,30), new CornerRadii(6), Insets.EMPTY
        )));
        pivoteLabel.setPadding(new Insets(2,8,2,8));

        //    y triángulo salida
        pivoteTip = new Polygon(
                0.0, 0.0,
                12.0, 0.0,
                6.0, 6.0
        );
        pivoteTip.setFill(Color.rgb(30,30,30));

        // 3) Contenedor vertical pivot
        pivoteContainer = new VBox(0, cartaContainer, pivoteLabel, pivoteTip);
        pivoteContainer.setAlignment(Pos.TOP_CENTER);

        // 4) Ensamblar todo
        getChildren().setAll(pivoteContainer);
        setAlignment(Pos.CENTER);

        // 5) Ajustar tamaño total
        double pivotHeight = pivoteLabel.getFont().getSize() + 4 + 6; // etiqueta + triángulo
        setPrefSize(CELL_WIDTH, CELL_HEIGHT + pivotHeight);
        setMinSize (CELL_WIDTH, CELL_HEIGHT + pivotHeight);
        setMaxSize (CELL_WIDTH, CELL_HEIGHT + pivotHeight);

        // 5) Hover anima TODO el PlayerCell (no sólo el texto interno)
        ScaleTransition stIn  = new ScaleTransition(Duration.millis(200), this);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), this);

        setOnMouseEntered(e -> {
            stOut.stop();
            double target = baseScale * 1.05;
            stIn.setToX(target);
            stIn.setToY(target);
            stIn.playFromStart();
            setCursor(Cursor.HAND);
        });

        setOnMouseExited(e -> {
            stIn.stop();
            stOut.setToX(baseScale);
            stOut.setToY(baseScale);
            stOut.playFromStart();
            setCursor(Cursor.DEFAULT);
        });
    }

    /** Placeholder “full–size” (sin pivote) */
    public PlayerCell(int index, Position position, boolean fullSize) {
        this.index    = index;
        this.position = position;

        double w = fullSize ? 180 : CELL_WIDTH;
        double h = fullSize ? 260 : CELL_HEIGHT;

        // 1) Placeholder gris redondeado + logo
        Rectangle fondo = new Rectangle(w, h);
        fondo.setFill(Color.GRAY);
        fondo.setArcWidth(20);
        fondo.setArcHeight(20);
        fondo.setStroke(Color.DARKGRAY);

        ImageView logo = new ImageView(
                new Image(getClass().getResource("/images/logo.png").toExternalForm())
        );
        logo.setFitWidth(w * 0.5);
        logo.setPreserveRatio(true);

        StackPane placeholder = new StackPane(fondo, logo);
        placeholder.setPrefSize(w, h);

        // ► Asignamos el campo placeholderNode para poder resetear luego
        this.placeholderNode = placeholder;

        // Lo ponemos en el container
        cartaContainer.getChildren().setAll(placeholderNode);

        cartaContainer.getChildren().setAll(placeholder);
        cartaContainer.setPrefSize(w, h);
        cartaContainer.setMinSize(w, h);
        cartaContainer.setMaxSize(w, h);

        // 2) Sólo si no es fullSize creamos pivote
        if (!fullSize) {
            // 2) Pivote justo abajo, sin espacio extra
            pivoteLabel = new Label(position.name() + " 0");
            pivoteLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            pivoteLabel.setTextFill(Color.WHITE);
            pivoteLabel.setBackground(new Background(new BackgroundFill(
                    Color.rgb(30,30,30), new CornerRadii(6), Insets.EMPTY
            )));
            pivoteLabel.setPadding(new Insets(2,8,2,8));

            //    y triángulo salida
            pivoteTip = new Polygon(
                    0.0, 0.0,
                    12.0, 0.0,
                    6.0, 6.0
            );
            pivoteTip.setFill(Color.rgb(30,30,30));

            // 3) Contenedor vertical pivot
            pivoteContainer = new VBox(0, cartaContainer, pivoteLabel, pivoteTip);
            pivoteContainer.setAlignment(Pos.TOP_CENTER);

            // 4) Ensamblar todo
            getChildren().setAll(pivoteContainer);
            double pivotHeight = pivoteLabel.getFont().getSize() + 4 + 6;
            setPrefSize(w, h + pivotHeight);
            setMinSize (w, h + pivotHeight);
            setMaxSize (w, h + pivotHeight);

        } else {
            // si es fullSize, simplemente inicialízalos a algo (aunque no los uses)
            pivoteLabel     = null;
            pivoteTip       = new Polygon();     // o null, pero si lo pones null quita el final de getPivotTipLocation
            pivoteContainer = null;
            getChildren().setAll(cartaContainer);
            setPrefSize(w, h);
            setMinSize (w, h);
            setMaxSize (w, h);
        }

        // 3) Agrupamos
        if (pivoteLabel != null) {
            VBox wrapper = new VBox(0, cartaContainer, pivoteLabel);
            wrapper.setAlignment(Pos.CENTER);
            getChildren().setAll(wrapper);
            double pivotHeight = pivoteLabel.getFont().getSize() + 4;
            setPrefSize(w, h + pivotHeight);
            setMinSize (w, h + pivotHeight);
            setMaxSize (w, h + pivotHeight);
        } else {
            // sólo la cartaContainer, sin pivote
            getChildren().setAll(cartaContainer);
            setPrefSize(w, h);
            setMinSize (w, h);
            setMaxSize (w, h);
        }

        setAlignment(Pos.CENTER);

        // 4) Hover effect
        ScaleTransition stIn  = new ScaleTransition(Duration.millis(200), this);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), this);
        stIn.setToX(1.05); stIn.setToY(1.05);
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

    public void setBaseScale(double scale) {
        this.baseScale = scale;
        setScaleX(scale);
        setScaleY(scale);
    }

    public void reset() {
        unlocked = false;
        // vuelve a poner el placeholder original
        cartaContainer.getChildren().setAll(placeholderNode);
    }

    public void resetVisual() {
        // sólo repintamos el placeholder, sin tocar unlocked
        cartaContainer.getChildren().setAll(placeholderNode);
    }

    public boolean isUnlocked()            { return unlocked; }
    public int getIndex()                  { return index; }
    public Position getPosition()          { return position; }
    public StackPane getCartaContainer()   { return cartaContainer; }
    public void setQuimica(int q)          { pivoteLabel.setText(position.name() + " " + q); }
    public Label getPivoteNode()           { return pivoteLabel; }
    public Point2D getPivotTipLocation(Pane linkLayer) {
        // 1) Punto en local: centro-base del triángulo, altura total
        Bounds tipB = pivoteTip.getBoundsInLocal();
        Point2D localTip = new Point2D(tipB.getWidth()/2, tipB.getHeight());

        // 2) A escena (incluye layoutX/Y de todos los ancestros)
        Point2D scenePt = pivoteTip.localToScene(localTip);

        // 3) De escena a coordenadas de linkLayer
        return linkLayer.sceneToLocal(scenePt);
    }
}