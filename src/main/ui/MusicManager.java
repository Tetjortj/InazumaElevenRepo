package main.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import java.net.URL;
import javafx.util.Duration;

public class MusicManager {
    private static MediaPlayer currentPlayer;

    public static void playMusic(String path) {
        if (currentPlayer != null) {
            currentPlayer.stop();
        }

        URL resource = MusicManager.class.getResource(path);
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            currentPlayer = new MediaPlayer(media);
            currentPlayer.setVolume(0.4); // Volumen inicial más bajo
            currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            currentPlayer.play();
        } else {
            System.err.println("No se encontró: " + path);
        }
    }

    public static void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer = null;
        }
    }

    public static MediaPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    // FADE OUT (bajar volumen suave hasta parar)
    public static void fadeOutMusic(Runnable afterFadeOut) {
        if (currentPlayer == null) return;

        Timeline fadeOut = new Timeline(
                new KeyFrame(
                        Duration.seconds(1.5),
                        new KeyValue(currentPlayer.volumeProperty(), 0)
                )
        );
        fadeOut.setOnFinished(e -> {
            currentPlayer.stop();
            if (afterFadeOut != null) {
                afterFadeOut.run();
            }
        });
        fadeOut.play();
    }

    // FADE IN (subir volumen suave)
    public static void fadeInMusic() {
        if (currentPlayer == null) return;

        currentPlayer.setVolume(0);
        currentPlayer.play();

        Timeline fadeIn = new Timeline(
                new KeyFrame(
                        Duration.seconds(1.5),
                        new KeyValue(currentPlayer.volumeProperty(), 0.4) // volumen deseado
                )
        );
        fadeIn.play();
    }
}
