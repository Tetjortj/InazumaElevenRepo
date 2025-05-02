package main.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    Pane linkLayer = new Pane();
    private final List<PlayerCell> playerCells = new ArrayList<>();

    public DraftView(Formation formation, PlayerPool playerPool, StatsPanel statsPanel) {
        this.formation     = formation;
        this.playerPool    = playerPool;
        this.statsPanel    = statsPanel;
        inicializarVista();
    }

    private void inicializarVista() {
        // --- Contenedor del campo: 75% de ancho, 100% de alto ---
        StackPane campoStack = new StackPane(linkLayer, jugadorLayer);
        campoStack.setPadding(Insets.EMPTY);
        campoStack.prefWidthProperty().bind(widthProperty().multiply(0.75));
        campoStack.prefHeightProperty().bind(heightProperty());

        // --- Le ponemos un BackgroundImage con COVER para que escale+recorte ---
        Image campoImg = new Image(
                getClass().getResource("/images/draft_background_43.png").toExternalForm()
        );
        BackgroundSize bgSize = new BackgroundSize(
                100, 100,    // 100% ancho y alto
                true, true,  // unidades en %
                true, true   // cover + center
        );
        BackgroundImage bgImage = new BackgroundImage(
                campoImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );
        campoStack.setBackground(new Background(bgImage));

        // --- Panel derecho: 25% de ancho, 100% de alto ---
        Button salir = new Button("Salir");
        salir.setFont(Font.font(16));
        salir.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        salir.setOnAction(e -> Platform.exit());

        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));
        VBox.setVgrow(statsPanel, Priority.ALWAYS);

        VBox panelDerecho = new VBox(20, salir, statsPanel, banquilloBox);
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.prefWidthProperty().bind(widthProperty().multiply(0.25));
        panelDerecho.prefHeightProperty().bind(heightProperty());
        panelDerecho.setStyle("-fx-background-color: #111;");

        // --- Ensamblamos el HBox principal ---
        this.getChildren().setAll(campoStack, panelDerecho);
        this.setPrefSize(1600, 900);

        // Una vez hecho el layout, dibujamos las celdas
        Platform.runLater(this::renderizarAlineacion);
    }

    private void renderizarAlineacion() {
        jugadorLayer.getChildren().clear();
        playerCells.clear();

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
            playerCells.add(cell);
        }

        // Centrar todo el layer
        double totalW = columnas * cellW + (columnas+1)*sx;
        double totalH = filas    * cellH + (filas   +1)*sy;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth()  - totalW)/2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight() - totalH)/2);

        // Finalmente, pintamos las conexiones
        renderConnections();
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

    private void renderConnections() {
        linkLayer.getChildren().clear();
        Map<Integer, List<Integer>> links = formation.getLinks();

        // Si no hay celdas aún, salimos
        if (playerCells.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, List<Integer>> e : links.entrySet()) {
            int fromIdx = e.getKey();
            // Validamos que el índice exista
            if (fromIdx < 0 || fromIdx >= playerCells.size()) continue;

            PlayerCell from = playerCells.get(fromIdx);
            for (Integer toIdx : e.getValue()) {
                // Validamos también el índice destino
                if (toIdx < 0 || toIdx >= playerCells.size()) continue;

                PlayerCell to = playerCells.get(toIdx);
                Line l = new Line();
                // Salen del centro X y borde inferior de cada carta
                l.startXProperty().bind(from.layoutXProperty().add(from.getWidth() / 2));
                l.startYProperty().bind(from.layoutYProperty().add(from.getHeight()));
                l.endXProperty().bind(to.layoutXProperty().add(to.getWidth() / 2));
                l.endYProperty().bind(to.layoutYProperty().add(to.getHeight()));
                l.setStroke(Color.LIGHTGRAY);
                l.setStrokeWidth(2);
                linkLayer.getChildren().add(l);
            }
        }
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}