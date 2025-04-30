package main.ui.screens;

import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import main.Formation;
import main.ui.FormationPreview;

import java.util.List;
import java.util.function.Consumer;

public class FormationSelectionScreen extends BorderPane {
    private static final double PADDING               = 40;
    private static final double SPACING               = 60;
    private static final double PREVIEW_RATIO         = 1.5;  // alto = ancho * 0.6
    private static final double HORIZONTAL_FILL_RATIO = 0.8;  // 80% del ancho para previews

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

        // 2) Padding y título
        setPadding(new Insets(PADDING));
        Label title = new Label("Elige tu formación");
        title.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 36));
        title.setTextFill(Color.BLACK);
        setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        // 3) Calculamos tamaños “reales” basados en pantalla
        double availableWidth = ScreenInfo.SCREEN_WIDTH - 2 * PADDING;
        int    count          = opts.size();
        double totalGaps      = SPACING * (count - 1);
        double usableWidth    = availableWidth * HORIZONTAL_FILL_RATIO - totalGaps;
        double eachWidth      = usableWidth / count;
        double eachHeight     = eachWidth * PREVIEW_RATIO;

        // 4) Contenedor de previews
        HBox box = new HBox(SPACING);
        box.setAlignment(Pos.CENTER);

        // efecto de hover: DropShadow dorado
        DropShadow hoverShadow = new DropShadow();
        hoverShadow.setRadius(15);
        hoverShadow.setColor(Color.web("#FFD700", 0.7));

        for (Formation f : opts) {
            // preview gráfica
            FormationPreview prev = new FormationPreview(f, () -> onSelect.accept(f));
            prev.setPrefSize(eachWidth, eachHeight);
            prev.setMinSize(eachWidth, eachHeight);
            prev.setMaxSize(eachWidth, eachHeight);

            // hover
            prev.setOnMouseEntered(e -> {
                prev.setEffect(hoverShadow);
                prev.setCursor(Cursor.HAND);
            });
            prev.setOnMouseExited(e -> {
                prev.setEffect(null);
                prev.setCursor(Cursor.DEFAULT);
            });

            // nombre bajo la preview
            Label name = new Label(f.getName());
            name.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
            name.setTextFill(Color.BLACK);

            VBox item = new VBox(15, prev, name);
            item.setAlignment(Pos.CENTER);
            box.getChildren().add(item);
        }

        setCenter(box);
    }
}