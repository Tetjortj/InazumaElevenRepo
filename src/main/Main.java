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
                System.out.printf("%d. %s | %s | %d | %s%n", i + 1, c.getName(), c.getTeam(), c.getRating(), c.getElement());
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
        System.out.println("¡Has completado tu draft!");
    }
}