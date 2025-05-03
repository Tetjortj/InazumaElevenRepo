package main.ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
        StackPane campoStack = new StackPane(linkLayer, jugadorLayer);
        campoStack.setPadding(Insets.EMPTY);
        campoStack.prefWidthProperty().bind(widthProperty().multiply(0.75));
        campoStack.prefHeightProperty().bind(heightProperty());

        jugadorLayer.prefWidthProperty().bind(campoStack.widthProperty());
        jugadorLayer.prefHeightProperty().bind(campoStack.heightProperty());
        linkLayer.prefWidthProperty().bind(campoStack.widthProperty());
        linkLayer.prefHeightProperty().bind(campoStack.heightProperty());

        Image campoImg = new Image(
                getClass().getResource("/images/draft_background_43.png").toExternalForm()
        );
        BackgroundSize bgSize = new BackgroundSize(100,100,true,true,true,true);
        BackgroundImage bgImage = new BackgroundImage(
                campoImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bgSize
        );
        campoStack.setBackground(new Background(bgImage));

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

        this.getChildren().setAll(campoStack, panelDerecho);
        this.setPrefSize(1600, 900);

        jugadorLayer.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                // aceptamos siempre MOVE en todo el área, así nunca sale el cursor de "stop"
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

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

        double W = jugadorLayer.getWidth() * 0.9;
        double H = jugadorLayer.getHeight() * 0.8;
        if (W == 0 || H == 0) {
            jugadorLayer.layoutBoundsProperty()
                    .addListener((o, ov, nv) -> renderizarAlineacion());
            return;
        }

        double maxCardW = 125, maxCardH = 175, minSpacingX = 20, minSpacingY = 20;
        double cardW = maxCardW;
        double spacingX = (W - columnas * cardW) / (columnas + 1);
        if (spacingX < minSpacingX) {
            cardW = (W - minSpacingX * (columnas + 1)) / columnas;
            spacingX = minSpacingX;
        }
        double cardH = maxCardH;
        double spacingY = (H - filas * cardH) / (filas + 1);
        if (spacingY < minSpacingY) {
            cardH = (H - minSpacingY * (filas + 1)) / filas;
            spacingY = minSpacingY;
        }

        for (PlayerPlacement p : formation.getPlacements()) {
            int idx = formation.getPlacements().indexOf(p);
            int row = p.getFila() - minFila;
            int col = p.getColumna() - minCol;

            PlayerCell cell = new PlayerCell(idx, p.getPosition());
            cell.setPrefSize(cardW, cardH);
            cell.setMinSize(cardW, cardH);
            cell.setMaxSize(cardW, cardH);
            cell.setBaseScale(1);

            double x = spacingX + col * (cardW + spacingX);
            double y = spacingY + row * (cardH + spacingY);
            cell.relocate(x, y);

            cell.setOnMouseClicked(evt -> {
                if (!cell.isUnlocked()) seleccionarJugador(cell);
            });

            cell.setOnDragDetected(e -> {
                if (!cell.isUnlocked()) return;
                // 1) Snapshot de la vista actual (mini-carta o placeholder)
                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                WritableImage snapshot = cell.getCartaContainer().snapshot(sp, null);

                // 2) Iniciamos el dragboard
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(cell.getIndex()));
                db.setContent(content);
                db.setDragView(snapshot,
                        snapshot.getWidth()  / 2,
                        snapshot.getHeight() / 2
                );

                // 3) Dejamos el placeholder “vacío” en la celda
                cell.resetVisual();

                cell.getScene().setCursor(Cursor.DEFAULT);

                e.consume();
            });


            cell.setOnDragOver(e -> {
                if (e.getDragboard().hasString()) {
                    // aceptamos siempre el MOVE para que el cursor nunca sea el "stop"
                    e.acceptTransferModes(TransferMode.MOVE);
                }
                e.consume();
            });

            cell.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                boolean success = false;
                if (db.hasString() && cell.isUnlocked()) {
                    int fromIdx = Integer.parseInt(db.getString());
                    int toIdx   = cell.getIndex();

                    Card a = jugadoresSeleccionados.get(fromIdx);
                    Card b = jugadoresSeleccionados.get(toIdx);

                    // 1) ponemos 'a' en destino
                    jugadoresSeleccionados.put(toIdx, a);
                    mostrarCartaEnCelda(findCell(toIdx), a);

                    // 2) swap o move
                    if (b != null) {
                        jugadoresSeleccionados.put(fromIdx, b);
                        mostrarCartaEnCelda(findCell(fromIdx), b);
                    } else {
                        jugadoresSeleccionados.remove(fromIdx);
                        findCell(fromIdx).reset();
                    }

                    statsPanel.actualizarStats(formation, jugadoresSeleccionados);
                    Platform.runLater(this::renderConnections);

                    success = true;
                }
                e.setDropCompleted(success);
                e.consume();
            });

            cell.setOnDragDone(e -> {
                if (!e.isDropCompleted()) {
                    Card original = jugadoresSeleccionados.get(cell.getIndex());
                    if (original != null) {
                        mostrarCartaEnCelda(cell, original);
                    } else {
                        cell.resetVisual();
                    }
                }
                cell.getScene().setCursor(Cursor.DEFAULT);
                e.consume();
            });
            // --- FIN DRAG & DROP ---

            jugadorLayer.getChildren().add(cell);
            playerCells.add(cell);
        }

        // resolver colisiones (idem)
        boolean moved;
        do {
            moved = false;
            for (int i = 0; i < playerCells.size(); i++) {
                PlayerCell a = playerCells.get(i);
                Bounds ba = a.getBoundsInParent();
                for (int j = i+1; j < playerCells.size(); j++) {
                    PlayerCell b = playerCells.get(j);
                    Bounds bb = b.getBoundsInParent();
                    if (ba.intersects(bb)) {
                        double dx = ba.getCenterX() - bb.getCenterX();
                        double dy = ba.getCenterY() - bb.getCenterY();
                        if (Math.hypot(dx,dy) < 1e-3) dy = 1;
                        double dist = Math.hypot(dx,dy);
                        double ux = dx/dist*2, uy = dy/dist*2;
                        a.relocate(a.getLayoutX()+ux, a.getLayoutY()+uy);
                        b.relocate(b.getLayoutX()-ux, b.getLayoutY()-uy);
                        moved = true;
                        ba = a.getBoundsInParent();
                        bb = b.getBoundsInParent();
                    }
                }
            }
        } while (moved);

        double totalW = columnas*cardW + (columnas+1)*spacingX;
        double totalH = filas   *cardH + (filas   +1)*spacingY;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth() - totalW)/2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight()- totalH)/2);

        Platform.runLater(this::renderConnections);
    }

    private PlayerCell findCell(int idx) {
        return playerCells.stream()
                .filter(c -> c.getIndex() == idx)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cell " + idx + " no encontrada"));
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
            Platform.runLater(this::renderConnections);
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
        if (playerCells.isEmpty()) return;

        for (var entry : links.entrySet()) {
            int fromIdx = entry.getKey();
            if (fromIdx < 0 || fromIdx >= playerCells.size()) continue;
            PlayerCell fromCell = playerCells.get(fromIdx);
            Point2D fromPoint = fromCell.getPivotTipLocation(linkLayer);

            for (Integer toIdx : entry.getValue()) {
                if (toIdx < 0 || toIdx >= playerCells.size()) continue;
                PlayerCell toCell = playerCells.get(toIdx);
                Point2D toPoint = toCell.getPivotTipLocation(linkLayer);

                // 1) Crear línea base (fina y gris) para todos los enlaces
                Line l = new Line(
                        fromPoint.getX(), fromPoint.getY(),
                        toPoint.getX(),   toPoint.getY()
                );
                l.setStrokeWidth(6);
                l.setStroke(Color.DARKSLATEGRAY);
                linkLayer.getChildren().add(l);

                // 2) Si ambos extremos tienen carta seleccionada, recalcular química
                if (jugadoresSeleccionados.containsKey(fromIdx) &&
                        jugadoresSeleccionados.containsKey(toIdx)) {

                    Card a = jugadoresSeleccionados.get(fromIdx);
                    Card b = jugadoresSeleccionados.get(toIdx);

                    double score;
                    if (a.getTeam() == b.getTeam() ||
                            (a.getElement() == b.getElement() && a.getGrade() == b.getGrade())) {
                        score = 1.0;
                    } else if ((a.getElement() == b.getElement() && a.getGrade() != b.getGrade()) ||
                            (a.getElement() != b.getElement() && a.getGrade() == b.getGrade())) {
                        score = 0.5;
                    } else {
                        score = 0.25;
                    }

                    // 3) Ajustar grosor y color según score
                    l.setStrokeWidth(6);
                    if (score == 1.0) {
                        l.setStroke(Color.CHARTREUSE);
                    } else if (score == 0.5) {
                        l.setStroke(Color.GOLD);
                    } else {
                        l.setStroke(Color.FIREBRICK);
                    }
                }
            }
        }
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}