package main;

import java.util.*;

public class FormationRepository {
    private final List<Formation> allFormations = new ArrayList<>();

    public FormationRepository() {
        allFormations.add(createDiamante());
        allFormations.add(createDobleW());
        allFormations.add(createAutobus());
        allFormations.add(createFlechaEspectral());
        allFormations.add(createJungla());
        allFormations.add(createReja());
        /*
        allFormations.add(createPiramide());
        allFormations.add(createZonaMuerta());
        allFormations.add(createAlasGrulla());
        allFormations.add(createArbolNavidad());
        allFormations.add(createAtaqueTrillizo());
        allFormations.add(createAncla());
        allFormations.add(createDobleM());
        allFormations.add(createPala());
        allFormations.add(createVueloFenix());
        allFormations.add(createPuertaCielo());
         */
    }

    public List<Formation> getRandomFormations(int n) {
        Collections.shuffle(allFormations);
        return allFormations.subList(0, Math.min(n, allFormations.size()));
    }

    public Formation createDiamante() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 2), //0

                new PlayerPlacement(Position.DF, 2, 0), //1
                new PlayerPlacement(Position.DF, 1, 1), //2
                new PlayerPlacement(Position.DF, 1, 3), //3
                new PlayerPlacement(Position.DF, 2, 4), //4

                new PlayerPlacement(Position.MF, 4, 0), //5
                new PlayerPlacement(Position.MF, 3, 1), //6
                new PlayerPlacement(Position.MF, 3, 3), //7
                new PlayerPlacement(Position.MF, 4, 4), //8

                new PlayerPlacement(Position.FW, 5, 1), //9
                new PlayerPlacement(Position.FW, 5, 3) //10
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(2, 3));
        links.put(1, List.of(6, 2));
        links.put(2, List.of(0, 1, 3, 6));
        links.put(3, List.of(0, 2, 4, 7));
        links.put(4, List.of(3, 7));
        links.put(5, List.of(6, 9));
        links.put(6, List.of(1, 2, 5, 7, 9));
        links.put(7, List.of(3, 4, 6, 8, 10));
        links.put(8, List.of(7, 10));
        links.put(9, List.of(5, 6, 10));
        links.put(10, List.of(7, 8, 9));

        return new Formation("Diamante (4-4-2)", placements, links);
    }

    public Formation createDobleW() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 2, 0),
                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 2, 6),

                new PlayerPlacement(Position.MF, 4, 1),
                new PlayerPlacement(Position.MF, 3, 3),
                new PlayerPlacement(Position.MF, 4, 5),

                new PlayerPlacement(Position.FW, 6, 0),
                new PlayerPlacement(Position.FW, 5, 3),
                new PlayerPlacement(Position.FW, 6, 6)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(2, 3));
        links.put(1, List.of(2, 5));
        links.put(2, List.of(0, 1, 3, 6));
        links.put(3, List.of(0, 2, 4, 6));
        links.put(4, List.of(3, 7));
        links.put(5, List.of(1, 6, 8, 9));
        links.put(6, List.of(2, 3, 5, 7));
        links.put(7, List.of(4, 6, 9, 10));
        links.put(8, List.of(5, 9));
        links.put(9, List.of(5, 7, 8, 10));
        links.put(10, List.of(7, 9));

        return new Formation("Doble w (4-3-3)", placements, links);
    }

    public Formation createAutobus() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 4),

                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 1, 7),

                new PlayerPlacement(Position.MF, 3, 0),
                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 4, 4),
                new PlayerPlacement(Position.MF, 2, 5),
                new PlayerPlacement(Position.MF, 3, 8),

                new PlayerPlacement(Position.FW, 5, 2),
                new PlayerPlacement(Position.FW, 5, 6)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2, 3));
        links.put(1, List.of(0, 2, 4));
        links.put(2, List.of(0, 1, 3, 5, 7));
        links.put(3, List.of(0, 2, 8));
        links.put(4, List.of(1, 5, 6, 9));
        links.put(5, List.of(2, 4, 7));
        links.put(6, List.of(4, 8, 9, 10));
        links.put(7, List.of(2, 5, 8));
        links.put(8, List.of(3, 6, 7, 10));
        links.put(9, List.of(4, 6, 10));
        links.put(10, List.of(6, 8, 9));


        return new Formation("Autobus (3-5-2)", placements, links);
    }

    public Formation createFlechaEspectral() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 0),
                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 1, 6),

                new PlayerPlacement(Position.MF, 2, 1),
                new PlayerPlacement(Position.MF, 3, 2),
                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 3, 4),
                new PlayerPlacement(Position.MF, 2, 5),

                new PlayerPlacement(Position.FW, 4, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(2, 3));
        links.put(1, List.of(2, 5));
        links.put(2, List.of(0, 1, 3, 5, 7));
        links.put(3, List.of(0, 2, 4, 7, 9));
        links.put(4, List.of(3, 9));
        links.put(5, List.of(1, 2, 6));
        links.put(6, List.of(5, 7, 10));
        links.put(7, List.of(2, 3, 6, 8));
        links.put(8, List.of(7, 9, 10));
        links.put(9, List.of(3, 4, 8));
        links.put(10, List.of(6, 8));

        return new Formation("Flecha espectral (4-5-1)", placements, links);
    }

    public Formation createJungla() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 1, 5),

                new PlayerPlacement(Position.MF, 2, 0),
                new PlayerPlacement(Position.MF, 2, 2),
                new PlayerPlacement(Position.MF, 2, 4),
                new PlayerPlacement(Position.MF, 2, 6),

                new PlayerPlacement(Position.FW, 3, 1),
                new PlayerPlacement(Position.FW, 3, 3),
                new PlayerPlacement(Position.FW, 3, 5)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2, 3));
        links.put(1, List.of(0, 2, 4));
        links.put(2, List.of(0, 1, 3, 5, 6));
        links.put(3, List.of(0, 2, 7));
        links.put(4, List.of(1, 5, 8));
        links.put(5, List.of(2, 4, 6, 9));
        links.put(6, List.of(2, 5, 7, 9));
        links.put(7, List.of(3, 6, 10));
        links.put(8, List.of(4, 9));
        links.put(9, List.of(5, 6, 8, 10));
        links.put(10, List.of(9, 7));

        return new Formation("Jungla (3-4-3)", placements, links);
    }

    public Formation createReja() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 2),

                new PlayerPlacement(Position.DF, 1, 0),
                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 1, 4),

                new PlayerPlacement(Position.MF, 2, 0),
                new PlayerPlacement(Position.MF, 2, 1),
                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 2, 4),

                new PlayerPlacement(Position.FW, 3, 1),
                new PlayerPlacement(Position.FW, 3, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(2, 3));
        links.put(1, List.of(2, 5));
        links.put(2, List.of(0, 1, 3, 6));
        links.put(3, List.of(0, 2, 4, 7));
        links.put(4, List.of(3, 8));
        links.put(5, List.of(1, 6, 9));
        links.put(6, List.of(2, 5, 7, 9));
        links.put(7, List.of(3, 6, 8, 10));
        links.put(8, List.of(4, 7, 10));
        links.put(9, List.of(5, 6, 10));
        links.put(10, List.of(7, 8, 9));

        return new Formation("Reja (4-4-2)", placements, links);
    }

    public Formation createPiramide() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 4),

                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 1, 5),

                new PlayerPlacement(Position.MF, 2, 1),
                new PlayerPlacement(Position.MF, 2, 4),
                new PlayerPlacement(Position.MF, 2, 7),

                new PlayerPlacement(Position.FW, 3, 0),
                new PlayerPlacement(Position.FW, 3, 2),
                new PlayerPlacement(Position.FW, 3, 4),
                new PlayerPlacement(Position.FW, 3, 6),
                new PlayerPlacement(Position.FW, 3, 8)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Piramide (2-3-5)", placements, links);
    }

    public Formation createZonaMuerta() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 4),

                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 6),
                new PlayerPlacement(Position.DF, 2, 0),
                new PlayerPlacement(Position.DF, 2, 8),
                new PlayerPlacement(Position.DF, 3, 4),

                new PlayerPlacement(Position.MF, 4, 1),
                new PlayerPlacement(Position.MF, 4, 4),
                new PlayerPlacement(Position.MF, 4, 7),

                new PlayerPlacement(Position.FW, 5, 3),
                new PlayerPlacement(Position.FW, 5, 5)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Zona muerta (5-3-2)", placements, links);
    }

    public Formation createAlasGrulla() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 2, 0),
                new PlayerPlacement(Position.DF, 2, 3),
                new PlayerPlacement(Position.DF, 2, 6),

                new PlayerPlacement(Position.MF, 3, 2),
                new PlayerPlacement(Position.MF, 3, 4),
                new PlayerPlacement(Position.MF, 4, 1),
                new PlayerPlacement(Position.MF, 4, 5),

                new PlayerPlacement(Position.FW, 5, 0),
                new PlayerPlacement(Position.FW, 5, 6)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Alas de grulla (4-4-2)", placements, links);
    }

    public Formation createArbolNavidad() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 0),
                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 1, 6),

                new PlayerPlacement(Position.MF, 2, 1),
                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 2, 5),

                new PlayerPlacement(Position.FW, 3, 2),
                new PlayerPlacement(Position.FW, 3, 4),
                new PlayerPlacement(Position.FW, 4, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Arbol de navidad (4-3-3)", placements, links);
    }

    public Formation createAtaqueTrillizo() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 4),

                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 1, 5),
                new PlayerPlacement(Position.DF, 1, 7),

                new PlayerPlacement(Position.MF, 2, 2),
                new PlayerPlacement(Position.MF, 2, 4),
                new PlayerPlacement(Position.MF, 2, 6),

                new PlayerPlacement(Position.FW, 3, 0),
                new PlayerPlacement(Position.FW, 3, 4),
                new PlayerPlacement(Position.FW, 3, 8)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Ataque trillizo (4-3-3)", placements, links);
    }

    public Formation createAncla() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 2, 3),
                new PlayerPlacement(Position.DF, 3, 0),
                new PlayerPlacement(Position.DF, 3, 6),

                new PlayerPlacement(Position.MF, 4, 3),
                new PlayerPlacement(Position.MF, 5, 1),
                new PlayerPlacement(Position.MF, 5, 5),
                new PlayerPlacement(Position.MF, 6, 3),

                new PlayerPlacement(Position.FW, 7, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Ancla (5-4-1)", placements, links);
    }

    public Formation createDobleM() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 4),

                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 4),
                new PlayerPlacement(Position.DF, 1, 7),

                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 2, 5),
                new PlayerPlacement(Position.MF, 3, 0),
                new PlayerPlacement(Position.MF, 3, 8),
                new PlayerPlacement(Position.MF, 4, 4),

                new PlayerPlacement(Position.FW, 5, 2),
                new PlayerPlacement(Position.FW, 5, 6)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Doble M (3-5-2)", placements, links);
    }

    public Formation createPala() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 2),
                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 1, 4),

                new PlayerPlacement(Position.MF, 2, 3),
                new PlayerPlacement(Position.MF, 3, 0),
                new PlayerPlacement(Position.MF, 3, 6),
                new PlayerPlacement(Position.MF, 4, 3),

                new PlayerPlacement(Position.FW, 5, 1),
                new PlayerPlacement(Position.FW, 5, 5),
                new PlayerPlacement(Position.FW, 6, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Pala (3-4-3)", placements, links);
    }

    public Formation createVueloFenix() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 3),

                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 2, 1),
                new PlayerPlacement(Position.DF, 2, 5),

                new PlayerPlacement(Position.MF, 3, 3),
                new PlayerPlacement(Position.MF, 4, 2),
                new PlayerPlacement(Position.MF, 4, 4),
                new PlayerPlacement(Position.MF, 5, 3),

                new PlayerPlacement(Position.FW, 6, 0),
                new PlayerPlacement(Position.FW, 6, 6),
                new PlayerPlacement(Position.FW, 7, 3)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Vuelo fenix (3-4-3)", placements, links);
    }

    public Formation createPuertaCielo() {
        List<PlayerPlacement> placements = List.of(
                new PlayerPlacement(Position.GK, 0, 2),

                new PlayerPlacement(Position.DF, 1, 1),
                new PlayerPlacement(Position.DF, 1, 3),
                new PlayerPlacement(Position.DF, 2, 0),
                new PlayerPlacement(Position.DF, 2, 4),

                new PlayerPlacement(Position.MF, 3, 1),
                new PlayerPlacement(Position.MF, 3, 3),
                new PlayerPlacement(Position.MF, 4, 0),
                new PlayerPlacement(Position.MF, 4, 4),

                new PlayerPlacement(Position.FW, 5, 2),
                new PlayerPlacement(Position.FW, 6, 2)
        );

        Map<Integer, List<Integer>> links = new HashMap<>();
        links.put(0, List.of(1, 2));
        links.put(1, List.of(0, 3));
        links.put(2, List.of(0, 3));
        links.put(3, List.of(1, 2));

        return new Formation("Puerta al cielo (4-4-2)", placements, links);
    }
}
