package main.ui.screens;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.Formation;
import main.ui.FormationPreview;

import java.util.List;
import java.util.function.Consumer;

public class FormationSelectionScreen extends BorderPane {
    private static final double PADDING               = 40;
    private static final double SPACING               = 60;
    private static final double PREVIEW_RATIO         = 1.5;  // alto = ancho * 1.5
    private static final double HORIZONTAL_FILL_RATIO = 0.8;  // 80% del ancho para previews

    private Duration entryDelay = Duration.seconds(1);

    // guardamos la caja para poder animarla
    private final HBox box = new HBox(SPACING);

    public FormationSelectionScreen(List<Formation> opts, Consumer<Formation> onSelect) {
        // 1) Fondo de toda la escena
        Image bg = new Image(
                getClass().getResource("/images/selection_formation_background.png")
                        .toExternalForm()
        );
        BackgroundImage bgi = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO, BackgroundSize.AUTO,
                        false, false,
                        true, true
                )
        );
        setBackground(new Background(bgi));

        // 2) Padding general
        setPadding(new Insets(PADDING));

        // 3) Título con fondo semitransparente
        Label title = new Label("Elige tu formación");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.BLACK);
        StackPane titleContainer = new StackPane(title);
        titleContainer.setPadding(new Insets(10, 20, 10, 20));
        titleContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.8),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        setTop(titleContainer);
        BorderPane.setAlignment(titleContainer, Pos.CENTER);

        // 4) Calculamos cuánto mide cada preview según el ancho de pantalla
        double availableWidth = ScreenInfo.SCREEN_WIDTH - 2 * PADDING;
        int    count          = opts.size();
        double totalGaps      = SPACING * (count - 1);
        double usableWidth    = availableWidth * HORIZONTAL_FILL_RATIO - totalGaps;
        double eachWidth      = usableWidth / count;
        double eachHeight     = eachWidth * PREVIEW_RATIO;

        // 5) Creamos HBox de previews
        box.setAlignment(Pos.CENTER);

        // 6) Efecto hover: sombra azul brillante
        DropShadow hoverShadow = new DropShadow();
        hoverShadow.setRadius(15);
        hoverShadow.setColor(Color.web("#00BFFF"));

        for (Formation f : opts) {
            // --- preview gráfica ---
            FormationPreview prev = new FormationPreview(f, () -> onSelect.accept(f));
            prev.setPrefSize(eachWidth, eachHeight);
            prev.setMinSize(eachWidth, eachHeight);
            prev.setMaxSize(eachWidth, eachHeight);
            prev.setOpacity(1);  // el preview en sí permanece visible, ocultaremos sólo las bolitas

            prev.setOnMouseEntered(e -> {
                prev.setEffect(hoverShadow);
                prev.setCursor(Cursor.HAND);
            });
            prev.setOnMouseExited(e -> {
                prev.setEffect(null);
                prev.setCursor(Cursor.DEFAULT);
            });

            // --- nombre bajo la preview ---
            Label name = new Label(f.getName());
            name.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            name.setTextFill(Color.BLACK);

            // metemos en un VBox
            VBox item = new VBox(15, name, prev);
            item.setAlignment(Pos.CENTER);
            box.getChildren().add(item);
        }

        // 7) Ponemos la HBox dentro de un StackPane con fondo semitransparente
        StackPane centerContainer = new StackPane(box);
        centerContainer.setPadding(new Insets(20));
        centerContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        setCenter(centerContainer);

        // 8) Inicialmente ocultamos **sólo** nombres y bolitas
        for (Node node : box.getChildren()) {
            VBox item = (VBox) node;
            Label name = (Label) item.getChildren().get(0);
            FormationPreview prev = (FormationPreview) item.getChildren().get(1);

            name.setOpacity(0);
            prev.getMarkers().setOpacity(0);
        }

        Platform.runLater(this::playEntryAnimation);
    }

    /**
     * Recorre cada item y hace aparecer secuencialmente
     * la etiqueta y las bolitas de cada preview.
     */
    public void playEntryAnimation() {
        SequentialTransition seq = new SequentialTransition();
        seq.setDelay(entryDelay);

        for (Node node : box.getChildren()) {
            VBox item = (VBox) node;
            Label name = (Label) item.getChildren().get(0);
            FormationPreview prev = (FormationPreview) item.getChildren().get(1);

            FadeTransition fadeName = new FadeTransition(Duration.seconds(0.8), name);
            fadeName.setFromValue(0);
            fadeName.setToValue(2);

            FadeTransition fadeDots = new FadeTransition(Duration.seconds(0.8), prev.getMarkers());
            fadeDots.setFromValue(0);
            fadeDots.setToValue(2);

            seq.getChildren().add(new ParallelTransition(fadeName, fadeDots));
        }

        seq.play();
    }
}