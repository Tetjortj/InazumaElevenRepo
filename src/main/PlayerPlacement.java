package main;

public class PlayerPlacement {
    public Position position;
    public int fila;
    public int columna;

    public PlayerPlacement(Position position, int fila, int columna) {
        this.position = position;
        this.fila = fila;
        this.columna = columna;
    }

    public Position getPosition() {
        return position;
    }

    public int getColumna() {
        return columna;
    }

    public int getFila() {
        return fila;
    }
}
