package main.scraping;

public class MainScraping {
    public static void main(String[] args) throws Exception {
        CardScraping scraper = new CardScraping();

        // ✅ Aquí indicas qué equipo quieres procesar
        scraper.setEquipoAProcesar("Occult"); // Ejemplo: solo jugadores del Instituto Raimon

        // ✅ Aquí le pasas las rutas correctas (dentro de /resources)
        scraper.actualizarCartasConFotos(
                "src/main/resources/jugadores.json",                 // entrada
                "src/main/resources/jugadores_actualizados.json"     // salida
        );
    }
}