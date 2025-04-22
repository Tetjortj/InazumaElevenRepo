package main;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CSVToJsonConverter {

    public static void main(String[] args) {
        String rutaCSV = "resources/Bdd.txt"; // nombre del archivo con datos CSV
        String rutaJSON = "resources/jugadores.json"; // archivo de salida en formato JSON

        List<PlayerStatsCSV> jugadores = leerJugadoresDesdeCSV(rutaCSV);
        guardarComoJSON(jugadores, rutaJSON);
        System.out.println("Se ha generado el archivo JSON con " + jugadores.size() + " jugadores.");
    }

    public static List<PlayerStatsCSV> leerJugadoresDesdeCSV(String ruta) {
        List<PlayerStatsCSV> jugadores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            br.readLine(); // saltar la cabecera

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", -1); // dividir con todos los campos
                if (partes.length < 17) continue; // asegurarse de que hay suficientes campos

                PlayerStatsCSV jugador = new PlayerStatsCSV(
                        partes[0], partes[1], partes[2], partes[3],
                        Integer.parseInt(partes[4]), Integer.parseInt(partes[5]),
                        Integer.parseInt(partes[6]), Integer.parseInt(partes[7]),
                        Integer.parseInt(partes[8]), Integer.parseInt(partes[9]),
                        Integer.parseInt(partes[10]), Integer.parseInt(partes[11]),
                        Integer.parseInt(partes[12]),
                        partes[13], partes[14], partes[15], partes[16]
                );

                jugadores.add(jugador);
            }

        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }

        return jugadores;
    }

    public static void guardarComoJSON(List<PlayerStatsCSV> lista, String archivoDestino) {
        try (Writer writer = new FileWriter(archivoDestino)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.err.println("Error escribiendo el JSON: " + e.getMessage());
        }
    }
}

