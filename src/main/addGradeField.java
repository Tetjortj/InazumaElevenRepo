package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class addGradeField {

    public static void main(String[] args) {
        String rutaJSON = "src/main/resources/jugadores.json";

        try {
            ObjectMapper mapper = new ObjectMapper();

            // Leer todos los jugadores desde el JSON
            List<Card> jugadores = mapper.readValue(new File(rutaJSON), new TypeReference<>() {});

            // Asignar FIRST_YEAR como valor por defecto a todos
            for (Card jugador : jugadores) {
                if (jugador.getGrade() == null) {
                    jugador.setGrade(Grade.FIRST_YEAR);
                }
            }

            // Sobrescribir el archivo con la nueva estructura
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaJSON), jugadores);
            System.out.println("Todos los jugadores actualizados con el campo 'grade' como FIRST_YEAR.");
        } catch (Exception e) {
            System.err.println("Error al modificar el JSON: " + e.getMessage());
        }
    }
}