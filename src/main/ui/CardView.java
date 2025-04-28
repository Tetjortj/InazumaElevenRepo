package main.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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

    public CardView(Card card) {
        this.card = card;
        this.content = new VBox(5);

        setPrefSize(180, 260);
        setMaxSize(180, 260);
        setMinSize(180, 260);

        inicializarClip();
        inicializarFondo();
        inicializarContenido();
        inicializarHoverEfect();

        this.getChildren().add(content);
    }

    private void inicializarClip() {
        Rectangle clip = new Rectangle(180, 260);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        setClip(clip);
    }

    private void inicializarFondo() {
        try {
            URL fondoUrl = getClass().getClassLoader().getResource("images/card_background.png");
            if (fondoUrl != null) {
                BackgroundImage backgroundImage = new BackgroundImage(
                        new Image(fondoUrl.toString(), 180, 260, false, true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT
                );
                setBackground(new Background(backgroundImage));
            } else {
                System.out.println("\u26a0\ufe0f No se encontr\u00f3 el fondo de carta.");
                setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #f5deb3);");
            }
        } catch (Exception e) {
            System.out.println("\u26a0\ufe0f Error cargando el fondo: " + e.getMessage());
        }
    }

    private void inicializarContenido() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(-5, 8, 8, 8));

        HBox topRow = new HBox(5);
        topRow.setAlignment(Pos.TOP_LEFT);

        // ESCUDO aparte
        VBox escudoBox = new VBox();
        escudoBox.setAlignment(Pos.CENTER);
        escudoBox.setPrefWidth(40); // espacio reservado para escudo
        escudoBox.setMinWidth(40);
        escudoBox.setMaxWidth(40);
        escudoBox.setPadding(new Insets(0, 0, 50, 0));
        ImageView teamLogo = cargarEscudoEquipo(card.getTeam().name());
        escudoBox.getChildren().add(teamLogo);

        // ICONOS (posición, elemento, grado)
        VBox iconos = new VBox(5);
        iconos.setAlignment(Pos.BOTTOM_LEFT);
        iconos.getChildren().addAll(
                cargarIconoPosicion(card.getPosition().name()),
                cargarIconoElemento(card.getElement().name()),
                cargarIconoGrado(card.getGrade().name())
        );

        // Imagen del jugador
        StackPane imagenJugadorFrame = crearMarcoJugador();

        // Ahora añades al HBox (escudo, iconos y foto jugador)
        topRow.getChildren().addAll(escudoBox, iconos, imagenJugadorFrame);

        content.getChildren().add(topRow);

        // Nombre grande
        Label nameLabel = new Label(card.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 18));
        nameLabel.setTextFill(Color.BLACK);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPadding(new Insets(-5, 0, 0, 0));

        DropShadow sombraNombre = new DropShadow();
        sombraNombre.setRadius(3);
        sombraNombre.setOffsetX(1);
        sombraNombre.setOffsetY(1);
        sombraNombre.setColor(Color.rgb(255, 255, 255, 0.8));
        nameLabel.setEffect(sombraNombre);

        content.getChildren().add(nameLabel);

        // Stats + Score
        GridPane statsGrid = new GridPane();
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setHgap(10);
        statsGrid.setVgap(5);

        addStat(statsGrid, "Kick", card.getKick(), 0, 0);
        addStat(statsGrid, "Body", card.getBody(), 1, 0);
        addStat(statsGrid, "Ctrl", card.getControl(), 0, 1);
        addStat(statsGrid, "Guard", card.getGuard(), 1, 1);
        addStat(statsGrid, "Spd", card.getSpeed(), 0, 2);
        addStat(statsGrid, "Stam", card.getStamina(), 1, 2);
        addStat(statsGrid, "Guts", card.getGuts(), 0, 3);

        Label scoreLabel = new Label(String.valueOf((int) card.getScore()));
        scoreLabel.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 24));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(scoreLabel, HPos.RIGHT);
        scoreLabel.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(5), Insets.EMPTY
        )));
        scoreLabel.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT
        )));
        scoreLabel.setPadding(new Insets(5));

        statsGrid.add(scoreLabel, 1, 3);

        content.getChildren().add(statsGrid);
    }

    private StackPane crearMarcoJugador() {
        StackPane imagenJugadorFrame = new StackPane();
        imagenJugadorFrame.setPrefSize(100, 100);

        VBox contenedorImagen = new VBox();
        contenedorImagen.setAlignment(Pos.TOP_CENTER);
        contenedorImagen.setPadding(new Insets(8, 0, 0, 0));

        ImageView fondoJugador = cargarFondoJugador();
        fondoJugador.setFitWidth(96);
        fondoJugador.setFitHeight(96);
        Rectangle fondoClip = new Rectangle(96, 96);
        fondoClip.setArcWidth(10);
        fondoClip.setArcHeight(10);
        fondoJugador.setClip(fondoClip);

        ImageView fotoJugador = cargarFotoJugador(card.getPhotoPath());
        fotoJugador.setFitWidth(96);
        fotoJugador.setFitHeight(96);
        fotoJugador.setPreserveRatio(true);

        DropShadow sombra = new DropShadow();
        sombra.setRadius(4.0);
        sombra.setOffsetX(0);
        sombra.setOffsetY(0);
        sombra.setColor(Color.rgb(0, 0, 0, 0.7));
        fotoJugador.setEffect(sombra);

        StackPane bordeNegro = new StackPane(fondoJugador, fotoJugador);
        bordeNegro.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        bordeNegro.setPrefSize(96, 96);
        bordeNegro.setAlignment(Pos.CENTER);

        contenedorImagen.getChildren().add(bordeNegro);
        imagenJugadorFrame.getChildren().add(contenedorImagen);
        return imagenJugadorFrame;
    }

    private void inicializarHoverEfect() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), this);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), this);
        scaleOut.setToX(1);
        scaleOut.setToY(1);

        this.setOnMouseEntered(e -> scaleIn.playFromStart());
        this.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    private void addStat(GridPane grid, String label, int value, int col, int row) {
        Label stat = new Label(label + ": " + value);
        stat.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        stat.setTextFill(Color.BLACK);
        stat.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(3),
                Insets.EMPTY
        )));
        stat.setPadding(new Insets(2, 5, 2, 5));
        grid.add(stat, col, row);
    }

    private ImageView cargarFotoJugador(String rutaRelativa) {
        try {
            URL resource = getClass().getClassLoader().getResource("images/players/" + rutaRelativa);
            if (resource == null) {
                System.out.println("No se encontr\u00f3 imagen jugador: " + rutaRelativa);
                return new ImageView();
            }
            Image img = new Image(resource.toString());
            ImageView imageView = new ImageView(img);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.out.println("Error cargando imagen jugador: " + rutaRelativa);
            return new ImageView();
        }
    }

    private ImageView cargarFondoJugador() {
        try {
            URL resource = getClass().getClassLoader().getResource("images/card_player_background.png");
            if (resource == null) {
                System.out.println("No se encontr\u00f3 fondo jugador.");
                return new ImageView();
            }
            return new ImageView(new Image(resource.toString()));
        } catch (Exception e) {
            System.out.println("Error cargando fondo jugador.");
            return new ImageView();
        }
    }

    private ImageView cargarIconoElemento(String elemento) {
        return cargarIcono("images/elements/" + elemento.toLowerCase() + ".jpg", 22, 22);
    }

    private ImageView cargarIconoPosicion(String posicion) {
        return cargarIcono("images/positions/" + posicion.toUpperCase() + ".jpg", 22, 22);
    }

    private ImageView cargarIconoGrado(String grado) {
        String fileName = switch (grado) {
            case "FIRST_YEAR" -> "1st.jpg";
            case "SECOND_YEAR" -> "2nd.jpg";
            case "THIRD_YEAR" -> "3rd.jpg";
            default -> "1st.jpg";
        };
        return cargarIcono("images/grade/" + fileName, 22, 22);
    }

    private ImageView cargarEscudoEquipo(String equipo) {
        String fileName = equipo.toLowerCase() + ".jpg";
        int ancho = 50, alto = 50;
        if (equipo.equalsIgnoreCase("Street_Sallys")) {
            ancho = 35;
            alto = 35;
        } else if(equipo.equalsIgnoreCase("Raimon") || equipo.equalsIgnoreCase("Zeus")) {
            ancho = 40;
            alto = 40;
        }
        return cargarIcono("images/teams/" + fileName, ancho, alto);
    }

    private ImageView cargarIcono(String ruta, int ancho, int alto) {
        try {
            URL resource = getClass().getClassLoader().getResource(ruta);
            if (resource == null) {
                System.out.println("No se encontr\u00f3 icono: " + ruta);
                return new ImageView();
            }
            Image img = new Image(resource.toString());
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(ancho);
            imageView.setFitHeight(alto);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.out.println("Error cargando icono: " + ruta);
            return new ImageView();
        }
    }
}