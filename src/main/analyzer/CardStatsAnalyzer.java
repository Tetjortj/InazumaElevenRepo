package main.analyzer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Card;
import main.Team;

import java.io.File;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class CardStatsAnalyzer {

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Card> cards = mapper.readValue(new File("src/main/resources/jugadores.json"), new TypeReference<>() {});

            //mostrarJugadoresPorEquipoConMedia(cards);
            mostrarConteoPorEquipo(cards);

        } catch (Exception e) {
            System.err.println("\u274c Error al analizar las cartas: " + e.getMessage());
        }
    }

    private static void mostrarJugadoresPorEquipoConMedia(List<Card> cards) {
        Map<Team, List<Card>> jugadoresPorEquipo = new TreeMap<>();

        for (Card c : cards) {
            jugadoresPorEquipo
                    .computeIfAbsent(c.getTeam(), k -> new ArrayList<>())
                    .add(c);
        }

        System.out.println("\uD83D\uDCCB Jugadores agrupados por equipo con media ponderada:\n");

        for (Map.Entry<Team, List<Card>> entry : jugadoresPorEquipo.entrySet()) {
            Team equipo = entry.getKey();
            List<Card> jugadores = entry.getValue();

            jugadores.sort((a, b) -> Integer.compare(b.calcularScore(), a.calcularScore()));

            System.out.println("\uD83D\uDDFE " + equipo.name());
            for (Card c : jugadores) {
                System.out.printf("  - %-20s (%d)%n", c.getName(), c.calcularScore());
            }
            System.out.println();
        }
    }

    private static void mostrarConteoPorEquipo(List<Card> cards) {
        Map<Team, Integer> conteo = new TreeMap<>();

        for (Card c : cards) {
            conteo.put(c.getTeam(), conteo.getOrDefault(c.getTeam(), 0) + 1);
        }

        int total = 0;
        System.out.println("\n\uD83D\uDCCA Cantidad de jugadores por equipo:");
        for (Map.Entry<Team, Integer> entry : conteo.entrySet()) {
            System.out.printf("- %-20s: %d%n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("\n\uD83D\uDCC5 Total de jugadores: " + total);
    }
}
