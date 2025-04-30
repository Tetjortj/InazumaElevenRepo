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
        // Fondo del campo
        Image fondo = new Image(getClass().getResource("/images/draft_background2.png").toExternalForm());
        ImageView backgroundView = new ImageView(fondo);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(widthProperty().multiply(0.7));
        backgroundView.fitHeightProperty().bind(heightProperty());

        // Contenedor del campo
        StackPane campoStack = new StackPane(backgroundView, jugadorLayer);
        campoStack.setPadding(new Insets(20));
        StackPane.setAlignment(jugadorLayer, Pos.CENTER);

        VBox campoWrapper = new VBox(campoStack);
        campoWrapper.prefWidthProperty().bind(widthProperty().multiply(0.7));
        campoWrapper.prefHeightProperty().bind(heightProperty());

        // Lateral derecho: stats + banquillo + botón salir
        Button salirButton = new Button("Salir");
        salirButton.setFont(Font.font(16));
        salirButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        salirButton.setOnAction(e -> Platform.exit());

        VBox panelDerecho = new VBox(20, salirButton, statsPanel, banquilloBox);
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.setPrefWidth(400);
        panelDerecho.setStyle("-fx-background-color: #111;");

        VBox.setVgrow(statsPanel, Priority.ALWAYS);
        banquilloBox.setAlignment(Pos.CENTER);
        banquilloBox.setPadding(new Insets(10));

        this.getChildren().addAll(campoWrapper, panelDerecho);
        this.setPrefSize(1600, 900);

        // Renderizar alineacion
        Platform.runLater(() -> renderizarAlineacion());
    }

    private void renderizarAlineacion() {
        jugadorLayer.getChildren().clear();

        int minFila = formation.getMinFila();
        int maxFila = formation.getPlacements().stream().mapToInt(PlayerPlacement::getFila).max().orElse(0);
        int minCol = formation.getMinColumna();
        int maxCol = formation.getPlacements().stream().mapToInt(PlayerPlacement::getColumna).max().orElse(0);

        int filas = maxFila - minFila + 1;
        int columnas = maxCol - minCol + 1;

        double anchoCampo = jugadorLayer.getWidth();
        double altoCampo = jugadorLayer.getHeight() * 0.8;

        if (anchoCampo == 0 || altoCampo == 0) {
            jugadorLayer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> renderizarAlineacion());
            return;
        }

        double spacingX = 20, spacingY = 20;

        double cartaAncho = (anchoCampo - spacingX * (columnas - 1)) / columnas;
        double cartaAlto = (altoCampo - spacingY * (filas - 1)) / filas;

        double cartaFinalWidth = Math.min(125, cartaAncho);
        double cartaFinalHeight = Math.min(175, cartaAlto);

        for (int i = 0; i < formation.getPlacements().size(); i++) {
            PlayerPlacement p = formation.getPlacements().get(i);
            int fila = p.getFila() - minFila;
            int col = p.getColumna() - minCol;

            PlayerCell cell = new PlayerCell(i, p.getPosition());
            playerCells.add(cell);

            cell.setPrefSize(cartaFinalWidth, cartaFinalHeight);
            cell.setMinSize(cartaFinalWidth, cartaFinalHeight);
            cell.setMaxSize(cartaFinalWidth, cartaFinalHeight);

            double x = col * (cartaFinalWidth + spacingX);
            double y = fila * (cartaFinalHeight + spacingY);
            cell.setLayoutX(x);
            cell.setLayoutY(y);

            jugadorLayer.getChildren().add(cell);

            cell.setOnMouseClicked(event -> {
                if (!cell.isUnlocked()) seleccionarJugador(cell);
            });
        }

        // Centrado manual del layer
        double totalWidth = columnas * cartaFinalWidth + (columnas - 1) * spacingX;
        double totalHeight = filas * cartaFinalHeight + (filas - 1) * spacingY;
        jugadorLayer.setTranslateX((jugadorLayer.getWidth() - totalWidth) / 2);
        jugadorLayer.setTranslateY((jugadorLayer.getHeight() - totalHeight) / 2);

    }

    private void seleccionarJugador(PlayerCell cell) {
        List<Card> opciones = new ArrayList<>(playerPool.getByPosition(cell.getPosition()));

        // ⚠️ Eliminar cartas ya seleccionadas
        opciones.removeAll(jugadoresSeleccionados.values());

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
        // 1) Recuperamos solo el contenedor de la carta (el pivote es hermano en el wrapper)
        StackPane container = cell.getCartaContainer();
        // 2) Limpiamos el placeholder (logo gris), sin tocar el pivote
        container.getChildren().clear();

        // 3) Creamos la vista real de la carta
        CardView miniCard = new CardView(cardSeleccionada);

        // 4) Calculamos la escala para que quepa EXACTAMENTE en el CELL_WIDTH/HEIGHT
        double scaleX = PlayerCell.CELL_WIDTH  / miniCard.getPrefWidth();
        double scaleY = PlayerCell.CELL_HEIGHT / miniCard.getPrefHeight();
        double scale = Math.min(scaleX, scaleY);

        miniCard.setScaleX(scale);
        miniCard.setScaleY(scale);

        // 5) La añadimos y centramos en el container
        container.getChildren().add(miniCard);
        container.setAlignment(Pos.CENTER);
    }

    public HBox getBanquilloBox() {
        return banquilloBox;
    }
}