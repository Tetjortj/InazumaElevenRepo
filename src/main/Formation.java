package main;

import java.util.List;
import java.util.Map;

public class Formation {
    private final String name;
    private final List<PlayerPlacement> placements;
    private final Map<Integer, List<Integer>> links;

    public Formation(String name, List<PlayerPlacement> placements, Map<Integer, List<Integer>> links) {
        this.name = name;
        this.placements = placements;
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public List<PlayerPlacement> getPlacements() {
        return placements;
    }

    public Map<Integer, List<Integer>> getLinks() {
        return links;
    }

    public int getMinFila() {
        return placements.stream().mapToInt(PlayerPlacement::getFila).min().orElse(0);
    }

    public int getMaxFila() {
        return placements.stream().mapToInt(PlayerPlacement::getFila).max().orElse(0);
    }

    public int getMinColumna() {
        return placements.stream().mapToInt(PlayerPlacement::getColumna).min().orElse(0);
    }

    public int getMaxColumna() {
        return placements.stream().mapToInt(PlayerPlacement::getColumna).max().orElse(0);
    }
}