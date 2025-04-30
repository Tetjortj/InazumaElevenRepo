package main.ui;

import javafx.stage.Stage;
import javafx.stage.Window;

public class FxUtils {
    public static Stage getCurrentStage() {
        return (Stage) Stage.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);
    }
}