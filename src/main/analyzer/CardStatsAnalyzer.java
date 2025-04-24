package main.analyzer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Card;
import main.Team;

import java.io.File;
import java.util.*;

public class CardStatsAnalyzer {

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Card> cards = mapper.readValue(new File("src/main/resources/jugadores.json"), new TypeReference<List<Card>>() {});

            // Agrupar jugadores por equipo
            Map<Team, List<Card>> jugadoresPorEquipo = new TreeMap<>();

            for (Card c : cards) {
                jugadoresPorEquipo
                        .computeIfAbsent(c.getTeam(), k -> new ArrayList<>())
                        .add(c);
            }

            System.out.println("📋 Jugadores agrupados por equipo con media ponderada:\n");

            for (Map.Entry<Team, List<Card>> entry : jugadoresPorEquipo.entrySet()) {
                Team equipo = entry.getKey();
                List<Card> jugadores = entry.getValue();

                // Ordenar por media ponderada descendente
                jugadores.sort((a, b) -> Integer.compare(b.calcularScore(), a.calcularScore()));

                System.out.println("🟦 " + equipo.name());

                for (Card c : jugadores) {
                    System.out.printf("  - %-20s (%d)%n", c.getName(), c.calcularScore());
                }

                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("❌ Error al analizar las cartas: " + e.getMessage());
        }
    }
}