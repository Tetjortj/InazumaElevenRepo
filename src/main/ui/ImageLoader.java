package main.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * Utilidades para cargar recursos gráficos (jugadores, iconos, escudos...).
 */
public final class ImageLoader {

    private ImageLoader() { /* no instanciable */ }

    /**
     * Carga una imagen desde el classpath y la envuelve en un ImageView.
     * @param resourcePath ruta dentro de /resources (p. ej. "images/players/foo.png")
     * @param fitWidth     ancho deseado (si ≤ 0 no lo fuerza)
     * @param fitHeight    alto deseado (si ≤ 0 no lo fuerza)
     */
    public static ImageView load(String resourcePath, double fitWidth, double fitHeight) {
        try {
            URL res = ImageLoader.class.getClassLoader().getResource(resourcePath);
            if (res == null) {
                System.err.println("⚠️ No encontrado recurso: " + resourcePath);
                return new ImageView();
            }
            Image img = new Image(res.toString());
            ImageView iv = new ImageView(img);
            if (fitWidth  > 0) iv.setFitWidth(fitWidth);
            if (fitHeight > 0) iv.setFitHeight(fitHeight);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            System.err.println("⚠️ Error cargando imagen: " + resourcePath);
            return new ImageView();
        }
    }

    /** Carga la foto del jugador. */
    public static ImageView loadPlayerPhoto(String filename, double size) {
        return load("images/players/" + filename, size, size);
    }

    /** Carga el fondo del marco de jugador. */
    public static ImageView loadPlayerFrameBackground(double width, double height) {
        return load("images/card_player_background.png", width, height);
    }

    /** Carga un icono genérico de carpeta + nombre. */
    public static ImageView loadIcon(String folder, String name, double size) {
        return load(String.format("images/%s/%s", folder, name), size, size);
    }

    /** Carga el icono de posición. */
    public static ImageView loadPositionIcon(String posName, double size) {
        return loadIcon("positions", posName.toUpperCase() + ".jpg", size);
    }

    /** Carga el icono de elemento. */
    public static ImageView loadElementIcon(String elemName, double size) {
        return loadIcon("elements", elemName.toLowerCase() + ".jpg", size);
    }

    /** Carga el icono de grado. */
    public static ImageView loadGradeIcon(String gradeName, double size) {
        String file = switch (gradeName) {
            case "FIRST_YEAR"  -> "1st.jpg";
            case "SECOND_YEAR" -> "2nd.jpg";
            case "THIRD_YEAR"  -> "3rd.jpg";
            case "ADULT"       -> "adult.jpg";
            default -> "1st.jpg";
        };
        return loadIcon("grade", file, size);
    }

    /** Carga el escudo de equipo, con ajustes por equipo. */
    public static ImageView loadTeamLogo(String teamName) {
        String file = teamName.toLowerCase() + ".jpg";
        double size = switch (teamName.toLowerCase()) {
            case "street_sallys" -> CardConfig.TEAM_LOGO_ALT_SMALLEST;
            case "raimon", "zeus"-> CardConfig.TEAM_LOGO_ALT_SMALL;
            default              -> CardConfig.TEAM_LOGO_DEFAULT;
        };
        return loadIcon("teams", file, size);
    }
}
