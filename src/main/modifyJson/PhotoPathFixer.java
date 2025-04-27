package main.modifyJson;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class PhotoPathFixer {

    public static void main(String[] args) {
        try {
            limpiarRutasFotos("src/main/resources/jugadores_limpio.json", "src/main/resources/jugadores_limpio.json");
            System.out.println("✅ JSON procesado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void limpiarRutasFotos(String inputJsonPath, String outputJsonPath) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray cartas;
        try (FileReader reader = new FileReader(inputJsonPath)) {
            cartas = JsonParser.parseReader(reader).getAsJsonArray();
        }

        for (JsonElement elem : cartas) {
            JsonObject card = elem.getAsJsonObject();

            if (card.has("photoPath") && !card.get("photoPath").isJsonNull()) {
                String originalPath = card.get("photoPath").getAsString();
                if (!originalPath.isEmpty()) {
                    // Solo dejar el nombre del archivo
                    File f = new File(originalPath);
                    String nuevoPath = f.getName(); // ej: "ingram.jpg"
                    card.addProperty("photoPath", nuevoPath);
                }
            }
        }

        // Nos aseguramos que el directorio de salida existe
        Files.createDirectories(Path.of(new File(outputJsonPath).getParent()));

        try (FileWriter writer = new FileWriter(outputJsonPath)) {
            gson.toJson(cartas, writer);
        }
    }
}