package main;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Cargar formaciones disponibles
        FormationRepository formRepo = new FormationRepository();
        List<Formation> formacionesDisponibles = formRepo.getRandomFormations(3);

        System.out.println("Bienvenido al Inazuma Draft ⚡");
        System.out.println("Escoge una formación:");

        for (int i = 0; i < formacionesDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + formacionesDisponibles.get(i).getName());
        }

        int eleccionFormacion = -1;
        while (eleccionFormacion < 1 || eleccionFormacion > formacionesDisponibles.size()) {
            System.out.print("Introduce el número de la formación elegida (1-" + formacionesDisponibles.size() + "): ");
            try {
                eleccionFormacion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ignored) {}
        }

        Formation formacion = formacionesDisponibles.get(eleccionFormacion - 1);

        // Cargar cartas desde JSON
        CardLoader loader = new CardLoader();
        List<Card> todasLasCartas = loader.loadCards();

        PlayerPool pool = new PlayerPool(todasLasCartas);
        FormationDisplayer displayer = new FormationDisplayer();
        Map<Integer, Card> jugadoresSeleccionados = new HashMap<>();

        while (jugadoresSeleccionados.size() < formacion.getPlacements().size()) {
            System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
            displayer.showFormation(formacion, jugadoresSeleccionados);

            // Mostrar puntuación media del equipo actual
            if (!jugadoresSeleccionados.isEmpty()) {
                int sumaPuntajes = jugadoresSeleccionados.values().stream().mapToInt(Card::calcularScore).sum();
                int mediaEquipo = Math.round((float) sumaPuntajes / jugadoresSeleccionados.size());
                System.out.println("Puntuación media del equipo actual: " + mediaEquipo);

                // Cálculo de química
                Map<Integer, List<Integer>> links = formacion.getLinks();
                int enlacesTotales = 0;
                float quimicaActual = 0;

                for (Map.Entry<Integer, List<Integer>> entry : links.entrySet()) {
                    int from = entry.getKey();
                    for (int to : entry.getValue()) {
                        if (from < to && jugadoresSeleccionados.containsKey(from) && jugadoresSeleccionados.containsKey(to)) {
                            enlacesTotales++;
                            Card a = jugadoresSeleccionados.get(from);
                            Card b = jugadoresSeleccionados.get(to);

                            if (a.getTeam() == b.getTeam()) {
                                quimicaActual += 1.0;
                            } else if (a.getElement() == b.getElement()) {
                                quimicaActual += 0.6;
                            } else {
                                quimicaActual += 0.25;
                            }
                        }
                    }
                }

                int quimicaFinal = enlacesTotales > 0 ? Math.round((quimicaActual / enlacesTotales) * 100) : 0;
                System.out.println("Química del equipo actual: " + quimicaFinal);
            }

            // Mostrar posiciones aún no elegidas
            List<Integer> posicionesPendientes = new ArrayList<>();
            for (int i = 0; i < formacion.getPlacements().size(); i++) {
                if (!jugadoresSeleccionados.containsKey(i)) {
                    posicionesPendientes.add(i);
                }
            }

            System.out.println("Elige una posición a revelar:");
            for (int idx : posicionesPendientes) {
                Position pos = formacion.getPlacements().get(idx).getPosition();
                System.out.println(idx + ": " + pos);
            }

            int indiceElegido = -1;
            while (!posicionesPendientes.contains(indiceElegido)) {
                System.out.print("Introduce el índice de la posición a revelar: ");
                try {
                    indiceElegido = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ignored) {}
            }

            Position posicionARevelar = formacion.getPlacements().get(indiceElegido).getPosition();
            List<Card> cartasDisponibles = new ArrayList<>(pool.getByPosition(posicionARevelar));
            Collections.shuffle(cartasDisponibles);

            List<Card> opciones = cartasDisponibles.stream().limit(5).toList();

            System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
            System.out.println("Opciones para posición " + posicionARevelar + ":");
            for (int i = 0; i < opciones.size(); i++) {
                Card c = opciones.get(i);
                System.out.printf("%d. %s | %s | Score: %d | %s%n",
                        i + 1, c.getName(), c.getTeam(), c.calcularScore(), c.getElement());
            }

            int eleccion = -1;
            while (eleccion < 1 || eleccion > 5) {
                System.out.print("Elige una carta (1-5): ");
                try {
                    eleccion = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ignored) {}
            }

            Card cartaElegida = opciones.get(eleccion - 1);
            jugadoresSeleccionados.put(indiceElegido, cartaElegida);

            System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
        }

        // Draft completado
        displayer.showFormation(formacion, jugadoresSeleccionados);

        int sumaPuntajes = jugadoresSeleccionados.values().stream().mapToInt(Card::calcularScore).sum();
        int mediaEquipo = Math.round((float) sumaPuntajes / jugadoresSeleccionados.size());
        System.out.println("Puntuación media final del equipo: " + mediaEquipo);

        Map<Integer, List<Integer>> links = formacion.getLinks();
        int enlacesTotales = 0;
        float quimicaActual = 0;

        for (Map.Entry<Integer, List<Integer>> entry : links.entrySet()) {
            int from = entry.getKey();
            for (int to : entry.getValue()) {
                if (from < to && jugadoresSeleccionados.containsKey(from) && jugadoresSeleccionados.containsKey(to)) {
                    enlacesTotales++;
                    Card a = jugadoresSeleccionados.get(from);
                    Card b = jugadoresSeleccionados.get(to);

                    if (a.getTeam() == b.getTeam()) {
                        quimicaActual += 1.0;
                    } else if (a.getElement() == b.getElement()) {
                        quimicaActual += 0.6;
                    } else {
                        quimicaActual += 0.25;
                    }
                }
            }
        }

        int quimicaFinal = enlacesTotales > 0 ? Math.round((quimicaActual / enlacesTotales) * 100) : 0;
        System.out.println("Química final del equipo: " + quimicaFinal);
        System.out.println("¡Has completado tu draft!");
        System.out.println("Puntuación final: " + mediaEquipo + ", Química: " + quimicaFinal);
    }
}

