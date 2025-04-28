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
            List<Card> withoutScoutCon = eliminarScoutConnecting(cards);

            //contarPorAtributo(cards);
            //mostrarConteoPorEquipoYFotosReales(cards);
            //contarCombinacionesCartas(withoutScoutCon);
            contarJugadoresPorScore(withoutScoutCon);

        } catch (Exception e) {
            System.err.println("\u274c Error al analizar las cartas: " + e.getMessage());
        }
    }

    private static List<Card> eliminarScoutConnecting(List<Card> cards) {
        List<Card> cartasSeleccionadas = new ArrayList<>();
        for (Card card : cards) {
            if(!(card.getTeam().name().equals("Conection_Map") || card.getTeam().name().equals("Scouting"))) {
                cartasSeleccionadas.add(card);
            }
        }
        return cartasSeleccionadas;
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

    private static void contarPorAtributo(List<Card> cards) {
        int earth = 0, fire = 0, wood = 0, wind = 0;
        int first = 0, second = 0, third = 0, adult = 0;
        int gk = 0, df = 0, mf = 0, fw = 0;

        for (Card c : cards) {
            if(!(c.getTeam().name().equals("Conection_Map") || c.getTeam().name().equals("Scouting"))) {
                switch (c.getPosition()) {
                    case GK:
                        gk++;
                        break;
                    case DF:
                        df++;
                        break;
                    case MF:
                        mf++;
                        break;
                    case FW:
                        fw++;
                        break;
                }

                switch (c.getElement()) {
                    case Earth:
                        earth++;
                        break;
                    case Fire:
                        fire++;
                        break;
                    case Wind:
                        wind++;
                        break;
                    case Wood:
                        wood++;
                        break;
                }

                switch (c.getGrade()) {
                    case FIRST_YEAR:
                        first++;
                        break;
                    case SECOND_YEAR:
                        second++;
                        break;
                    case THIRD_YEAR:
                        third++;
                        break;
                    case ADULT:
                        adult++;
                        break;
                }
            }
        }

        System.out.println("Earth: " + earth);
        System.out.println("Fire: " + fire);
        System.out.println("Wood: " + wood);
        System.out.println("Wind: " + wind);

        System.out.println("First: " + first);
        System.out.println("Second: " + second);
        System.out.println("Third: " + third);
        System.out.println("Adult: " + adult);

        System.out.println("GK: " + gk);
        System.out.println("DF: " + df);
        System.out.println("MF: " + mf);
        System.out.println("FW: " + fw);
    }

    private static void contarCombinacionesCartas(List<Card> cartas) {
        Map<String, Integer> combinaciones = new HashMap<>();

        for (Card carta : cartas) {
            String clave = carta.getPosition().name() + "-" +
                    carta.getElement().name() + "-" +
                    carta.getGrade().name();

            combinaciones.put(clave, combinaciones.getOrDefault(clave, 0) + 1);
        }

        // Mostrar resultados
        for (Map.Entry<String, Integer> entry : combinaciones.entrySet()) {
            System.out.println("Combinación: " + entry.getKey() + " -> " + entry.getValue() + " cartas");
        }
    }

    private static void contarJugadoresPorScore(List<Card> cartas) {
        Map<Integer, Integer> scoreContador = new HashMap<>();

        for (Card carta : cartas) {
            int score = (int) carta.getScore(); // Aseguramos que sea entero

            scoreContador.put(score, scoreContador.getOrDefault(score, 0) + 1);
        }

        // Mostrar resultados
        for (Map.Entry<Integer, Integer> entry : scoreContador.entrySet()) {
            System.out.println("Score: " + entry.getKey() + " -> " + entry.getValue() + " jugadores");
        }
    }

}