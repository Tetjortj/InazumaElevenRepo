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
    private static String currentPath = "";
    private static double savedVolume = 0.4;

    public static void playMusic(String path) {
        // si es la misma ruta, no lo reiniciamos
        if (path.equals(currentPath) && currentPlayer != null) {
            return;
        }
        currentPath = path;
        if (currentPlayer != null) {
            currentPlayer.stop();
        }
        URL res = MusicManager.class.getResource(path);
        if (res != null) {
            Media m = new Media(res.toExternalForm());
            currentPlayer = new MediaPlayer(m);
            currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            currentPlayer.setVolume(savedVolume);
            currentPlayer.play();
        }
    }

    public static void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
        }
    }

    public static void setVolume(double volume) {
        savedVolume = volume;
        if (currentPlayer != null) {
            currentPlayer.setVolume(volume);
        }
    }

    public static double getVolume() {
        return savedVolume;
    }

    public static void fadeOutMusic(Runnable afterFadeOut) {
        if (currentPlayer == null) return;

        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(currentPlayer.volumeProperty(), currentPlayer.getVolume())),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(currentPlayer.volumeProperty(), 0))
        );
        fadeOut.setOnFinished(e -> {
            currentPlayer.stop();
            if (afterFadeOut != null) {
                afterFadeOut.run();
            }
        });
        fadeOut.play();
    }

    public static void fadeInMusic() {
        if (currentPlayer == null) return;
        currentPlayer.setVolume(0);
        currentPlayer.play();

        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(currentPlayer.volumeProperty(), 0)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(currentPlayer.volumeProperty(), savedVolume)) // ⬅️ hacia volumen guardado
        );
        fadeIn.play();
    }
}