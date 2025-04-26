package main.ui;

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

    public PlayerCell(int index, Position position) {
        this.index = index;
        this.position = position;

        Rectangle fondo = new Rectangle(80, 80);
        fondo.setFill(Color.GRAY);
        fondo.setStroke(Color.BLACK);

        label = new Label(position.name() + "\n[" + index + "]");
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(fondo, label);
    }

    public void desbloquear(Card card) {
        this.unlocked = true;
        this.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        label.setText(card.getName() + "\n" + card.getTeam());
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