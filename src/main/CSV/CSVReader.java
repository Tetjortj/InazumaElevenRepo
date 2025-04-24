package main.CSV;

import java.io.*;
import java.util.*;

public class CSVReader {
    public static List<PlayerStatsCSV> leerJugadores(String archivo) {
        List<PlayerStatsCSV> lista = new ArrayList<>();

        try (
                InputStream is = CSVReader.class.getClassLoader().getResourceAsStream(archivo);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String linea;
            boolean primera = true;

            while ((linea = reader.readLine()) != null) {
                if (primera) { // Saltar cabecera
                    primera = false;
                    continue;
                }

                String[] partes = linea.split(",");

                // Asegúrate que el formato coincide
                PlayerStatsCSV jugador = new PlayerStatsCSV(
                        partes[0], partes[1], partes[2], partes[3],
                        Integer.parseInt(partes[4]), Integer.parseInt(partes[5]), Integer.parseInt(partes[6]), Integer.parseInt(partes[7]),
                        Integer.parseInt(partes[8]), Integer.parseInt(partes[9]), Integer.parseInt(partes[10]), Integer.parseInt(partes[11]),
                        Integer.parseInt(partes[12]),
                        partes[13], partes[14], partes[15], partes[16]
                );


                lista.add(jugador);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

}

