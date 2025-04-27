package main.scraping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ObjectMapper objectMapper = new ObjectMapper();
        Path copiaTemporal = Path.of("src/main/resources/copia_temp.json");
        Files.copy(Path.of(inputJsonPath), copiaTemporal, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        List<Card> cartas = objectMapper.readValue(copiaTemporal.toFile(), new TypeReference<List<Card>>() {});

        Files.createDirectories(Path.of(IMAGE_FOLDER));
        Map<String, String> equipoUrls = obtenerUrlsEquipos();

        for (Card card : cartas) {
            if (card.getTeam() == null || !teamNameMapping.containsKey(card.getTeam().name())) {
                continue;
            }

            String nombreEquipoWeb = teamNameMapping.get(card.getTeam().name());
            String equipoUrl = equipoUrls.get(nombreEquipoWeb);

            if (equipoUrl == null) {
                System.out.println("No se encontró URL para equipo: " + nombreEquipoWeb);
                continue;
            }

            boolean debeActualizar = equipoAProcesar == null || card.getTeam().name().equalsIgnoreCase(equipoAProcesar);

            if (debeActualizar && (card.getPhotoPath() == null || card.getPhotoPath().isEmpty())) {
                try {
                    String fotoUrl = buscarFotoJugador(equipoUrl, card.getName());

                    if (fotoUrl != null) {
                        String nombreImagen = generarNombreImagen(card.getName());
                        descargarImagen(fotoUrl, nombreImagen);
                        card.setPhotoPath("images/players/" + nombreImagen);
                        System.out.println("✅ Foto descargada para " + card.getName());
                    } else {
                        System.out.println("❌ No se encontró foto para: " + card.getName());
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error procesando jugador: " + card.getName() + " - " + e.getMessage());
                }
            } else if (!debeActualizar) {
                System.out.println("ℹ️ Saltando jugador de otro equipo: " + card.getName());
            } else {
                System.out.println("ℹ️ Ya tenía foto: " + card.getName());
            }
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputJsonPath), cartas);

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
        String nombreJugadorNormalizado = normalizarTexto(nombreJugador);
        Document doc = Jsoup.connect(equipoUrl).get();
        Elements filas = doc.select("table tbody tr");

        for (Element fila : filas) {
            Elements ths = fila.select("th");
            Elements tds = fila.select("td");

            if (ths.size() >= 2 && tds.size() >= 1) {
                Element thImagen = ths.get(1);
                Element img = thImagen.selectFirst("span[typeof=\"mw:File\"] a img");

                Element tdNombre = tds.get(0);
                Element linkNombre = tdNombre.selectFirst("a");
                String nombreEnTabla = linkNombre != null ? normalizarTexto(linkNombre.text()) : "";

                if (!nombreEnTabla.isEmpty() && textosParecidos(nombreJugadorNormalizado, nombreEnTabla)) {
                    if (img != null) {
                        String fotoUrl = img.hasAttr("data-src") ? img.attr("data-src") : img.attr("src");
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            if (!fotoUrl.startsWith("http")) {
                                fotoUrl = "https:" + fotoUrl;
                            }
                            System.out.println("✅ Encontrado " + nombreJugador + " en tabla");
                            return fotoUrl;
                        }
                    }
                }
            }
        }

        // Si no se encuentra en la tabla, buscar en la página individual del jugador
        System.out.println("🔎 Buscando página individual de: " + nombreJugador);
        Element linkJugador = doc.selectFirst("a[title=\"" + nombreJugador + "\"]");

        if (linkJugador != null) {
            String href = linkJugador.attr("href");
            if (href != null && !href.isEmpty()) {
                String urlJugador = BASE_URL + href;
                return extraerFotoDePaginaJugador(urlJugador);
            }
        }

        return null;
    }

    private String extraerFotoDePaginaJugador(String jugadorUrl) throws Exception {
        Document docJugador = Jsoup.connect(jugadorUrl).get();

        // Buscar la primera tabla
        Element primeraTabla = docJugador.selectFirst("table");

        if (primeraTabla != null) {
            Element imagen = primeraTabla.selectFirst("img");
            if (imagen != null) {
                String fotoUrl = imagen.hasAttr("data-src") ? imagen.attr("data-src") : imagen.attr("src");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    if (!fotoUrl.startsWith("http")) {
                        fotoUrl = "https:" + fotoUrl;
                    }
                    System.out.println("✅ Imagen encontrada en primera tabla de: " + jugadorUrl);
                    return fotoUrl;
                }
            }
        }

        System.out.println("❌ No se encontró imagen en la primera tabla de: " + jugadorUrl);
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
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Quitar acentos
                .replaceAll("[^a-zA-Z0-9 ]", "") // Quitar paréntesis, guiones, etc.
                .toLowerCase()
                .trim();
        return normalized;
    }

    private boolean textosParecidos(String texto1, String texto2) {
        return texto1.equals(texto2) || texto1.contains(texto2) || texto2.contains(texto1);
    }
}