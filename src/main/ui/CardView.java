package main.ui;

import javafx.animation.ScaleTransition;
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
                System.out.println("⚠️ No se encontró el fondo de carta.");
                setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #f5deb3);");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error cargando el fondo: " + e.getMessage());
        }
    }

    private void inicializarContenido() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(8, 8, 8, 8));

        // TopRow: iconos + imagen
        HBox topRow = new HBox(5);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox atributos = new VBox(8);
        atributos.setAlignment(Pos.TOP_CENTER);

        atributos.getChildren().addAll(
                cargarIconoPosicion(card.getPosition().name()),
                cargarIconoElemento(card.getElement().name()),
                cargarIconoGrado(card.getGrade().name())
        );

        StackPane imagenJugadorFrame = crearMarcoJugador();
        topRow.getChildren().addAll(atributos, imagenJugadorFrame);
        content.getChildren().add(topRow);

        // Nombre grande
        Label nameLabel = new Label(card.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 18));
        nameLabel.setTextFill(Color.BLACK);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPadding(new Insets(5, 0, 0, 0));

        DropShadow sombraNombre = new DropShadow();
        sombraNombre.setRadius(3);
        sombraNombre.setOffsetX(1);
        sombraNombre.setOffsetY(1);
        sombraNombre.setColor(Color.rgb(255, 255, 255, 0.8));
        nameLabel.setEffect(sombraNombre);

        content.getChildren().add(nameLabel);

        // Stats + Escudo y Score
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

        // Añadimos escudo + score a la derecha de Guts
        HBox teamAndScore = new HBox(3);
        teamAndScore.setAlignment(Pos.CENTER_LEFT);

        ImageView teamLogo = cargarEscudoEquipo(card.getTeam().name());
        Label scoreLabel = new Label(String.valueOf((int) card.getScore()));
        scoreLabel.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 14));
        scoreLabel.setTextFill(Color.GOLDENROD);

        teamAndScore.getChildren().addAll(teamLogo, scoreLabel);

        statsGrid.add(teamAndScore, 1, 3);

        content.getChildren().add(statsGrid);
    }

    private StackPane crearMarcoJugador() {
        StackPane imagenJugadorFrame = new StackPane();
        imagenJugadorFrame.setPrefSize(100, 100);
        imagenJugadorFrame.setMaxSize(100, 100);
        imagenJugadorFrame.setMinSize(100, 100);

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

        imagenJugadorFrame.getChildren().add(bordeNegro);
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
                System.out.println("No se encontró imagen jugador: " + rutaRelativa);
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
                System.out.println("No se encontró fondo jugador.");
                return new ImageView();
            }
            return new ImageView(new Image(resource.toString()));
        } catch (Exception e) {
            System.out.println("Error cargando fondo jugador.");
            return new ImageView();
        }
    }

    private ImageView cargarIconoElemento(String elemento) {
        return cargarIcono("images/elements/" + elemento.toLowerCase() + ".jpg", 25, 25);
    }

    private ImageView cargarIconoPosicion(String posicion) {
        return cargarIcono("images/positions/" + posicion.toUpperCase() + ".jpg", 25, 25);
    }

    private ImageView cargarIconoGrado(String grado) {
        String fileName = switch (grado) {
            case "FIRST_YEAR" -> "1st.jpg";
            case "SECOND_YEAR" -> "2nd.jpg";
            case "THIRD_YEAR" -> "3rd.jpg";
            default -> "1st.jpg";
        };
        return cargarIcono("images/grade/" + fileName, 25, 25);
    }

    private ImageView cargarEscudoEquipo(String equipo) {
        String fileName = equipo.toLowerCase() + ".jpg";
        return cargarIcono("images/teams/" + fileName, 30, 30);
    }

    private ImageView cargarIcono(String ruta, int ancho, int alto) {
        try {
            URL resource = getClass().getClassLoader().getResource(ruta);
            if (resource == null) {
                System.out.println("No se encontró icono: " + ruta);
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