package main.modifyJson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Card;
import main.Grade;

import java.io.File;
import java.util.*;

public class GradeUpdater {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String rutaJSON = "src/main/resources/jugadores.json";
        //String equipoObjetivo = "Occult"; // CAMBIA AQUÍ el equipo deseado

        try {
            ObjectMapper mapper = new ObjectMapper();

            List<Card> jugadores = mapper.readValue(new File(rutaJSON), new TypeReference<>() {});
            //System.out.println("Asignando grado a jugadores del equipo: " + equipoObjetivo + "\n");

            for (Card jugador : jugadores) {
                if (!jugador.getTeam().name().equalsIgnoreCase("Connection_Map") &&
                        !jugador.getTeam().name().equalsIgnoreCase("Scouting") &&
                        !jugador.getTeam().name().equalsIgnoreCase("Raimon")) {
                    System.out.println("Jugador: " + jugador.getName() + ", Equipo: " + jugador.getTeam());
                    System.out.println("¿En qué grado está?");
                    System.out.println("1 - FIRST_YEAR");
                    System.out.println("2 - SECOND_YEAR");
                    System.out.println("3 - THIRD_YEAR");
                    System.out.println("4 - ADULT");
                    System.out.println("5 - UNKNOWN");

                    int eleccion = -1;
                    while (eleccion < 1 || eleccion > 5) {
                        System.out.print("Introduce el número del grado (1-5): ");
                        try {
                            eleccion = Integer.parseInt(scanner.nextLine().trim());
                        } catch (NumberFormatException ignored) {}
                    }

                    Grade nuevoGrade = switch (eleccion) {
                        case 1 -> Grade.FIRST_YEAR;
                        case 2 -> Grade.SECOND_YEAR;
                        case 3 -> Grade.THIRD_YEAR;
                        case 4 -> Grade.ADULT;
                        case 5 -> Grade.UNKNOWN;
                        default -> Grade.UNKNOWN;
                    };

                    jugador.setGrade(nuevoGrade);
                    System.out.println("Grado asignado: " + nuevoGrade + "\n");
                }
            }

            // Guardar cambios
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaJSON), jugadores);
            //System.out.println("¡Grados actualizados correctamente para el equipo " + equipoObjetivo + "!");

        } catch (Exception e) {
            System.err.println("Error al actualizar grados: " + e.getMessage());
        }
    }
}

