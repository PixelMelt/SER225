package Screens;

import Engine.GraphicsHandler;
import Engine.Screen;
import Engine.ScreenManager;
import SpriteFont.SpriteFont;

import java.awt.*;

// This class is for the level cleared screen
public class LevelClearedScreen extends Screen {
    protected SpriteFont winMessage;
    protected SpriteFont timeDisplay;
    protected long finalTimeMs = 0L;

    public LevelClearedScreen() {
        initialize();
    }

    @Override
    public void initialize() {
        winMessage = new SpriteFont("Level Cleared", 320, 239, "Arial", 30, Color.white);
    }

    @Override
    public void update() {

    }

    public void draw(GraphicsHandler graphicsHandler) {
        // paint entire screen black and dislpay level cleared text and timer text
        graphicsHandler.drawFilledRectangle(0, 0, ScreenManager.getScreenWidth(), ScreenManager.getScreenHeight(), Color.black);
        int screenW = ScreenManager.getScreenWidth();
        int screenH = ScreenManager.getScreenHeight();
        Graphics2D g = graphicsHandler.getGraphics();
        if (winMessage != null) {
            Font winFont = winMessage.getFont();
            FontMetrics fm = g.getFontMetrics(winFont);
            int textWidth = fm.stringWidth(winMessage.getText());
            int centerX = screenW / 2 - textWidth / 2;
            int winY = screenH / 2 - 20;
            winMessage.setLocation(centerX, winY);
            winMessage.draw(graphicsHandler);
        }

        if (timeDisplay != null) {
            Font timeFont = timeDisplay.getFont();
            FontMetrics tfm = g.getFontMetrics(timeFont);
            int textWidth = tfm.stringWidth(timeDisplay.getText());
            int centerX = screenW / 2 - textWidth / 2;
            int timeY = screenH / 2 + 10;
            timeDisplay.setLocation(centerX, timeY);
            timeDisplay.draw(graphicsHandler);
        }
    }

    // display final time
    public void setFinalTime(long ms) {
        this.finalTimeMs = ms;
        String timeString = formatTime(ms);
        // place the time under the win message
        this.timeDisplay = new SpriteFont("Time: " + timeString, 320, 290, "Arial", 24, Color.white);
        this.timeDisplay.setOutlineColor(Color.black);
        this.timeDisplay.setOutlineThickness(2);
    }

    private String formatTime(long ms) {
        long minutes = (ms / 1000) / 60;
        long seconds = (ms / 1000) % 60;
        long milliseconds = (ms % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
    }
}
