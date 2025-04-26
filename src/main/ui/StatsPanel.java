package main.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.Card;
import main.Formation;

import java.util.List;
import java.util.Map;

public class StatsPanel extends VBox {
    private final Label mediaLabel = new Label("Puntuación media: 0");
    private final Label quimicaLabel = new Label("Química: 0");

    public StatsPanel() {
        this.setStyle("-fx-background-color: #333; -fx-padding: 20;");
        this.setSpacing(10);
        this.getChildren().addAll(mediaLabel, quimicaLabel);
    }

    public void actualizarStats(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
        if (jugadoresSeleccionados.isEmpty()) {
            mediaLabel.setText("Puntuación media: 0");
            quimicaLabel.setText("Química: 0");
            return;
        }

        int sumaPuntajes = jugadoresSeleccionados.values().stream().mapToInt(Card::calcularScore).sum();
        int media = Math.round((float) sumaPuntajes / jugadoresSeleccionados.size());
        mediaLabel.setText("Puntuación media: " + media);

        int quimica = calcularQuimica(formacion, jugadoresSeleccionados);
        quimicaLabel.setText("Química: " + quimica);
    }

    private int calcularQuimica(Formation formacion, Map<Integer, Card> jugadoresSeleccionados) {
        Map<Integer, List<Integer>> links = formacion.getLinks();
        int enlacesTotales = 0;
        float quimicaActual = 0;

        for (Map.Entry<Integer, List<Integer>> entry : links.entrySet()) {
            int from = entry.getKey();
            for (int to : entry.getValue()) {
                if (from < to && jugadoresSeleccionados.containsKey(from) && jugadoresSeleccionados.containsKey(to)) {
                    enlacesTotales++;
                    Card a = jugadoresSeleccionados.get(from);
                    Card b = jugadoresSeleccionados.get(to);

                    if (a.getTeam() == b.getTeam() || (a.getElement() == b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 1.0;
                    } else if ((a.getElement() == b.getElement() && a.getGrade() != b.getGrade()) ||
                            (a.getElement() != b.getElement() && a.getGrade() == b.getGrade())) {
                        quimicaActual += 0.5;
                    } else {
                        quimicaActual += 0.25;
                    }
                }
            }
        }
        return enlacesTotales > 0 ? Math.round((quimicaActual / enlacesTotales) * 100) : 0;
    }
}

