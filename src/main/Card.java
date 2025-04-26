package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    private String name;
    private Team team;
    private Position position;
    private Element element;
    private Grade grade;
    private int fp, tp, kick, body, control, guard, speed, stamina, guts;
    private String move1, move2, move3, move4;
    private int score;
    private String photoPath; // Nuevo campo para la imagen

    @JsonCreator
    public Card(
            @JsonProperty("name") String name,
            @JsonProperty("team") Team team,
            @JsonProperty("position") Position position,
            @JsonProperty("element") Element element,
            @JsonProperty("grade") Grade grade,
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
        this.grade = grade;
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
        this.score = calcularScore();
    }

    public String getName() { return name; }
    public Team getTeam() { return team; }
    public Position getPosition() { return position; }
    public Element getElement() { return element; }
    public Grade getGrade() { return grade; }
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
    public double getScore() { return score; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public int calcularScore() {
        double cruda = switch (position) {
            case GK -> guard * 0.4 + control * 0.2 + guts * 0.15 + speed * 0.1 + body * 0.1 + stamina * 0.05;
            case DF -> guard * 0.35 + body * 0.25 + control * 0.15 + speed * 0.1 + guts * 0.1 + stamina * 0.05;
            case MF -> control * 0.3 + body * 0.2 + speed * 0.15 + stamina * 0.15 + guts * 0.1 + kick * 0.1;
            case FW -> kick * 0.4 + speed * 0.2 + control * 0.15 + guts * 0.1 + body * 0.1 + stamina * 0.05;
            default -> (kick + guard + control + body + speed + stamina + guts) / 7.0;
        };

        return normalizarPuntaje(cruda);
    }

    private int normalizarPuntaje(double valor) {
        int minOriginal = 41;
        int maxOriginal = 76;
        int minDeseado = 50;
        int maxDeseado = 90;

        if (valor <= minOriginal) return minDeseado;
        if (valor >= maxOriginal) return maxDeseado;

        double escalado = minDeseado + (valor - minOriginal) * (maxDeseado - minDeseado) / (maxOriginal - minOriginal);
        return (int) Math.round(escalado);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) – %s – Score: %d",
                name, position, team, score);
    }
}