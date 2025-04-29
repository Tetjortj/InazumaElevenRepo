package main.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import main.Card;
import main.Formation;
import main.PlayerPlacement;
import main.PlayerPool;

import java.util.*;

public class DraftView extends HBox {

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
        // --- Fondo e imagen del campo
        Image fondo = new Image(getClass().getResource("/images/draft_background2.png").toExternalForm());
        ImageView backgroundView = new ImageView(fondo);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(widthProperty().multiply(0.7)); // 70% para el campo
        backgroundView.fitHeightProperty().bind(heightProperty());

        // --- Campo
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

        StackPane campoStack = new StackPane(backgroundView, fieldGrid);
        campoStack.setPadding(new Insets(20));
        StackPane.setAlignment(fieldGrid, Pos.CENTER);

        VBox campoWrapper = new VBox(campoStack);
        campoWrapper.setPrefWidth(0); // lo fijamos por bindings luego
        VBox.setVgrow(campoStack, Priority.ALWAYS);
        campoWrapper.prefWidthProperty().bind(widthProperty().multiply(0.7));
        campoWrapper.prefHeightProperty().bind(heightProperty());

        Button salirButton = new Button("Salir");
        salirButton.setFont(Font.font(16));
        salirButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        salirButton.setOnAction(e -> Platform.exit());

        // --- Lateral derecho: stats y banquillo
        VBox panelDerecho = new VBox(20, salirButton, statsPanel, banquilloBox);
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.setPrefWidth(400);
        panelDerecho.setStyle("-fx-background-color: #111;"); // fondo negro temporal

        VBox.setVgrow(statsPanel, Priority.ALWAYS);
        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));

        // --- Ensamblar vista
        this.getChildren().addAll(campoWrapper, panelDerecho);
        this.setPrefSize(1600, 900); // tamaño base de referencia
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