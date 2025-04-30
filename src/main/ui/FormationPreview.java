package main.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.Formation;
import main.PlayerPlacement;

public class FormationPreview extends StackPane {
    private static final double DOT_RADIUS = 12;

    private final ImageView field;
    private final Pane markers;

    public FormationPreview(Formation formation, Runnable onSelect) {
        // --- Fondo del campo ---
        field = new ImageView(
                new Image(getClass().getResource("/images/draft_background2.png")
                        .toExternalForm())
        );
        field.setPreserveRatio(false);

        // --- Capa de marcadores ---
        markers = new Pane();
        buildMarkers(formation);

        getChildren().addAll(field, markers);

        // --- Hover pequeña escala ---
        ScaleTransition stIn  = new ScaleTransition(Duration.millis(200), this);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), this);
        stIn .setToX(1.05); stIn .setToY(1.05);
        stOut.setToX(1.00); stOut.setToY(1.00);

        setOnMouseEntered(e -> {
            stOut.stop();
            stIn.playFromStart();
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(e -> {
            stIn.stop();
            stOut.playFromStart();
            setCursor(Cursor.DEFAULT);
        });
        setOnMouseClicked(e -> onSelect.run());
    }

    private void buildMarkers(Formation f) {
        int minR = f.getMinFila(), minC = f.getMinColumna();
        int maxR = f.getPlacements().stream().mapToInt(PlayerPlacement::getFila).max().orElse(minR);
        int maxC = f.getPlacements().stream().mapToInt(PlayerPlacement::getColumna).max().orElse(minC);
        int rows    = maxR - minR + 1;
        int columns = maxC - minC + 1;

        markers.getChildren().clear();
        for (PlayerPlacement p : f.getPlacements()) {
            int row = p.getFila()    - minR;
            int col = p.getColumna() - minC;

            Circle dot = new Circle(DOT_RADIUS, Color.web("#0096C9"));
            dot.setStroke(Color.WHITE);
            dot.setStrokeWidth(1);

            // Bind al tamaño real de este StackPane
            dot.layoutXProperty().bind(
                    widthProperty().divide(columns)
                            .multiply(col + 0.5)
            );
            dot.layoutYProperty().bind(
                    heightProperty().divide(rows)
                            .multiply(row + 0.5)
            );
            markers.getChildren().add(dot);
        }
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight();
        field.setFitWidth(w);
        field.setFitHeight(h);
        markers.resizeRelocate(0, 0, w, h);
        super.layoutChildren();
    }

    /** Para la animación: poder acceder a las bolitas */
    public Pane getMarkers() {
        return markers;
    }
}