package main.ui;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    private final int index;
    private final Position position;
    private boolean unlocked = false;

    private final StackPane cartaContainer = new StackPane();
    private final Label pivote;

    public static final double CELL_WIDTH = 90;
    public static final double CELL_HEIGHT = 130;

    public PlayerCell(int index, Position position) {
        this.index = index;
        this.position = position;

        // 1) Fondo gris redondeado (placeholder)
        Rectangle fondo = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
        fondo.setFill(Color.GRAY);
        fondo.setArcWidth(20);
        fondo.setArcHeight(20);
        fondo.setStroke(Color.DARKGRAY);

        // 2) Logo centrado dentro del placeholder
        ImageView logo = new ImageView(new Image(
                getClass().getResource("/images/logo.png").toExternalForm()
        ));
        logo.setFitWidth(CELL_WIDTH * 0.5);
        logo.setPreserveRatio(true);

        StackPane logoWrapper = new StackPane(logo);
        logoWrapper.setAlignment(Pos.CENTER);

        // 3) Carta container: donde luego insertaremos la CardView escalada
        cartaContainer.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
        cartaContainer.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        cartaContainer.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
        cartaContainer.getChildren().add(new StackPane(fondo, logoWrapper));
        cartaContainer.setAlignment(Pos.CENTER);

        // 4) Pivote justo debajo: posición + química (inicial 0)
        pivote = new Label(position.name() + " 0");
        pivote.setFont(javafx.scene.text.Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 12));
        pivote.setTextFill(Color.WHITE);
        pivote.setBackground(new Background(new BackgroundFill(
                Color.rgb(30, 30, 30), new CornerRadii(8), Insets.EMPTY
        )));
        pivote.setPadding(new Insets(2, 6, 2, 6));

        // 5) Agrupamos cartaContainer + pivote en un VBox CENTRADO
        VBox wrapper = new VBox(0, cartaContainer, pivote);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setFillWidth(false);

        // 6) Este StackPane (PlayerCell) centra el wrapper
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(wrapper);

        // 7) Fijamos tamaño total: altura carta + espacio para pivote (~20px)
        this.setPrefSize(CELL_WIDTH, CELL_HEIGHT + 20);
        this.setMinSize(CELL_WIDTH, CELL_HEIGHT + 20);
        this.setMaxSize(CELL_WIDTH, CELL_HEIGHT + 20);
    }

    /**
     * Coloca la carta seleccionada en la celda.
     * Solo limpia el placeholder; el CardView se inyecta desde DraftView
     * vía getCartaContainer().
     */
    public void desbloquear(Card card) {
        this.unlocked = true;
        cartaContainer.getChildren().clear();
    }

    /** Para que DraftView pueda añadir la CardView escalada. */
    public StackPane getCartaContainer() {
        return cartaContainer;
    }

    /** Actualiza el texto del pivote con la química actual. */
    public void setQuimica(int valor) {
        pivote.setText(position.name() + " " + valor);
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public int getIndex() {
        return index;
    }

    public Position getPosition() {
        return position;
    }

    /** Si necesitas acceder al Label del pivote desde fuera. */
    public Label getPivoteLabel() {
        return pivote;
    }
}