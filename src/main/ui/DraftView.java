package main.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.*;
import main.ui.screens.TitleScreen;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DraftView extends HBox {

    private final Pane jugadorLayer = new Pane();
    private final Map<Integer, Card> jugadoresSeleccionados = new HashMap<>();
    private final PlayerPool playerPool;
    private final Formation formation;
    private final StatsPanel statsPanel;
    private final HBox banquilloBox = new HBox(10);
    Pane linkLayer = new Pane();
    private final List<PlayerCell> playerCells = new ArrayList<>();
    private final Pane benchLayer = new Pane();

    // — NUEVO: barra de resultados —
    private final Label quimicaLabel    = new Label("Química: 0");
    private final Label puntuacionLabel = new Label("Puntuación: 0");
    private final HBox scoreBox         = new HBox(20, quimicaLabel, puntuacionLabel);

    private final List<PlayerCell> benchCells = new ArrayList<>();
    private final List<Card> bench = new ArrayList<>();
    private final VBox benchContainer = new VBox(10);

    private final Button finishButton = new Button("Terminar Draft");

    private final ClickMoveManager moveManager = new ClickMoveManager(this);

    private List<Card> lastOpts;
    private Consumer<Card> lastOnSelect, lastOnPeek;
    private int lastIdx;
    private boolean lastOnField;

    // bandera global
    private boolean inPreview;
    // para saber qué carta y en qué idx
    private Card    previewCard;
    private int     previewIdx;

    private boolean initialModalShown = false;

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

            bench.add(null);

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

        benchLayer.prefWidthProperty().bind(benchContainer.widthProperty());
        benchLayer.prefHeightProperty().bind(benchContainer.heightProperty());
        benchLayer.setMouseTransparent(true);

        // 1) metemos benchContainer y benchLayer en un StackPane
        StackPane benchStack = new StackPane(benchContainer, benchLayer);
        benchStack.setAlignment(Pos.CENTER);
        // (guardamos benchLayer ya dimensionado igual que benchContainer,
        //  ver paso 3)

        // 2) y en el panel derecho usamos benchStack en lugar de benchContainer
        VBox panelDerecho = new VBox(20,
                salir,
                benchStack,
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

    private void showInitialHighRatedSelector() {
        // 1) Filtramos todas las cartas >80 de media
        List<Card> opts = playerPool.getAllPlayers().stream()
                .filter(c -> c.getScore() > 80)
                .collect(Collectors.toList());
        // 2) Barajamos y nos quedamos con 5
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        lastOnSelect = carta -> {
            for (PlayerCell pc : playerCells) {
                if (pc.getPosition().equals(carta.getPosition())
                        && !jugadoresSeleccionados.containsKey(pc.getIndex())) {
                    jugadoresSeleccionados.put(pc.getIndex(), carta);
                    pc.desbloquear(carta);
                    mostrarCartaEnCelda(pc, carta);
                    updateScores();
                    statsPanel.actualizarStats(formation, jugadoresSeleccionados);
                    Platform.runLater(this::renderConnections);
                    break;
                }
            }
        };
        lastOnPeek = carta -> { /* no-op en este paso */ };

        CardSelectorModal sel = new CardSelectorModal(opts, lastOnSelect, lastOnPeek, true, true);
        sel.showAndWait();
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
        for (PlayerCell cell : playerCells) {
            double chem = calculatePlayerChemistry(cell.getIndex());
            cell.updateChemistry(chem);
        }

        double totalChem = playerCells.stream()
                .mapToDouble(cell -> calculatePlayerChemistry(cell.getIndex()))
                .sum();

        int totalChemInt = (int) Math.round(totalChem);
        quimicaLabel.setText("Química: " + totalChemInt);

        int puntuacion = calcularPuntuacion(formation, jugadoresSeleccionados);
        puntuacionLabel.setText("Puntuación: " + puntuacion);

        puntuacionLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        quimicaLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        // 1) ¿Campo completo?
        boolean allField = jugadoresSeleccionados.size() == playerCells.size();

        // 2) ¿Banquillo completo?
        long benchCount = bench.stream().filter(Objects::nonNull).count();
        boolean allBench = benchCount == benchCells.size();

        // 3) Activar sólo si ambos están completos
        boolean ready = allField && allBench;
        finishButton.setDisable(!ready);
        finishButton.setVisible(ready);
    }

    /**
     * Devuelve un valor de 0 a 10 para el jugador en posición idx.
     */
    private double calculatePlayerChemistry(int idx) {
        Card card = jugadoresSeleccionados.get(idx);
        if (card == null) return 0;

        double score = 0;
        // --- 4 puntos por posición correcta ---
        PlayerPlacement placement = formation.getPlacements().get(idx);
        if (card.getPosition().equals(placement.getPosition())) {
            score += 4;
        }

        // --- 6 puntos por enlaces ---
        List<Integer> links = formation.getLinks().get(idx);
        if (links != null && !links.isEmpty()) {
            int maxLinks = links.size();

            // definimos cuántos puntos vale cada tipo de enlace
            int ptsStrong, ptsMedium;
            switch (maxLinks) {
                case 1:
                    ptsStrong = 6;  ptsMedium = 4;
                    break;
                case 2:
                    ptsStrong = 4;  ptsMedium = 3;
                    break;
                case 3:
                    ptsStrong = 4;  ptsMedium = 2;
                    break;
                case 4:
                    ptsStrong = 3;  ptsMedium = 2;
                    break;
                case 5:
                    ptsStrong = 3;  ptsMedium = 1;
                    break;
                case 6:
                    ptsStrong = 2;  ptsMedium = 1;
                    break;
                default:
                    // por si en el futuro hubiera más de 6 enlaces
                    ptsStrong = 1;  ptsMedium = 1;
            }

            // recorremos cada enlace y sumamos según su fuerza
            int linkScore = 0;
            for (int to : links) {
                Card other = jugadoresSeleccionados.get(to);
                if (other == null) continue;

                if (card.getTeam() == other.getTeam()
                        || (card.getElement() == other.getElement() && card.getGrade() == other.getGrade())) {
                    // enlace fuerte (verde)
                    linkScore += ptsStrong;
                } else if (card.getElement() == other.getElement()
                        || card.getGrade() == other.getGrade()) {
                    // enlace medio (amarillo)
                    linkScore += ptsMedium;
                }
                // los “rojos” no suman nada
            }

            score += linkScore;
        }

        return Math.min(score, 10);
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
        // 1) construyo el set de ya usados: campo + banquillo
        Set<Card> usados = new HashSet<>(jugadoresSeleccionados.values());
        usados.addAll(
                bench.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        // 2) cojo sólo los de su posición y filtro los usados
        List<Card> opts = playerPool.getByPosition(cell.getPosition()).stream()
                .filter(c -> !usados.contains(c))
                .collect(Collectors.toList());
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        // ——> GUARDAR estado para poder reabrirlo ——>
        lastOpts       = opts;
        lastOnField    = true;
        lastIdx        = cell.getIndex();
        lastOnSelect   = carta -> {
            jugadoresSeleccionados.put(cell.getIndex(), carta);
            cell.desbloquear(carta);
            mostrarCartaEnCelda(cell, carta);
            updateScores();
            statsPanel.actualizarStats(formation, jugadoresSeleccionados);
            Platform.runLater(this::renderConnections);
        };
        lastOnPeek     = carta -> previewCardInField(cell.getIndex(), carta);

        // ——> Crear modal con ambos consumers ——>
        CardSelectorModal sel = new CardSelectorModal(
                opts,
                lastOnSelect,
                lastOnPeek
        );
        sel.showAndWait();
    }

    public void seleccionarDelBench(PlayerCell cell) {
        // 1) construyo el set de ya usados: campo + banquillo
        Set<Card> usados = new HashSet<>(jugadoresSeleccionados.values());
        usados.addAll(
                bench.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        // 2) filtro todas las cartas
        List<Card> opts = playerPool.getAllPlayers().stream()
                .filter(c -> !usados.contains(c))
                .collect(Collectors.toList());
        Collections.shuffle(opts);
        opts = opts.stream().limit(5).toList();

        int benchIdx = benchCells.indexOf(cell);

        // ——> GUARDAR estado ——>
        lastOpts       = opts;
        lastOnField    = false;
        lastIdx        = benchIdx;
        lastOnSelect   = carta -> {
            bench.set(benchIdx, carta);
            cell.desbloquear(carta);
            cell.getCartaContainer().getChildren().setAll(new MiniCardView(carta));
            updateScores();
            Platform.runLater(this::renderConnections);
        };
        lastOnPeek     = carta -> previewCardInBench(benchIdx, carta);

        // ——> Crear modal con ambos consumers ——>
        CardSelectorModal sel = new CardSelectorModal(
                opts,
                lastOnSelect,
                lastOnPeek
        );
        sel.showAndWait();
    }

    public void previewCardInField(int idx, Card carta) {
        // 1) entramos en modo preview
        inPreview     = true;
        // 2) guardamos carta e índice
        previewCard   = carta;
        previewIdx    = idx;

        // 3) pintamos el ghost sobre el campo
        renderPreviewOnLayer(jugadorLayer, idx);
        // 4) bloqueamos toda interacción salvo click derecho
        installPreviewExitHandler();
    }

    public void previewCardInBench(int idx, Card carta) {
        inPreview     = true;
        previewCard   = carta;
        previewIdx    = idx;

        renderPreviewOnLayer(benchLayer, idx);
        installPreviewExitHandler();
    }

    /**
     * Dibuja el “ghost” de la carta previsualizada sobre el layer indicado.
     */
    private void renderPreviewOnLayer(Pane layer, int idx) {
        // 1) Eliminamos cualquier preview anterior
        layer.getChildren().removeIf(n -> "PREVIEW".equals(n.getUserData()));

        if (previewCard == null) return;

        // 2) Creamos el PlayerCell fantasma
        PlayerCell ghost;
        if (layer == jugadorLayer) {
            // en el campo, necesitamos posición
            Position pos = formation.getPlacements().get(idx).getPosition();
            ghost = new PlayerCell(idx, pos);
        } else {
            // en el banquillo, basta con el constructor sin posición
            ghost = new PlayerCell(true);
        }

        // 3) Le ponemos la carta y le damos transparencia
        ghost.getCartaContainer().getChildren().setAll(new MiniCardView(previewCard));
        ghost.setOpacity(0.6);
        ghost.setUserData("PREVIEW");

        // 4) Lo colocamos en coordenadas exactas
        Point2D coords = (layer == jugadorLayer)
                ? calculateCoordsForPlacement(idx)
                : calculateCoordsForBench(idx);
        ghost.relocate(coords.getX(), coords.getY());

        // 5) Añadimos el fantasma
        layer.getChildren().add(ghost);
    }

    /**
     * Mientras inPreview==true:
     *  - consume TODO click izquierdo y muestra alert
     *  - al hacer click derecho, sale de preview y reabre el selector
     */
    private void installPreviewExitHandler() {
        // creamos un campo para poder quitarlo después
        EventHandler<MouseEvent> previewFilter = new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                if (!inPreview) return;

                if (e.getButton() == MouseButton.SECONDARY) {
                    // 1) eliminamos fantasma
                    clearPreviewGhost();
                    inPreview = false;

                    // 2) quitamos este filtro de la Scene
                    getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);

                    // 3) reabrimos selector SIN animación de entrada
                    CardSelectorModal sel = new CardSelectorModal(
                            lastOpts, lastOnSelect, lastOnPeek, false
                    );
                    sel.showAndWait();

                    e.consume();
                } else {
                    // clic izquierdo o cualquier otra cosa:
                    // mostramos alerta y consumimos TODO
                    new Alert(Alert.AlertType.INFORMATION,
                            "⚠️ Estás en modo vista previa\n" +
                                    "   Solo clic derecho sale de este modo."
                    ).showAndWait();
                    e.consume();
                }
            }
        };

        // instalamos Filtro en la Scene (que va a pillar todo lo que se haga)
        getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, previewFilter);
    }

    /**
     * Borra de ambos layers cualquier nodo marcado como PREVIEW.
     */
    private void clearPreviewGhost() {
        jugadorLayer.getChildren().removeIf(n -> "PREVIEW".equals(n.getUserData()));
        benchContainer.getChildren().removeIf(n -> "PREVIEW".equals(n.getUserData()));
    }

    /**
     * Obtiene la esquina superior izquierda de la PlayerCell idx
     * en coordenadas de jugadorLayer.
     */
    private Point2D calculateCoordsForPlacement(int idx) {
        PlayerCell cell = playerCells.get(idx);
        // 1) Sacamos los bounds en escena
        Bounds sceneBounds = cell.localToScene(cell.getBoundsInLocal());
        // 2) Convertimos esa esquina a coordenadas de jugadorLayer
        return jugadorLayer.sceneToLocal(new Point2D(sceneBounds.getMinX(), sceneBounds.getMinY()));
    }

    /**
     * Igual que el anterior, pero para los benchCells dentro de benchContainer.
     */
    private Point2D calculateCoordsForBench(int idx) {
        PlayerCell cell = benchCells.get(idx);
        Bounds sceneBounds = cell.localToScene(cell.getBoundsInLocal());
        return benchContainer.sceneToLocal(new Point2D(sceneBounds.getMinX(), sceneBounds.getMinY()));
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

        if (!initialModalShown) {
            initialModalShown = true;
            // aseguramos que ocurra *tras* este frame
            Platform.runLater(this::showInitialHighRatedSelector);
        }
    }

    private void showFinalResult() {
        double totalChem = playerCells.stream()
                .mapToDouble(cell -> calculatePlayerChemistry(cell.getIndex()))
                .sum();

        int totalChemInt = (int) Math.round(totalChem);
        int puntuacion = calcularPuntuacion(formation, jugadoresSeleccionados);
        int total      = totalChemInt + puntuacion;

        Alert alert = new Alert(Alert.AlertType.NONE);
        Stage stage = (Stage) getScene().getWindow();
        alert.initOwner(stage);
        alert.setTitle("Draft Finalizado");
        // etiqueta grande y en el medio
        Label msg = new Label("Total final: " + total);
        msg.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        alert.getDialogPane().setContent(msg);

        ButtonType btnVolver = new ButtonType("Volver al titulo", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnVolver);

        alert.showAndWait();

        TitleScreen title = new TitleScreen();
        title.show(stage);

        crossFadeToTitle(stage, title.getRoot());
    }

    private void crossFadeToTitle(Stage stage, Parent newRoot) {
        Scene scene    = stage.getScene();
        Parent oldRoot = scene.getRoot();

        // 1) Fade-out del viejo root
        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), oldRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            // 2) Reemplaza directamente la root (sin stack intermedio)
            scene.setRoot(newRoot);

            // 3) Fade-in de la nueva root
            newRoot.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(600), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}