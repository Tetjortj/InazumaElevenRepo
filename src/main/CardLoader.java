package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class CardLoader {

    public List<Card> loadCards() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/jugadores.json");

            if (is == null) {
                System.out.println("Archivo players.json no encontrado en resources.");
                return Collections.emptyList();
            }

            return mapper.readValue(is, new TypeReference<List<Card>>() {});
        } catch (Exception e) {
            System.out.println("Error al cargar los jugadores: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
