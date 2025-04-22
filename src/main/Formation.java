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
}
