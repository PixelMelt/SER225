package Level;

import Engine.GraphicsHandler;
import Engine.ScreenManager;
import SpriteFont.SpriteFont;
import java.awt.*;

public class DialogueBox {
    private static final int BOX_HEIGHT_RATIO = 3; // 1/3 of screen
    private static final int PORTRAIT_SIZE = 80;
    private static final int PORTRAIT_PADDING = 10;
    private static final int TEXT_PADDING = 15;
    private static final int LINE_SPACING = 25;

    private DialogueMessage currentMessage;
    private int boxX;
    private int boxY;
    private int boxWidth;
    private int boxHeight;

    public DialogueBox() {
        this.boxWidth = ScreenManager.getScreenWidth();
        this.boxHeight = ScreenManager.getScreenHeight() / BOX_HEIGHT_RATIO;
        this.boxX = 0;
        this.boxY = ScreenManager.getScreenHeight() - boxHeight;
    }

    public void setCurrentMessage(DialogueMessage message) {
        this.currentMessage = message;
    }

    public void draw(GraphicsHandler graphicsHandler) {
        if (currentMessage == null) {
            return;
        }

        graphicsHandler.drawFilledRectangle(boxX, boxY, boxWidth, boxHeight, Color.BLACK);
        graphicsHandler.drawRectangle(boxX, boxY, boxWidth, boxHeight, Color.WHITE, 2);

        int textX = boxX + TEXT_PADDING;
        int textY = boxY + TEXT_PADDING + 20; // offset for first line

        // draw portrait if available
        if (currentMessage.hasPortrait()) {
            int portraitX = boxX + PORTRAIT_PADDING;

            int actualPortraitHeight = currentMessage.getPortrait().getHeight();
            int portraitY = boxY + (boxHeight / 2) - (actualPortraitHeight * 2);

            graphicsHandler.drawImage(currentMessage.getPortrait(), portraitX, portraitY, PORTRAIT_SIZE, PORTRAIT_SIZE);

            // text position right of portrait
            textX = portraitX + PORTRAIT_SIZE + TEXT_PADDING;
        }

        drawWrappedText(graphicsHandler, currentMessage.getText(), textX, textY);
    }

    private void drawWrappedText(GraphicsHandler graphicsHandler, String text, int startX, int startY) {
        String[] lines = text.split("\n");

        int currentY = startY;

        for (String line : lines) {
            // Draw each line
            SpriteFont textLine = new SpriteFont(line, startX, currentY, "Arial", 18, Color.WHITE);
            textLine.draw(graphicsHandler);
            currentY += LINE_SPACING;
        }
    }
}
