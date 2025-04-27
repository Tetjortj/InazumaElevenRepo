package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.Card;

import java.io.File;
import java.io.InputStream;

import java.net.URL;


public class CardView extends StackPane {

    private final Card card;
    private final VBox content;

    public CardView(Card card) {
        this.card = card;
        this.content = new VBox(8);

        setPrefSize(180, 280);
        setMaxSize(180, 280);
        setMinSize(180, 280);

        setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 10px; -fx-border-radius: 10px;");

        inicializarContenido();

        this.getChildren().add(content);
    }

    private void inicializarContenido() {
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10));

        // Imagen principal del jugador
        if (card.getPhotoPath() != null && !card.getPhotoPath().isEmpty()) {
            System.out.println(card.getPhotoPath());
            content.getChildren().add(cargarFotoJugador(card.getPhotoPath()));
        }

        // Iconos de Elemento, Posición y Grado
        HBox iconsRow = new HBox(5);
        iconsRow.setAlignment(Pos.CENTER);

        iconsRow.getChildren().addAll(
                cargarIconoElemento(card.getElement().name()),
                cargarIconoPosicion(card.getPosition().name()),
                cargarIconoGrado(card.getGrade().name())
        );
        content.getChildren().add(iconsRow);

        // Nombre del jugador + Escudo del equipo
        HBox nameRow = new HBox(5);
        nameRow.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(card.getName());
        nameLabel.setFont(Font.font("Arial", 14));
        nameLabel.setTextFill(Color.BLACK);

        ImageView teamLogo = cargarEscudoEquipo(card.getTeam().name());

        nameRow.getChildren().addAll(teamLogo, nameLabel);
        content.getChildren().add(nameRow);

        // Score
        Label score = new Label("Score: " + card.getScore());
        score.setFont(Font.font("Arial", 12));
        score.setTextFill(Color.BLACK);
        content.getChildren().add(score);
    }

    private ImageView cargarFotoJugador(String rutaRelativa) {
        return cargarIcono("images/players/" + rutaRelativa, 30, 30);
    }
    private ImageView cargarIconoElemento(String elemento) {
        return cargarIcono("images/elements/" + elemento.toLowerCase() + ".jpg", 30, 30);
    }

    private ImageView cargarIconoPosicion(String posicion) {
        return cargarIcono("images/positions/" + posicion.toUpperCase() + ".jpg", 30, 30);
    }

    private ImageView cargarIconoGrado(String grado) {
        String fileName = switch (grado) {
            case "FIRST_YEAR" -> "1st.jpg";
            case "SECOND_YEAR" -> "2nd.jpg";
            case "THIRD_YEAR" -> "3rd.jpg";
            default -> "1st.jpg";
        };
        return cargarIcono("images/grade/" + fileName, 30, 30);
    }

    private ImageView cargarEscudoEquipo(String equipo) {
        String fileName = equipo.toLowerCase() + ".jpg"; // NO hacer replace
        return cargarIcono("images/teams/" + fileName, 30, 30);
    }

    private ImageView cargarIcono(String ruta, int ancho, int alto) {
        try {
            System.out.println(ruta);
            InputStream is = getClass().getClassLoader().getResourceAsStream(ruta);
            if (is == null) {
                System.out.println("No se encontró icono: " + ruta);
                return new ImageView();
            }
            Image img = new Image(is);
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