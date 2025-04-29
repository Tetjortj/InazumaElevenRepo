package main.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import main.Card;
import main.Formation;
import main.PlayerPlacement;

import java.util.*;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import main.PlayerPool;

import java.util.*;

public class DraftField extends StackPane {
    private final GridPane fieldGrid = new GridPane();
    private final Formation formation;
    private final Map<Integer, Card> jugadoresSeleccionados = new HashMap<>();
    private final List<PlayerCell> playerCells = new ArrayList<>();
    private final PlayerPool playerPool;
    private final StatsPanel statsPanel;

    public DraftField(Formation formation, PlayerPool playerPool, StatsPanel statsPanel) {
        this.formation = formation;
        this.playerPool = playerPool;
        this.statsPanel = statsPanel;
        crearCampo();
    }

    private void crearCampo() {
        fieldGrid.setHgap(20);
        fieldGrid.setVgap(20);
        fieldGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < formation.getPlacements().size(); i++) {
            PlayerPlacement placement = formation.getPlacements().get(i);

            PlayerCell cell = new PlayerCell(i, placement.getPosition());
            playerCells.add(cell);

            fieldGrid.add(cell, placement.getColumna(), placement.getFila());

            cell.setOnMouseClicked(event -> {
                if (!cell.isUnlocked()) {
                    seleccionarJugador(cell);
                }
            });
        }

        this.getChildren().add(fieldGrid);

        Image fondo = new Image(getClass().getResource("/images/draft_background.png").toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(
                fondo,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100, true, true, false, false // 100% del ancho y alto
                )
        );
        this.setBackground(new Background(backgroundImage));
    }

    private void seleccionarJugador(PlayerCell cell) {
        List<Card> opciones = new ArrayList<>(playerPool.getByPosition(cell.getPosition()));
        Collections.shuffle(opciones);
        opciones = opciones.stream().limit(5).toList();

        CardSelectorModal selector = new CardSelectorModal(opciones, cardSeleccionada -> {
            jugadoresSeleccionados.put(cell.getIndex(), cardSeleccionada);
            cell.desbloquear(cardSeleccionada);
            mostrarCartaEnCelda(cell, cardSeleccionada);
            statsPanel.actualizarStats(formation, jugadoresSeleccionados);
        });

        selector.showAndWait();
    }

    private void mostrarCartaEnCelda(PlayerCell cell, Card cardSeleccionada) {
        cell.getChildren().clear(); // Limpiamos la celda
        CardView miniCard = new CardView(cardSeleccionada);
        // Escalado para campo
        miniCard.setScaleX(0.5);
        miniCard.setScaleY(0.5);
        cell.getChildren().add(miniCard);
    }
}