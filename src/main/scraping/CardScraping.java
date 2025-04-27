package main.scraping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.HashMap;
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
        teamNameMapping.put("Brainwashing", "Instituto Brain");
        teamNameMapping.put("Otaku", "Instituto Otaku");
        teamNameMapping.put("Royal Academy", "Royal Academy");
        teamNameMapping.put("Shuriken", "Instituto Shuriken");
        teamNameMapping.put("Farm", "Instituto Farm");
        teamNameMapping.put("Kirkwood", "Instituto Kirkwood");
        teamNameMapping.put("Zeus", "Instituto Zeus");
        teamNameMapping.put("Raimon Old Boys", "Inazuma Eleven (Equipo)");
        teamNameMapping.put("Street Sally\u0027s", "Sallys");
        teamNameMapping.put("Inazuma KFC", "Inazuma Kids FC");
        teamNameMapping.put("Umbrella", "Instituto Umbrella");
    }

    private String equipoAProcesar = null;

    public void setEquipoAProcesar(String equipo) {
        this.equipoAProcesar = equipo;
    }

    public void actualizarCartasConFotos(String inputJsonPath, String outputJsonPath) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonArray cartasOriginales = parser.parse(new FileReader(inputJsonPath)).getAsJsonArray();

        Files.createDirectories(Path.of(IMAGE_FOLDER));
        Map<String, String> equipoUrls = obtenerUrlsEquipos();

        for (JsonElement elem : cartasOriginales) {
            JsonObject card = elem.getAsJsonObject();
            String teamName = card.has("team") && !card.get("team").isJsonNull() ? card.get("team").getAsString() : null;

            if (teamName != null && teamNameMapping.containsKey(teamName)) {
                boolean mismoEquipo = equipoAProcesar == null || teamName.equalsIgnoreCase(equipoAProcesar);

                if (mismoEquipo) {
                    String nombreJugador = card.get("name").getAsString();
                    String nombreEquipoWeb = teamNameMapping.get(teamName);
                    String equipoUrl = equipoUrls.get(nombreEquipoWeb);

                    if (equipoUrl == null) {
                        System.out.println("No se encontró URL para equipo: " + nombreEquipoWeb);
                        continue;
                    }

                    try {
                        String fotoUrl = buscarFotoJugador(equipoUrl, nombreJugador);

                        if (fotoUrl != null) {
                            String nombreImagen = generarNombreImagen(nombreJugador);
                            descargarImagen(fotoUrl, nombreImagen);
                            // 🔥 Aquí sí modificamos directamente el card ORIGINAL
                            card.addProperty("photoPath", "images/players/" + nombreImagen);
                            System.out.println("✅ Foto actualizada para " + nombreJugador);
                        } else {
                            System.out.println("❌ No se encontró foto para: " + nombreJugador);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Error procesando jugador: " + nombreJugador + " - " + e.getMessage());
                    }
                }
            }
        }

        // Guardamos TODO el array (cartasOriginales ya contiene las modificaciones hechas)
        try (FileWriter writer = new FileWriter(outputJsonPath)) {
            gson.toJson(cartasOriginales, writer);
        }
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
                            return fotoUrl;
                        }
                    }
                }
            }
        }

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
        Element primeraTabla = docJugador.selectFirst("table");

        if (primeraTabla != null) {
            Element imagen = primeraTabla.selectFirst("img");
            if (imagen != null) {
                String fotoUrl = imagen.hasAttr("data-src") ? imagen.attr("data-src") : imagen.attr("src");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    if (!fotoUrl.startsWith("http")) {
                        fotoUrl = "https:" + fotoUrl;
                    }
                    return fotoUrl;
                }
            }
        }

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
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .toLowerCase()
                .trim();
        return normalized;
    }

    private boolean textosParecidos(String texto1, String texto2) {
        return texto1.equals(texto2) || texto1.contains(texto2) || texto2.contains(texto1);
    }
}