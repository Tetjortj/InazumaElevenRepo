package main;

import java.util.ArrayList;
import java.util.List;

public class PlayerPool {
    private final List<Card> allPlayers = new ArrayList<>();

    private final List<Card> goalkeepers = new ArrayList<>();
    private final List<Card> defenders = new ArrayList<>();
    private final List<Card> midfielders = new ArrayList<>();
    private final List<Card> strikers = new ArrayList<>();

    public PlayerPool(List<Card> players) {
        this.allPlayers.addAll(players);
        groupPlayersByPosition();
    }

    private void groupPlayersByPosition() {
        for (Card card : allPlayers) {
            switch (card.getPos()) {
                case GKE -> goalkeepers.add(card);
                case DEF -> defenders.add(card);
                case MDF -> midfielders.add(card);
                case STR -> strikers.add(card);
            }
        }
    }

    // Getters
    public List<Card> getGoalkeepers() { return goalkeepers; }
    public List<Card> getDefenders() { return defenders; }
    public List<Card> getMidfielders() { return midfielders; }
    public List<Card> getStrikers() { return strikers; }

    public List<Card> getAllPlayers() { return allPlayers; }

    public List<Card> getByPosition(Position pos) {
        return switch (pos) {
            case GKE -> goalkeepers;
            case DEF -> defenders;
            case MDF -> midfielders;
            case STR -> strikers;
        };
    }
}
