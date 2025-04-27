package main.modifyJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.Card;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class JSONPhotoPathFixer {

    public static void main(String[] args) throws Exception {
        String inputJsonPath = "src/main/resources/jugadores_actualizados.json"; // Cambia esta ruta si hace falta
        String outputJsonPath = "src/main/resources/jugadores_actualizados.json"; // Puedes sobrescribir el mismo si quieres

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Card>>() {}.getType();

        List<Card> cartas = gson.fromJson(new FileReader(Path.of(inputJsonPath).toFile()), listType);

        for (Card card : cartas) {
            if (card.getPhotoPath() == null) {
                card.setPhotoPath("");
            }
        }

        try (FileWriter writer = new FileWriter(outputJsonPath)) {
            gson.toJson(cartas, writer);
        }

        System.out.println("✅ JSON corregido: todos los jugadores tienen 'photoPath'.");
    }
}
