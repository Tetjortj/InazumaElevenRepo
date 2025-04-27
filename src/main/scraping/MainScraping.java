package main.scraping;

public class MainScraping {
    public static void main(String[] args) throws Exception {
        CardScraping scraper = new CardScraping();

        // ❌ No seleccionamos equipo, porque queremos actualizar todos
        scraper.setEquipoAProcesar("Inazuma Eleven (equipo)"); // <- Esto es la clave: null = todos los equipos

        // ✅ Actualizamos todo el JSON
        scraper.actualizarCartasConFotos(
                "src/main/resources/jugadores_limpio.json",                // JSON de entrada
                "src/main/resources/jugadores_limpio.json"     // JSON de salida
        );

        System.out.println("✅ Proceso completado para TODOS los equipos.");
    }
}