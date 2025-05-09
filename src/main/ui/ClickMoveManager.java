package main.ui;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Gestiona el “primer clic” (origen) y el “segundo clic” (destino)
 * para mover/intercambiar cartas entre PlayerCells.
 */
public class ClickMoveManager {
    private final DraftView draftView;
    private PlayerCell sourceCell;
    private boolean sourceOnField;

    // usamos un borde dorado y un sombreado
    private static final Border SELECTED_BORDER = new Border(new BorderStroke(
            Color.GOLD, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(4)
    ));
    private static final DropShadow SELECTED_SHADOW = new DropShadow(15, Color.GOLD);

    public ClickMoveManager(DraftView draftView) {
        this.draftView = draftView;
    }

    /**
     * Registra el handler de click para cada PlayerCell.
     * @param cell     la celda
     * @param onField  true si está en el campo, false si es banquillo
     */
    public void register(PlayerCell cell, boolean onField) {
        cell.setOnMouseClicked(evt -> {
            // 0) Si no hay carta pero ya tenías una source marcada, avisamos en lugar de abrir selector
            if (!cell.isUnlocked() && sourceCell != null) {
                // Creamos un Label tipo toast
                Label toast = new Label("Tienes una carta seleccionada");
                toast.setStyle(
                        "-fx-background-color: rgba(0,0,0,0.75);" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 8px 16px;" +
                                "-fx-background-radius: 4px;" +
                                "-fx-font-size: 18px;"        // <-- aquí pones el tamaño que quieras
                );
                // Asumimos que el draftView está envuelto en un StackPane
                StackPane root = (StackPane) draftView.getChildren().get(0);
                root.getChildren().add(toast);
                StackPane.setAlignment(toast, Pos.CENTER);

                // Lo quitamos tras 2 segundos
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(ev -> root.getChildren().remove(toast));
                delay.play();

                return;
            }

            // Si la celda NO está desbloqueada (sin carta), –abrir selector–
            if (!cell.isUnlocked()) {
                if (onField)      draftView.seleccionarJugador(cell);
                else              draftView.seleccionarDelBench(cell);
                return;
            }

            // Si ya hay una source pendiente, tratamos como “click destino”
            if (sourceCell != null) {
                // Segundo clic: movemos/intercambiamos
                draftView.performClickMove(
                        sourceCell, sourceOnField,
                        cell,        onField
                );
                clearSelection();
            } else {
                // Primer clic: marcamos fuente y resaltamos
                sourceCell      = cell;
                sourceOnField   = onField;
                cell.getCartaContainer().setBorder(SELECTED_BORDER);
                cell.getCartaContainer().setEffect(SELECTED_SHADOW);
            }
        });
    }

    private void clearSelection() {
        if (sourceCell != null) {
            sourceCell.getCartaContainer().setBorder(null);
            sourceCell.getCartaContainer().setEffect(null);
            sourceCell = null;
        }
    }
}