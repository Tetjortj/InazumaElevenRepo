package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    private String name;
    private Team team;
    private Position position;
    private Element element;
    private int fp, tp, kick, body, control, guard, speed, stamina, guts;
    private String move1, move2, move3, move4;

    @JsonCreator
    public Card(
            @JsonProperty("name") String name,
            @JsonProperty("team") Team team,
            @JsonProperty("position") Position position,
            @JsonProperty("element") Element element,
            @JsonProperty("fp") int fp,
            @JsonProperty("tp") int tp,
            @JsonProperty("kick") int kick,
            @JsonProperty("body") int body,
            @JsonProperty("control") int control,
            @JsonProperty("guard") int guard,
            @JsonProperty("speed") int speed,
            @JsonProperty("stamina") int stamina,
            @JsonProperty("guts") int guts,
            @JsonProperty("move1") String move1,
            @JsonProperty("move2") String move2,
            @JsonProperty("move3") String move3,
            @JsonProperty("move4") String move4
    ) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.element = element;
        this.fp = fp;
        this.tp = tp;
        this.kick = kick;
        this.body = body;
        this.control = control;
        this.guard = guard;
        this.speed = speed;
        this.stamina = stamina;
        this.guts = guts;
        this.move1 = move1;
        this.move2 = move2;
        this.move3 = move3;
        this.move4 = move4;
    }

    public String getName() { return name; }
    public Team getTeam() { return team; }
    public Position getPosition() { return position; }
    public Element getElement() { return element; }

    public int getFp() { return fp; }
    public int getTp() { return tp; }
    public int getKick() { return kick; }
    public int getBody() { return body; }
    public int getControl() { return control; }
    public int getGuard() { return guard; }
    public int getSpeed() { return speed; }
    public int getStamina() { return stamina; }
    public int getGuts() { return guts; }

    public String getMove1() { return move1; }
    public String getMove2() { return move2; }
    public String getMove3() { return move3; }
    public String getMove4() { return move4; }

    @Override
    public String toString() {
        return String.format("%s (%s) – %s – Kick: %d | Moves: %s, %s",
                name, position, team, kick, move1, move2);
    }
}

