package main;

import java.util.*;

public class FormationDisplayer {
    private final int alturaCarta = 4;
    private final int espacioVertical = 2;
    private final int anchoCarta = 13;
    private final int columnasVisualMin = 10;

    public void showFormation(Formation formation, Map<Integer, Card> jugadoresSeleccionados, List<Card> banquillo) {
        List<PlayerPlacement> placements = formation.getPlacements();

        int minFila = placements.stream().mapToInt(PlayerPlacement::getFila).min().orElse(0);
        int maxFila = placements.stream().mapToInt(PlayerPlacement::getFila).max().orElse(0);
        int minCol = placements.stream().mapToInt(PlayerPlacement::getColumna).min().orElse(0);
        int maxCol = placements.stream().mapToInt(PlayerPlacement::getColumna).max().orElse(0);

        int filasLogicas = maxFila - minFila + 1;
        int columnasLogicas = maxCol - minCol + 1;
        int columnasVisuales = Math.max(columnasLogicas, columnasVisualMin);

        int filasReales = filasLogicas * alturaCarta + (filasLogicas - 1) * espacioVertical;
        int columnasReales = columnasVisuales;

        String[][] campo = new String[filasReales][columnasReales];
        int offsetCol = 0;

        for (int i = 0; i < placements.size(); i++) {
            PlayerPlacement p = placements.get(i);
            int filaPantalla = (p.getFila() - minFila) * (alturaCarta + espacioVertical);
            int colPantalla = (p.getColumna() - minCol + offsetCol);

            if (jugadoresSeleccionados.containsKey(i)) {
                Card c = jugadoresSeleccionados.get(i);
                campo[filaPantalla + 0][colPantalla] = "[" + abreviar(c.getName(), 11) + "]";
                campo[filaPantalla + 1][colPantalla] = "[" + abreviar(c.getTeam().name(), 11) + "]";

                String punGra = String.format("P:%2d G:%s", c.calcularScore(), c.getGrade().toStringCorrect());
                campo[filaPantalla + 2][colPantalla] = "[" + abreviar(punGra, 11) + "]";

                String elemPos = String.format("%s | %s", c.getElement().name(), c.getPosition().name());
                campo[filaPantalla + 3][colPantalla] = "[" + abreviar(elemPos, 11) + "]";
            } else {
                String tipo = p.getPosition().name() + "-" + i;
                campo[filaPantalla + 0][colPantalla] = "[" + " ".repeat(11) + "]";
                campo[filaPantalla + 1][colPantalla] = "[" + centrar(tipo, 11) + "]";
                campo[filaPantalla + 2][colPantalla] = "[" + " ".repeat(11) + "]";
                campo[filaPantalla + 3][colPantalla] = "[" + " ".repeat(10) + i + "]";
            }

        }

        // Mostrar campo
        System.out.println("Formación: " + formation.getName());
        System.out.println("─".repeat(columnasVisuales * anchoCarta));

        for (int f = 0; f < filasReales; f++) {
            for (int c = 0; c < columnasReales; c++) {
                System.out.print(campo[f][c] == null ? " ".repeat(anchoCarta) : campo[f][c]);
            }
            System.out.println();
        }

        System.out.println("─".repeat(columnasVisuales * anchoCarta));
        System.out.println("Enlaces entre jugadores:");

        Map<Integer, List<Integer>> links = formation.getLinks();
        int numColumnas = 4;
        int jugadoresPorColumna = (int) Math.ceil(placements.size() / (double) numColumnas);
        List<List<String>> columnas = new ArrayList<>();
        for (int i = 0; i < numColumnas; i++) columnas.add(new ArrayList<>());

        for (int i = 0; i < placements.size(); i++) {
            Position fromPos = placements.get(i).getPosition();
            String encabezado = fromPos.name() + "-" + i;
            int columna = i / jugadoresPorColumna;
            List<String> bloque = columnas.get(columna);
            bloque.add(encabezado);

            for (int destino : links.getOrDefault(i, List.of())) {
                Position toPos = placements.get(destino).getPosition();
                String enlace = fromPos.name() + "-" + i + " --- " + toPos.name() + "-" + destino;
                String raw = "  " + enlace;
                String padded = String.format("%-30s", raw);
                String coloreado = padded;

                if (jugadoresSeleccionados.containsKey(i) && jugadoresSeleccionados.containsKey(destino)) {
                    Card a = jugadoresSeleccionados.get(i);
                    Card b = jugadoresSeleccionados.get(destino);

                    if (a.getTeam() == b.getTeam() || (a.getElement() == b.getElement() && a.getGrade() == b.getGrade())) {
                        coloreado = "\u001B[32m" + padded + "\u001B[0m"; // VERDE
                    } else if (
                            (a.getElement() == b.getElement() && a.getGrade() != b.getGrade()) ||
                                    (a.getElement() != b.getElement() && a.getGrade() == b.getGrade())) {
                        coloreado = "\u001B[33m" + padded + "\u001B[0m"; // AMARILLO
                    } else {
                        coloreado = "\u001B[31m" + padded + "\u001B[0m"; // ROJO
                    }
                }
                bloque.add(coloreado);
            }
            bloque.add("");
        }

        int alturaMax = columnas.stream().mapToInt(List::size).max().orElse(0);

        for (int fila = 0; fila < alturaMax; fila++) {
            for (List<String> col : columnas) {
                if (fila < col.size()) {
                    System.out.print(String.format("%-30s", col.get(fila)));
                } else {
                    System.out.print(" ".repeat(30));
                }
            }
            System.out.println();
        }

        System.out.println("─".repeat(columnasVisuales * anchoCarta));

        if (!banquillo.isEmpty()) {
            System.out.println("Banquillo:");
            for (int i = 0; i < banquillo.size(); i++) {
                Card c = banquillo.get(i);
                System.out.printf("Suplente %d: %s | %s | Score: %d | %s%n",
                        i + 1, c.getName(), c.getTeam(), c.calcularScore(), c.getElement());
            }
        }
    }

    private String abreviar(String texto, int max) {
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : String.format("%-" + max + "s", texto);
    }

    private String centrar(String texto, int ancho) {
        int espacios = ancho - texto.length();
        int izquierda = espacios / 2;
        int derecha = espacios - izquierda;
        return " ".repeat(Math.max(0, izquierda)) + texto + " ".repeat(Math.max(0, derecha));
    }
}