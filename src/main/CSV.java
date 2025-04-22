package main;

import java.util.List;

import java.util.*;

public class CSV {
    public static void main(String[] args) {
        List<PlayerStatsCSV> jugadores = CSVReader.leerJugadores("resources/Bdd.txt");

        Set<String> equiposUnicos = new HashSet<>();

        for (PlayerStatsCSV j : jugadores) {
            equiposUnicos.add(j.getTeam());
        }

        System.out.println("\nTotal de equipos distintos: " + equiposUnicos.size());
        System.out.println("Equipos encontrados:");
        for (String equipo : equiposUnicos) {
            System.out.println("- " + equipo);
        }
    }
}

