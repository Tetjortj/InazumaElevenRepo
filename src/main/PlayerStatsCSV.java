package main;

public class PlayerStatsCSV {
    private String name;
    private String team;
    private String position;
    private String element;
    private int fp, tp, kick, body, control, guard, speed, stamina, guts;
    private String move1, move2, move3, move4;

    public PlayerStatsCSV(String name, String team, String position, String element,
                          int fp, int tp, int kick, int body, int control,
                          int guard, int speed, int stamina, int guts,
                          String move1, String move2, String move3, String move4) {
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


    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getElement() { return element; }
    public void setElement(String element) { this.element = element; }

    public int getFp() { return fp; }
    public void setFp(int fp) { this.fp = fp; }

    public int getTp() { return tp; }
    public void setTp(int tp) { this.tp = tp; }

    public int getKick() { return kick; }
    public void setKick(int kick) { this.kick = kick; }

    public int getBody() { return body; }
    public void setBody(int body) { this.body = body; }

    public int getControl() { return control; }
    public void setControl(int control) { this.control = control; }

    public int getGuard() { return guard; }
    public void setGuard(int guard) { this.guard = guard; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }

    public int getGuts() { return guts; }
    public void setGuts(int guts) { this.guts = guts; }

    public String getMove1() { return move1; }
    public void setMove1(String move1) { this.move1 = move1; }

    public String getMove2() { return move2; }
    public void setMove2(String move2) { this.move2 = move2; }

    public String getMove3() { return move3; }
    public void setMove3(String move3) { this.move3 = move3; }

    public String getMove4() { return move4; }
    public void setMove4(String move4) { this.move4 = move4; }

    @Override
    public String toString() {
        return name + " (" + position + ") [" + element + "] | Team: " + team +
                " | FP: " + fp + " | TP: " + tp +
                " | Kick: " + kick + " | Body: " + body + " | Control: " + control +
                " | Guard: " + guard + " | Speed: " + speed + " | Stamina: " + stamina + " | Guts: " + guts +
                " | Moves: " + move1 + ", " + move2 + ", " + move3 + ", " + move4;
    }

}

