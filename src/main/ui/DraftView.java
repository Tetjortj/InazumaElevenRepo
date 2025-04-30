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

    private final Pane jugadorLayer = new Pane();
    private final Map<Integer, Card> jugadoresSeleccionados = new HashMap<>();
    private final PlayerPool playerPool;
    private final Formation formation;
    private final StatsPanel statsPanel;
    private final HBox banquilloBox = new HBox(10);

    public DraftView(Formation formation, PlayerPool playerPool, StatsPanel statsPanel) {
        this.formation     = formation;
        this.playerPool    = playerPool;
        this.statsPanel    = statsPanel;
        inicializarVista();
    }

    private void inicializarVista() {
        // --- Fondo ---
        ImageView backgroundView = new ImageView(new Image(
                getClass().getResource("/images/draft_background2.png").toExternalForm()
        ));
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(widthProperty().multiply(0.7));
        backgroundView.fitHeightProperty().bind(heightProperty());

        StackPane campoStack = new StackPane(backgroundView, jugadorLayer);
        campoStack.setPadding(new Insets(20));
        StackPane.setAlignment(jugadorLayer, Pos.CENTER);

        VBox campoWrapper = new VBox(campoStack);
        campoWrapper.prefWidthProperty().bind(widthProperty().multiply(0.7));
        campoWrapper.prefHeightProperty().bind(heightProperty());

        // --- Panel derecho ---
        Button salir = new Button("Salir");
        salir.setFont(Font.font(16));
        salir.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        salir.setOnAction(e -> Platform.exit());

        VBox panelDerecho = new VBox(20, salir, statsPanel, banquilloBox);
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.setPrefWidth(400);
        panelDerecho.setStyle("-fx-background-color: #111;");

        VBox.setVgrow(statsPanel, Priority.ALWAYS);
        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));

        this.getChildren().addAll(campoWrapper, panelDerecho);
        this.setPrefSize(1600, 900);

        Platform.runLater(this::renderizarAlineacion);
    }

    private void renderizarAlineacion() {
        jugadorLayer.getChildren().clear();

        int minFila = formation.getMinFila();
        int maxFila = formation.getPlacements().stream()
                .mapToInt(PlayerPlacement::getFila).max().orElse(minFila);
        int minCol = formation.getMinColumna();
        int maxCol = formation.getPlacements().stream()
                .mapToInt(PlayerPlacement::getColumna).max().orElse(minCol);

        int filas    = maxFila - minFila + 1;
        int columnas = maxCol - minCol + 1;

        double W = jugadorLayer.getWidth();
        double H = jugadorLayer.getHeight() * 0.8;
        if (W == 0 || H == 0) {
            jugadorLayer.layoutBoundsProperty()
                    .addListener((o,ov,nv)-> renderizarAlineacion());
            return;
        }

        // Espaciado para que las celdas encajen exactas
        double cellW = PlayerCell.CELL_WIDTH;
        double cellH = PlayerCell.CELL_HEIGHT;
        double sx = (W - columnas * cellW) / (columnas + 1);
        double sy = (H - filas    * cellH) / (filas    + 1);

        for (PlayerPlacement p : formation.getPlacements()) {
            int idx = formation.getPlacements().indexOf(p);
            int row = p.getFila()   - minFila;
            int col = p.getColumna()- minCol;

            PlayerCell cell = new PlayerCell(idx, p.getPosition());
            double x = sx + col * (cellW + sx);
            double y = sy + row * (cellH + sy);
            cell.relocate(x,y);

            cell.setOnMouseClicked(evt -> {
                if (!cell.isUnlocked()) seleccionarJugador(cell);
            });

            jugadorLayer.getChildren().add(cell);
        }

        // Centrar todo el layer
        double totalW = columnas * cellW + (columnas+1)*sx;
        double totalH = filas    * cellH + (filas   +1)*sy;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth()  - totalW)/2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight() - totalH)/2);
    }

    private void seleccionarJugador(PlayerCell cell) {
        List<Card> opts = new ArrayList<>(playerPool.getByPosition(cell.getPosition()));
        opts.removeAll(jugadoresSeleccionados.values());
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        CardSelectorModal sel = new CardSelectorModal(opts, carta -> {
            jugadoresSeleccionados.put(cell.getIndex(), carta);
            cell.desbloquear(carta);
            mostrarCartaEnCelda(cell, carta);
            statsPanel.actualizarStats(formation, jugadoresSeleccionados);
        });
        sel.showAndWait();
    }

    private void mostrarCartaEnCelda(PlayerCell cell, Card carta) {
        MiniCardView mini = new MiniCardView(carta);
        cell.getCartaContainer().getChildren().setAll(mini);
        StackPane.setAlignment(mini, Pos.CENTER);
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}