package main.ui;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
    private final Label label;
    private final Rectangle fondo;

    // Tamaño fijo de celda (coincide con carta escalada)
    private static final double CELL_WIDTH = 125;
    private static final double CELL_HEIGHT = 175;

    public PlayerCell(int index, Position position) {
        this.index = index;
        this.position = position;

        fondo = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
        fondo.setFill(Color.GRAY);
        fondo.setStroke(Color.BLACK);

        label = new Label(position.name() + "\n[" + index + "]");
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(fondo, label);

        // Fijar el tamaño de la celda para mantener consistencia
        this.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
        this.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        this.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
    }

    public void desbloquear(Card card) {
        this.unlocked = true;

        // Eliminar fondo y label al colocar carta
        this.getChildren().clear();
        this.setStyle(null);

        // ⚠️ OPCIONAL: si quieres que siga ocupando el mismo espacio exactamente, puedes mantener el tamaño fijo.
        // Si no, puedes usar computed_size como antes.
        this.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
        this.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        this.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
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
}