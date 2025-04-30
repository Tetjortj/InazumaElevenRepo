package main.ui.screens;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class ScreenInfo {
    private static final Rectangle2D VISUAL_BOUNDS = Screen.getPrimary().getVisualBounds();
    public static final double SCREEN_WIDTH  = VISUAL_BOUNDS.getWidth();
    public static final double SCREEN_HEIGHT = VISUAL_BOUNDS.getHeight();
}
