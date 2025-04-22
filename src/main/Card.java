package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    public String name;
    public int rating;
    public Position position;
    public Team team;
    public Element element;

    @JsonCreator
    public Card(
            @JsonProperty("name") String name,
            @JsonProperty("rating") int rating,
            @JsonProperty("pos") Position position,
            @JsonProperty("team") Team team,
            @JsonProperty("element") Element element
    ) {
        this.name = name;
        this.rating = rating;
        this.position = position;
        this.team = team;
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public Position getPos() {
        return position;
    }

    public int getRating() {
        return rating;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) – %s – %d", name, position, team, rating);
    }
}
