package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import main.Card;
import main.Formation;
import main.PlayerPlacement;
import main.PlayerPool;

import java.util.*;

public class DraftView extends BorderPane {

    private final GridPane fieldGrid = new GridPane();
    private final List<PlayerCell> playerCells = new ArrayList<>();
    private final Map<Integer, Card> jugadoresSeleccionados = new HashMap<>();
    private final PlayerPool playerPool;
    private final Formation formation;
    private final StatsPanel statsPanel;
    private final HBox banquilloBox = new HBox(10);

    public DraftView(Formation formation, PlayerPool playerPool, StatsPanel statsPanel) {
        this.formation = formation;
        this.playerPool = playerPool;
        this.statsPanel = statsPanel;

        inicializarVista();
    }

    private void inicializarVista() {
        // Fondo general
        try {
            Image fondo = new Image(getClass().getResource("/images/draft_background.png").toExternalForm());
            BackgroundImage backgroundImage = new BackgroundImage(
                    fondo,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true)
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            this.setStyle("-fx-background-color: green;");
        }

        // Campo de juego
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

        StackPane centro = new StackPane(fieldGrid);
        centro.setPadding(new Insets(20));
        this.setCenter(centro);

        // Panel lateral
        VBox derecha = new VBox(statsPanel);
        derecha.setPadding(new Insets(20));
        derecha.setAlignment(Pos.TOP_CENTER);
        derecha.setPrefWidth(300);
        this.setRight(derecha);

        // Banquillo
        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));
        this.setBottom(banquilloBox);
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
        cell.getChildren().clear();
        CardView miniCard = new CardView(cardSeleccionada);
        miniCard.setScaleX(0.5);
        miniCard.setScaleY(0.5);
        cell.getChildren().add(miniCard);
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}