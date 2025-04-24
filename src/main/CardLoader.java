package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CardLoader {

    public List<Card> loadCards() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("jugadores.json");

            if (is == null) {
                System.out.println("Archivo jugadores.json no encontrado en resources.");
                return Collections.emptyList();
            }

            List<Card> allCards = mapper.readValue(is, new TypeReference<List<Card>>() {});

            // Filtrar jugadores de Connection Map y Scouting
            return allCards.stream()
                    .filter(card -> !(card.getTeam() == Team.Conection_Map || card.getTeam() == Team.Scouting))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Error al cargar los jugadores: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

