package main.ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        // >>> AÑADIMOS ESTO: para que jugadorLayer y linkLayer llenen TODO el campo <<<
        jugadorLayer.prefWidthProperty().bind(campoStack.widthProperty());
        jugadorLayer.prefHeightProperty().bind(campoStack.heightProperty());
        linkLayer.prefWidthProperty().bind(campoStack.widthProperty());
        linkLayer.prefHeightProperty().bind(campoStack.heightProperty());

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

        // 1) Determinar filas/columnas de la formación
        int minFila = formation.getMinFila();
        int maxFila = formation.getPlacements().stream()
                .mapToInt(PlayerPlacement::getFila).max().orElse(minFila);
        int minCol = formation.getMinColumna();
        int maxCol = formation.getPlacements().stream()
                .mapToInt(PlayerPlacement::getColumna).max().orElse(minCol);

        int filas    = maxFila - minFila + 1;
        int columnas = maxCol - minCol + 1;

        // 2) Obtener espacio disponible en el campo
        double W = jugadorLayer.getWidth() * 0.9;
        double H = jugadorLayer.getHeight() * 0.8;
        if (W == 0 || H == 0) {
            // Si aún no está layoutado, reintentar tras el próximo layout
            jugadorLayer.layoutBoundsProperty()
                    .addListener((o, ov, nv) -> renderizarAlineacion());
            return;
        }

        // 3) Parámetros máximos y mínimos
        double maxCardW    = 125;  // ancho máximo de carta
        double maxCardH    = 175;  // alto máximo de carta
        double minSpacingX = 20;   // espacio mínimo horizontal
        double minSpacingY = 20;   // espacio mínimo vertical

        // 4) Ajustar ancho de carta y spacingX
        double cardW   = maxCardW;
        double spacingX = (W - columnas * cardW) / (columnas + 1);
        if (spacingX < minSpacingX) {
            // Reducimos carta hasta que spacingX == minSpacingX
            cardW    = (W - minSpacingX * (columnas + 1)) / columnas;
            spacingX = minSpacingX;
        }

        // 5) Ajustar alto de carta y spacingY
        double cardH   = maxCardH;
        double spacingY = (H - filas * cardH) / (filas + 1);
        if (spacingY < minSpacingY) {
            cardH    = (H - minSpacingY * (filas + 1)) / filas;
            spacingY = minSpacingY;
        }

        // 6) Colocar cada celda
        for (PlayerPlacement p : formation.getPlacements()) {
            int idx = formation.getPlacements().indexOf(p);
            int row = p.getFila()    - minFila;
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

            jugadorLayer.getChildren().add(cell);
            playerCells.add(cell);
        }

        // 6.b) Resolver colisiones: empuja ligeramente celdas que se solapen
        boolean moved;
        do {
            moved = false;
            for (int i = 0; i < playerCells.size(); i++) {
                PlayerCell a = playerCells.get(i);
                Bounds ba = a.getBoundsInParent();
                for (int j = i + 1; j < playerCells.size(); j++) {
                    PlayerCell b = playerCells.get(j);
                    Bounds bb = b.getBoundsInParent();
                    if (ba.intersects(bb)) {
                        // Vector de separación A←B
                        double dx = ba.getCenterX() - bb.getCenterX();
                        double dy = ba.getCenterY() - bb.getCenterY();
                        if (Math.hypot(dx, dy) < 1e-3) {
                            // Centros casi idénticos: empuja en Y
                            dy = 1;
                        }
                        double dist = Math.hypot(dx, dy);
                        // Normalizar y aplicar un pequeño desplazamiento (2px)
                        double ux = dx / dist * 2;
                        double uy = dy / dist * 2;

                        a.relocate(a.getLayoutX() + ux, a.getLayoutY() + uy);
                        b.relocate(b.getLayoutX() - ux, b.getLayoutY() - uy);

                        // Recalcular bounds para próximas iteraciones
                        ba = a.getBoundsInParent();
                        bb = b.getBoundsInParent();
                        moved = true;
                    }
                }
            }
        } while (moved);

        // 7) Centrar todo el layer dentro del pane
        double totalW = columnas * cardW + (columnas + 1) * spacingX;
        double totalH = filas    * cardH + (filas    + 1) * spacingY;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth()  - totalW) / 2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight() - totalH) / 2);

        // 8) Dibujar conexiones
        Platform.runLater(this::renderConnections);
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