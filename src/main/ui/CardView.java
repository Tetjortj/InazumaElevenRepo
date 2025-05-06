package main.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import main.Card;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import javafx.scene.shape.Rectangle;

public class CardView extends StackPane {

    private final Card card;
    private final VBox content;
    private double originalScaleX;
    private double originalScaleY;

    public CardView(Card card) {
        this.card = card;
        this.content = new VBox(5);

        setUpSizeAndClip();
        setUpBackground();
        setUpContent();
        setUpHoverEfect();

        originalScaleX = getScaleX();
        originalScaleY = getScaleY();

        // Añadirlo al StackPane (detrás del content)
        this.getChildren().addAll(content);
    }

    //MET: Inicializa la altura y amplitud de la carta, junto con el recorte
    private void setUpSizeAndClip() {
        setPrefSize(CardConfig.FULL_WIDTH, CardConfig.FULL_HEIGHT);
        setMinSize(CardConfig.FULL_WIDTH, CardConfig.FULL_HEIGHT);
        setMaxSize(CardConfig.FULL_WIDTH, CardConfig.FULL_HEIGHT);

        Rectangle clip = new Rectangle(
                CardConfig.FULL_WIDTH,
                CardConfig.FULL_HEIGHT
        );
        clip.setArcWidth(CardConfig.ARC_RADIUS);
        clip.setArcHeight(CardConfig.ARC_RADIUS);
        setClip(clip);
    }

    //MET: Inicializa background de la carta, tamaño y imagen
    private void setUpBackground() {
        URL fondoUrl = getClass().getClassLoader().getResource("images/card_background.png");
        if (fondoUrl != null) {
            BackgroundImage bgImg = new BackgroundImage(
                    new Image(fondoUrl.toString(),
                            CardConfig.FULL_WIDTH, CardConfig.FULL_HEIGHT,
                            false, true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            setBackground(new Background(bgImg));
        } else {
            // Fallback estético
            setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #f5deb3);");
        }
    }

    //MET: Inicializa el contenido de dentro de la carta
    private void setUpContent() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(CardConfig.CONTENT_PADDING));

        content.getChildren().addAll(
                createTopRow(),
                createNameSection(),
                createSeparator(),
                createStatsSection()
        );
        addScoreLabel();
    }

    //MET: Devuelve Node con el contenido de la parte de arriba de la carta
    //(escudo, iconos y retrato)
    private Node createTopRow() {
        HBox topRow = new HBox(CardConfig.TOP_ROW_SPACING);
        topRow.setAlignment(Pos.TOP_LEFT);

        Pane shieldBox     = createShield();
        VBox iconsBox      = createIcons();
        StackPane photoBox = createPlayerPhoto();

        topRow.getChildren().addAll(shieldBox, iconsBox, photoBox);
        return topRow;
    }

    //MET: Devuelve Pane con la imagen del escudo
    private Pane createShield() {
        Pane box = new VBox();
        double w = CardConfig.FULL_WIDTH * CardConfig.SHIELD_BOX_RATIO;
        box.setPrefWidth(w);

        ImageView logo = ImageLoader.loadTeamLogo(card.getTeam().name());

        box.getChildren().add(logo);
        return box;
    }

    //MET: Devuelve VBox con los iconos
    private VBox createIcons() {
        ImageView posIcon = ImageLoader.loadPositionIcon(card.getPosition().name(),
                                                                        CardConfig.ICON_SIZE);
        ImageView elemIcon = ImageLoader.loadElementIcon(card.getElement().name(),
                                                                        CardConfig.ICON_SIZE);
        ImageView grade  = ImageLoader.loadGradeIcon(card.getGrade().name(),
                                                                        CardConfig.ICON_SIZE);
        return new VBox(CardConfig.ICON_SPACING,
                posIcon,
                elemIcon,
                grade
        );
    }

    //MET: Devuelve StackPane con el marco del jugador
    private StackPane createPlayerPhoto() {
        StackPane framePane = new StackPane();
        double frame = CardConfig.FULL_WIDTH * CardConfig.PLAYER_FRAME_RATIO;
        framePane.setPrefSize(frame, frame);

        VBox photoContainer = new VBox();
        photoContainer.setAlignment(Pos.TOP_CENTER);
        photoContainer.setPadding(new Insets(8, 0, 0, 0));

        ImageView background = ImageLoader.loadPlayerFrameBackground(CardConfig.PLAYER_FRAME_SIZE,
                                                                        CardConfig.PLAYER_FRAME_SIZE);

        background.setClip(new Rectangle(CardConfig.PLAYER_FRAME_SIZE, CardConfig.PLAYER_FRAME_SIZE,
                CardConfig.PLAYER_FRAME_SIZE, CardConfig.PLAYER_FRAME_SIZE) {{
            setArcWidth(CardConfig.ARC_RADIUS/2);
            setArcHeight(CardConfig.ARC_RADIUS/2);
        }});

        ImageView photo = ImageLoader.loadPlayerPhoto(card.getPhotoPath(),
                                                                        CardConfig.PLAYER_FRAME_SIZE);

        photo.setEffect(new DropShadow(4, 0, 0, Color.rgb(0,0,0,0.7)));

        StackPane portrait = new StackPane(background, photo);
        portrait.setStyle("-fx-border-color:black; -fx-border-width:2px;"
                + "-fx-background-radius:5px; -fx-border-radius:5px;");
        portrait.setPrefSize(CardConfig.PLAYER_FRAME_SIZE, CardConfig.PLAYER_FRAME_SIZE);
        portrait.setAlignment(Pos.CENTER);

        photoContainer.getChildren().add(portrait);
        framePane.getChildren().add(photoContainer);
        return framePane;
    }

    //MET: Devuelve Node con la seccion del nombre
    private Node createNameSection() {
        // --- Nombre del jugador
        Label lbl = new Label(card.getName());
        lbl.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, CardConfig.FULL_WIDTH *
                                                                        CardConfig.NAME_FONT_RATIO));
        lbl.setTextFill(Color.BLACK);
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        lbl.setPadding(new Insets(CardConfig.FULL_HEIGHT * CardConfig.NAME_PAD_TOP_RATIO,
                                                                    0, 0, 0));

        //Sombra del nombre
        lbl.setEffect(new DropShadow(3, 1, 1, Color.rgb(255,255,255,0.8)));

        return lbl;
    }

    //MET: Devuelve Node con el separador
    private Node createSeparator() {
        Rectangle sep = new Rectangle(CardConfig.FULL_WIDTH * CardConfig.SEPARATOR_WIDTH_RATIO,
                                                                                            1);
        sep.setFill(Color.rgb(0,0,0,0.5));
        return sep;
    }

    //MET: Devuelve Node con todas las stats y su background transparente
    private Node createStatsSection() {
        double h = CardConfig.FULL_HEIGHT * CardConfig.STATS_SECTION_RATIO;
        Rectangle statsBackground = new Rectangle(CardConfig.FULL_WIDTH, h);
        statsBackground.setFill(Color.rgb(4,127,191,0.5));

        GridPane stats = createStatsGrid();

        StackPane overlay = new StackPane(statsBackground, stats);
        overlay.setPrefSize(CardConfig.FULL_WIDTH, h);
        return overlay;
    }

    //MET: Devuelve GridPane con todas las stats
    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(CardConfig.STATS_GRID_HGAP);
        grid.setVgap(CardConfig.STATS_GRID_VGAP);
        grid.setAlignment(Pos.CENTER);

        addStat(grid, "KIC", card.getKick(), 0, 0);
        addStat(grid, "BOD", card.getBody(), 1, 0);
        addStat(grid, "CON", card.getControl(), 0, 1);
        addStat(grid, "GUA", card.getGuard(), 1, 1);
        addStat(grid, "SPE", card.getSpeed(), 0, 2);
        addStat(grid, "STA", card.getStamina(), 1, 2);

        Label guts = new Label(card.getGuts() + " GUTS");
        guts.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, CardConfig.GUTS_FONT_SIZE));
        guts.setTextFill(Color.YELLOW);
        guts.setEffect(new DropShadow(3,1,1,Color.rgb(255,255,255,0.8)));
        grid.add(guts, 0, 3, 2, 1);
        GridPane.setHalignment(guts, HPos.CENTER);

        return grid;
    }

    //MET: Añade el score dentro de la carta
    private void addScoreLabel() {
        Label score = new Label(String.valueOf((int)card.getScore()));
        score.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD,
                CardConfig.FULL_WIDTH * CardConfig.SCORE_FONT_RATIO));
        score.setTextFill(Color.YELLOW);
        score.setEffect(new DropShadow(3,1,1,Color.rgb(255,255,255,0.8)));
        StackPane.setAlignment(score, Pos.TOP_LEFT);
        StackPane.setMargin(score, new Insets(
                CardConfig.FULL_HEIGHT * CardConfig.SCORE_MARGIN_TOP_RATIO,
                0, 0,
                CardConfig.FULL_WIDTH  * CardConfig.SCORE_MARGIN_LEFT_RATIO
        ));
        getChildren().add(score);
    }

    //MET: Añade la animacion al pasar por encima de las cartas
    private void setUpHoverEfect() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), this);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), this);

        this.setOnMouseEntered(e -> {
            originalScaleX = getScaleX();
            originalScaleY = getScaleY();

            // Aumentamos un 10%
            scaleIn.setToX(originalScaleX * 1.1);
            scaleIn.setToY(originalScaleY * 1.1);

            scaleIn.playFromStart();
        });

        this.setOnMouseExited(e -> {
            scaleOut.setToX(originalScaleX);
            scaleOut.setToY(originalScaleY);
            scaleOut.playFromStart();
        });
    }

    //MET: Añade una estadistica al GridPane correspondiente
    private void addStat(GridPane grid, String label, int value, int col, int row) {
        Label stat = new Label(value+ " " + label);
        stat.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, CardConfig.STAT_FONT_SIZE)); // Igual que el nombre
        stat.setTextFill(Color.YELLOW);
        stat.setWrapText(true);
        stat.setAlignment(Pos.CENTER);

        // Aplicar sombra blanca
        stat.setEffect(new DropShadow(3,1,1, Color.rgb(255,255,255,0.8)));

        stat.setPadding(new Insets(CardConfig.STAT_PADDING));

        grid.add(stat, col, row);
    }
}