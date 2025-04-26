package main.scraping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.Card;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CardScraping {

    private static final String BASE_URL = "https://inazuma.fandom.com";
    private static final String CATEGORY_URL = "https://inazuma.fandom.com/es/wiki/Categor%C3%ADa:Equipos_(IE_Original_T1)";
    private static final String IMAGE_FOLDER = "src/main/resources/images/players";

    private static final Map<String, String> teamNameMapping = new HashMap<>();
    static {
        teamNameMapping.put("Raimon", "Instituto Raimon");
        teamNameMapping.put("Occult", "Instituto Occult");
        teamNameMapping.put("Wild", "Instituto Wild");
        teamNameMapping.put("Brain", "Instituto Brain");
        teamNameMapping.put("Otaku", "Instituto Otaku");
        teamNameMapping.put("Royal_Academy", "Royal Academy");
        teamNameMapping.put("Shuriken", "Instituto Shuriken");
        teamNameMapping.put("Farm", "Instituto Farm");
        teamNameMapping.put("Kirkwood", "Instituto Kirkwood");
        teamNameMapping.put("Zeus", "Instituto Zeus");
        teamNameMapping.put("Raimon_Old_Boys", "Inazuma Eleven (Equipo)");
        teamNameMapping.put("Street_Sallys", "Sallys");
        teamNameMapping.put("Inazuma_KFC", "Inazuma Kids FC");
        teamNameMapping.put("Umbrella", "Instituto Umbrella");
    }

    private String equipoAProcesar = null;

    public void setEquipoAProcesar(String equipo) {
        this.equipoAProcesar = equipo;
    }

    public void actualizarCartasConFotos(String inputJsonPath, String outputJsonPath) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Copiar JSON original a uno temporal
        Path copiaTemporal = Path.of("src/main/resources/copia_temp.json");
        Files.copy(Path.of(inputJsonPath), copiaTemporal, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Leer el JSON temporal
        Type listType = new TypeToken<List<Card>>() {}.getType();
        List<Card> cartas = gson.fromJson(new FileReader(copiaTemporal.toFile()), listType);

        // Asegurar que la carpeta de imágenes existe
        Files.createDirectories(Path.of(IMAGE_FOLDER));

        // Construir mapa de equipos a enlaces
        Map<String, String> equipoUrls = obtenerUrlsEquipos();

        for (Card card : cartas) {
            if (card.getTeam() == null || !teamNameMapping.containsKey(card.getTeam().name())) {
                continue;
            }

            if (equipoAProcesar != null && !card.getTeam().name().equalsIgnoreCase(equipoAProcesar)) {
                continue;
            }

            String nombreEquipoWeb = teamNameMapping.get(card.getTeam().name());
            String equipoUrl = equipoUrls.get(nombreEquipoWeb);

            if (equipoUrl == null) {
                System.out.println("No se encontró URL para equipo: " + nombreEquipoWeb);
                continue;
            }

            try {
                String fotoUrl = buscarFotoJugador(equipoUrl, card.getName());

                if (fotoUrl != null) {
                    String nombreImagen = generarNombreImagen(card.getName());
                    descargarImagen(fotoUrl, nombreImagen);
                    card.setPhotoPath("images/players/" + nombreImagen);
                    System.out.println("Foto descargada para " + card.getName());
                } else {
                    System.out.println("No se encontró foto para: " + card.getName());
                }
            } catch (Exception e) {
                System.out.println("Error procesando jugador: " + card.getName() + " - " + e.getMessage());
            }
        }

        // Guardar JSON actualizado
        try (FileWriter writer = new FileWriter(outputJsonPath)) {
            gson.toJson(cartas, writer);
        }

        // Eliminar copia temporal
        Files.deleteIfExists(copiaTemporal);
    }

    private Map<String, String> obtenerUrlsEquipos() throws Exception {
        Map<String, String> map = new HashMap<>();
        Document doc = Jsoup.connect(CATEGORY_URL).get();

        Elements equipos = doc.select("div.category-page__members a.category-page__member-link");
        for (Element equipo : equipos) {
            String nombre = equipo.text();
            String href = equipo.attr("href");
            map.put(nombre, BASE_URL + href);
        }
        return map;
    }

    private String buscarFotoJugador(String equipoUrl, String nombreJugador) throws Exception {
        Document doc = Jsoup.connect(equipoUrl).get();

        Elements tablas = doc.select("table");

        if (tablas.isEmpty()) {
            System.out.println("❌ No hay tablas en la página: " + equipoUrl);
            return null;
        }

        Element tablaPrincipal = tablas.get(0); // ⬅️ Usamos la primera tabla
        Elements filas = tablaPrincipal.select("tbody > tr");

        String nombreJugadorNormalizado = normalizarTexto(nombreJugador);

        for (Element fila : filas) {
            Elements ths = fila.select("th");
            Elements tds = fila.select("td");

            if (ths.size() >= 2 && tds.size() >= 1) {
                // ---------------------
                // 🔥 IMAGEN
                // ---------------------
                Element thImagen = ths.get(1);
                Element img = thImagen.selectFirst("span[typeof=\"mw:File\"] a img");

                // ---------------------
                // 🔥 NOMBRE
                // ---------------------
                Element tdNombre = tds.get(0);
                Element linkNombre = tdNombre.selectFirst("a");
                String nombreEnTabla = linkNombre != null ? normalizarTexto(linkNombre.text()) : "";

                if (nombreJugadorNormalizado.equals(nombreEnTabla) ||
                        nombreJugadorNormalizado.contains(nombreEnTabla) ||
                        nombreEnTabla.contains(nombreJugadorNormalizado)) {

                    if (img != null) {
                        String fotoUrl = img.hasAttr("data-src") ? img.attr("data-src") : img.attr("src");

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            if (!fotoUrl.startsWith("http")) {
                                fotoUrl = "https:" + fotoUrl;
                            }
                            return fotoUrl;
                        }
                    }
                }
            }
        }

        System.out.println("❌ No se encontró el jugador: " + nombreJugador + " en " + equipoUrl);
        return null;
    }

    private void descargarImagen(String url, String nombreArchivo) throws Exception {
        try (var in = new URL(url).openStream(); var out = new FileOutputStream(new File(IMAGE_FOLDER, nombreArchivo))) {
            in.transferTo(out);
        }
    }

    private String generarNombreImagen(String nombre) {
        return nombre.toLowerCase().replace(" ", "_") + ".jpg";
    }

    private String normalizarTexto(String texto) {
        return texto.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace("(", "").replace(")", "")
                .replace("[", "").replace("]", "")
                .replace("-", " ")
                .trim();
    }
}