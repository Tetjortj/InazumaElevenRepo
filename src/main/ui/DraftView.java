package main.ui;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
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
import javafx.scene.text.FontWeight;
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

    // — NUEVO: barra de resultados —
    private final Label quimicaLabel    = new Label("Química: 0");
    private final Label puntuacionLabel = new Label("Puntuación: 0");
    private final HBox scoreBox         = new HBox(20, quimicaLabel, puntuacionLabel);

    private final List<PlayerCell> benchCells = new ArrayList<>();
    private final List<Card> bench = new ArrayList<>();
    private final VBox benchContainer = new VBox(10);

    private final Button finishButton = new Button("Terminar Draft");

    private final ClickMoveManager moveManager = new ClickMoveManager(this);

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

        // — AÑADIMOS la barra de resultados encima del campo —
        scoreBox.setPadding(new Insets(8));
        scoreBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-background-radius: 8;");

        Button salir = new Button("Salir");
        salir.setFont(Font.font(16));
        salir.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        salir.setOnAction(e -> Platform.exit());

        // --- BANQUILLO: 2 filas (3 + 2) dentro de benchContainer ---
        benchContainer.setPadding(new Insets(10));
        benchContainer.setAlignment(Pos.CENTER);
        // fila 1: 3 celdas
        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER);
        // fila 2: 2 celdas
        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {
            // índice ficticio y posición null para bench
            PlayerCell bc = new PlayerCell(true);
            bc.setPrefSize(120, 170);
            bc.resetVisual();
            moveManager.register(bc, /*onField*/ false);
            benchCells.add(bc);
            if (i < 3) row1.getChildren().add(bc);
            else      row2.getChildren().add(bc);
        }
        benchContainer.getChildren().setAll(row1, row2);

        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(0));

        // 1.a) configura el botón
        finishButton.setDisable(true);
        finishButton.setVisible(false);
        finishButton.setOnAction(e -> showFinalResult());

        VBox.setVgrow(statsPanel, Priority.ALWAYS);

        VBox panelDerecho = new VBox(20,
                salir,
                benchContainer,
                statsPanel,
                scoreBox,
                finishButton
        );
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.prefWidthProperty().bind(widthProperty().multiply(0.25));
        panelDerecho.prefHeightProperty().bind(heightProperty());
        panelDerecho.setStyle("-fx-background-color: #111;");

        this.getChildren().setAll(campoStack, panelDerecho);
        this.setPrefSize(1600, 900);

        // hacemos que todo el campo acepte siempre MOVE para no mostrar “stop”
        jugadorLayer.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
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

            moveManager.register(cell, /*onField*/ true);

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
                double extra = 30;
                Bounds totalA = new BoundingBox(
                        ba.getMinX(),
                        ba.getMinY(),
                        ba.getWidth(),
                        ba.getHeight() + extra
                );

                for (int j = i+1; j < playerCells.size(); j++) {
                    PlayerCell b = playerCells.get(j);
                    Bounds bb = b.getBoundsInParent();
                    Bounds totalB = new BoundingBox(
                            bb.getMinX(),
                            bb.getMinY(),
                            bb.getWidth(),
                            bb.getHeight() + extra
                    );

                    if (totalA.intersects(totalB)) {
                        double dx = ba.getCenterX() - bb.getCenterX();
                        double dy = ba.getCenterY() - bb.getCenterY();
                        if (Math.hypot(dx,dy) < 1e-3) dy = 1;
                        double dist = Math.hypot(dx,dy);
                        double ux = dx/dist*2, uy = dy/dist*2;
                        a.relocate(a.getLayoutX()+ux, a.getLayoutY()+uy);
                        b.relocate(b.getLayoutX()-ux, b.getLayoutY()-uy);
                        moved = true;
                    }
                }
            }
        } while (moved);

        double totalW = columnas*cardW + (columnas+1)*spacingX;
        double totalH = filas   *cardH + (filas   +1)*spacingY;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth() - totalW)/2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight()- totalH)/2);

        updateScores();
        Platform.runLater(this::renderConnections);
    }

    private PlayerCell findCell(int idx) {
        return playerCells.stream()
                .filter(c -> c.getIndex() == idx)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cell " + idx + " no encontrada"));
    }

    /**
     * Mueve o intercambia la carta de sourceCell a targetCell.
     * Si target está vacío, simplemente la mueve.
     * Si target contiene carta, hace swap.
     * En destinos inválidos, devuelve la carta al origen.
     */
    public void performClickMove(
            PlayerCell srcCell, boolean srcOnField,
            PlayerCell dstCell, boolean dstOnField) {

        // 1) Recuperar la carta que estaba en srcCell
        Card moving = srcOnField
                ? jugadoresSeleccionados.get(srcCell.getIndex())
                : bench.get( benchCells.indexOf(srcCell) );

        if (moving == null) {
            // nada que mover
            return;
        }

        // 2) Destino válido → intercambio
        if (dstOnField && srcOnField) {
            // campo ⇄ campo
            int iFrom = srcCell.getIndex();
            int iTo   = dstCell.getIndex();
            Card other = jugadoresSeleccionados.get(iTo);

            // mueve la “moving”
            jugadoresSeleccionados.put(iTo, moving);
            mostrarCartaEnCelda(dstCell, moving);

            if (other != null) {
                // devuelve la “other” al source
                jugadoresSeleccionados.put(iFrom, other);
                mostrarCartaEnCelda(srcCell, other);
            } else {
                jugadoresSeleccionados.remove(iFrom);
                srcCell.resetVisual();
            }

        } else if (!dstOnField && !srcOnField) {
            // banquillo ⇄ banquillo
            int bFrom = benchCells.indexOf(srcCell);
            int bTo   = benchCells.indexOf(dstCell);
            Collections.swap(bench, bFrom, bTo);

            Node tmp = srcCell.getCartaContainer().getChildren().get(0);
            srcCell.getCartaContainer().getChildren().setAll(dstCell.getCartaContainer().getChildren());
            dstCell.getCartaContainer().getChildren().setAll(tmp);

        } else if (srcOnField && !dstOnField) {
            // campo → banquillo
            int iFrom = srcCell.getIndex();
            int bTo   = benchCells.indexOf(dstCell);
            Card oldBench = bench.get(bTo);

            // quita del campo
            jugadoresSeleccionados.remove(iFrom);
            srcCell.resetVisual();

            // pone en bench
            bench.set(bTo, moving);
            dstCell.getCartaContainer().getChildren().setAll(new MiniCardView(moving));

            // devuelve la que había en bench al campo
            if (oldBench != null) {
                jugadoresSeleccionados.put(iFrom, oldBench);
                mostrarCartaEnCelda(srcCell, oldBench);
            }

        } else if (!srcOnField && dstOnField) {
            // banquillo → campo
            int bFrom = benchCells.indexOf(srcCell);
            int iTo   = dstCell.getIndex();
            Card oldField = jugadoresSeleccionados.get(iTo);

            // quita de bench
            bench.set(bFrom, null);
            srcCell.resetVisual();

            // pone en campo
            jugadoresSeleccionados.put(iTo, moving);
            mostrarCartaEnCelda(dstCell, moving);

            // devuelve la que había en campo al bench
            if (oldField != null) {
                bench.set(bFrom, oldField);
                benchCells.get(bFrom).getCartaContainer()
                        .getChildren().setAll(new MiniCardView(oldField));
            }
        }

        // 3) Actualiza stats y conexiones
        updateScores();
        Platform.runLater(this::renderConnections);
    }

    private void updateScores() {
        int quimica    = calcularQuimica(formation, jugadoresSeleccionados);
        int puntuacion = calcularPuntuacion(formation, jugadoresSeleccionados);

        quimicaLabel.   setText("Química: "    + quimica);
        puntuacionLabel.setText("Puntuación: " + puntuacion);

        puntuacionLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        quimicaLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        boolean allPlaced = jugadoresSeleccionados.size() == playerCells.size();
        finishButton.setDisable(!allPlaced);
        finishButton.setVisible(allPlaced);
    }

    private static int calcularQuimica(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
        Map<Integer, List<Integer>> links = formacion.getLinks();
        int enlacesTotales = 0;
        float quimicaActual = 0;

        for (Map.Entry<Integer, List<Integer>> entry : links.entrySet()) {
            int from = entry.getKey();
            for (int to : entry.getValue()) {
                if (from < to
                        && jugadoresSeleccionados.containsKey(from)
                        && jugadoresSeleccionados.containsKey(to)) {
                    enlacesTotales++;
                    Card a = jugadoresSeleccionados.get(from);
                    Card b = jugadoresSeleccionados.get(to);

                    if (a.getTeam() == b.getTeam()
                            || (a.getElement() == b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 1.0;
                    } else if ((a.getElement() == b.getElement() && a.getGrade() != b.getGrade())
                            || (a.getElement() != b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 0.5;
                    } else {
                        quimicaActual += 0.25;
                    }
                }
            }
        }
        return enlacesTotales > 0
                ? Math.round((quimicaActual / enlacesTotales) * 100)
                : 0;
    }

    // Calcula la puntuación media del equipo (como en tu Main)
    private static int calcularPuntuacion(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
        if (jugadoresSeleccionados.isEmpty()) return 0;
        int suma = jugadoresSeleccionados.values().stream()
                .mapToInt(Card::getScore)
                .sum();
        return Math.round((float) suma / jugadoresSeleccionados.size());
    }


    public void seleccionarJugador(PlayerCell cell) {
        List<Card> opts = new ArrayList<>(playerPool.getByPosition(cell.getPosition()));
        opts.removeAll(jugadoresSeleccionados.values());
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        CardSelectorModal sel = new CardSelectorModal(opts, carta -> {
            jugadoresSeleccionados.put(cell.getIndex(), carta);
            cell.desbloquear(carta);
            mostrarCartaEnCelda(cell, carta);

            updateScores();

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

    public void seleccionarDelBench(PlayerCell cell) {
        List<Card> opts = new ArrayList<>(playerPool.getAllPlayers());
        opts.removeAll(jugadoresSeleccionados.values());
        opts.removeAll(bench);
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        CardSelectorModal sel = new CardSelectorModal(opts, carta -> {
            bench.add(carta);
            cell.desbloquear(carta);
            cell.getCartaContainer().getChildren().setAll(new MiniCardView(carta));
        });
        sel.showAndWait();
    }

    private void showFinalResult() {
        int quimica    = calcularQuimica(formation, jugadoresSeleccionados);
        int puntuacion = calcularPuntuacion(formation, jugadoresSeleccionados);
        int total      = quimica + puntuacion;

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(getScene().getWindow());
        alert.setTitle("Draft Finalizado");
        // etiqueta grande y en el medio
        Label msg = new Label("Total final: " + total);
        msg.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        alert.getDialogPane().setContent(msg);

        ButtonType btnSalir = new ButtonType("Salir", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnSalir);

        alert.showAndWait();
        Platform.exit();
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}