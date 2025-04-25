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
        List<Card> banquillo = new ArrayList<>();

        while (jugadoresSeleccionados.size() < formacion.getPlacements().size()) {
            System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
            displayer.showFormation(formacion, jugadoresSeleccionados, banquillo);

            mostrarStatsEquipo(formacion, jugadoresSeleccionados);

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
                System.out.printf("%d. %s | %s | Score: %d | Grade: %s | %s%n",
                        i + 1, c.getName(), c.getTeam(), c.calcularScore(), c.getGrade().toStringCorrect(), c.getElement());
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
        }

        // Mostrar formación y stats antes del banquillo
        displayer.showFormation(formacion, jugadoresSeleccionados, banquillo);
        mostrarStatsEquipo(formacion, jugadoresSeleccionados);

        // Selección del banquillo
        System.out.println("\nSelecciona tus 5 jugadores del banquillo:");
        while (banquillo.size() < 5) {
            List<Card> opciones = new ArrayList<>(todasLasCartas);
            Collections.shuffle(opciones);
            opciones = opciones.subList(0, 5);

            for (int i = 0; i < opciones.size(); i++) {
                Card c = opciones.get(i);
                System.out.printf("%d. %s | %s | Score: %d | Grade: %s | %s%n",
                        i + 1, c.getName(), c.getTeam(), c.calcularScore(), c.getGrade().toStringCorrect(), c.getElement());
            }

            int eleccion = -1;
            while (eleccion < 1 || eleccion > 5) {
                System.out.print("Elige una carta para el banquillo (1-5): ");
                try {
                    eleccion = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ignored) {}
            }

            System.out.println();
            banquillo.add(opciones.get(eleccion - 1));
            System.out.println("Jugador añadido al banquillo. " + (5 - banquillo.size()) + " espacios restantes.");
        }

        // Final del draft
        boolean quiereIntercambiar = true;
        while (quiereIntercambiar) {
            displayer.showFormation(formacion, jugadoresSeleccionados, banquillo);
            System.out.println();
            mostrarStatsEquipo(formacion, jugadoresSeleccionados);
            System.out.println();

            System.out.print("¿Deseas intercambiar dos jugadores? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (!respuesta.equals("s")) break;

            System.out.print("Introduce el índice del primer jugador a intercambiar: ");
            int i1 = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Introduce el índice del segundo jugador a intercambiar: ");
            int i2 = Integer.parseInt(scanner.nextLine().trim());

            if (jugadoresSeleccionados.containsKey(i1) && jugadoresSeleccionados.containsKey(i2)) {
                Card temp = jugadoresSeleccionados.get(i1);
                jugadoresSeleccionados.put(i1, jugadoresSeleccionados.get(i2));
                jugadoresSeleccionados.put(i2, temp);
                System.out.println("Intercambio realizado.");
            } else {
                System.out.println("Uno de los índices no tiene jugador asignado.");
            }
        }

        // Final
        displayer.showFormation(formacion, jugadoresSeleccionados, banquillo);
        mostrarStatsEquipo(formacion, jugadoresSeleccionados);
        System.out.println("¡Has completado tu draft!");

        int finalScore = jugadoresSeleccionados.values().stream().mapToInt(Card::calcularScore).sum() / jugadoresSeleccionados.size();
        int quimicaFinal = calcularQuimica(formacion, jugadoresSeleccionados);

        System.out.println();
        System.out.println("Puntuación final: " + (finalScore + quimicaFinal));
    }

    private static void mostrarStatsEquipo(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
        int sumaPuntajes = jugadoresSeleccionados.values().stream().mapToInt(Card::calcularScore).sum();
        int mediaEquipo = Math.round((float) sumaPuntajes / jugadoresSeleccionados.size());
        System.out.println("Puntuación media del equipo actual: " + mediaEquipo);

        int quimicaFinal = calcularQuimica(formacion, jugadoresSeleccionados);
        System.out.println("Química del equipo actual: " + quimicaFinal);
    }

    private static int calcularQuimica(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
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

                    if (a.getTeam() == b.getTeam() || (a.getElement() == b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 1.0;
                    } else if ((a.getElement() == b.getElement() && a.getGrade() != b.getGrade()) ||
                            (a.getElement() != b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 0.5;
                    } else {
                        quimicaActual += 0.25;
                    }
                }
            }
        }
        return enlacesTotales > 0 ? Math.round((quimicaActual / enlacesTotales) * 100) : 0;
    }
}