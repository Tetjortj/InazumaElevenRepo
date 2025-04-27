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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URL;
import java.util.*;

public class CardStatsAnalyzer {

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Card> cards = mapper.readValue(new File("src/main/resources/jugadores_limpio.json"), new TypeReference<>() {});

            mostrarConteoPorEquipoYFotosReales(cards);

        } catch (Exception e) {
            System.err.println("\u274c Error al analizar las cartas: " + e.getMessage());
        }
    }

    private static void mostrarConteoPorEquipoYFotosReales(List<Card> cards) {
        Map<Team, List<Card>> jugadoresPorEquipo = new TreeMap<>();

        for (Card c : cards) {
            jugadoresPorEquipo
                    .computeIfAbsent(c.getTeam(), k -> new ArrayList<>())
                    .add(c);
        }

        int totalJugadores = 0;
        int totalConFotoReal = 0;

        System.out.println("\n📊 Estado de fotos reales por equipo:\n");

        for (Map.Entry<Team, List<Card>> entry : jugadoresPorEquipo.entrySet()) {
            Team equipo = entry.getKey();
            List<Card> jugadores = entry.getValue();

            int conFotoReal = 0;
            for (Card c : jugadores) {
                if (tieneFotoReal(c)) {
                    conFotoReal++;
                }
            }

            System.out.printf("- %-20s: %d jugadores, %d con foto REAL %s%n",
                    equipo.name(),
                    jugadores.size(),
                    conFotoReal,
                    (conFotoReal == jugadores.size() ? "✅" : "❌")
            );

            totalJugadores += jugadores.size();
            totalConFotoReal += conFotoReal;
        }

        System.out.println("\n🧮 Total jugadores: " + totalJugadores);
        System.out.println("🖼️ Total jugadores con foto REAL: " + totalConFotoReal);
    }

    private static boolean tieneFotoReal(Card card) {
        if (card.getPhotoPath() == null || card.getPhotoPath().isEmpty()) {
            return false;
        }
        try {
            // Intentar cargar el recurso real
            URL resource = CardStatsAnalyzer.class.getClassLoader().getResource("images/players/" + card.getPhotoPath());
            return resource != null;
        } catch (Exception e) {
            return false;
        }
    }
}