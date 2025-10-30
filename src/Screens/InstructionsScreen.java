package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import java.awt.*;

// This class is for the credits screen
public class InstructionsScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected Map background;
    protected KeyLocker keyLocker = new KeyLocker();
    protected SpriteFont instructionsLabel;
    protected SpriteFont keyLabel;
    protected SpriteFont returnInstructionsLabel;

    public InstructionsScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    @Override
    public void initialize() {
        // setup graphics on screen (background map, spritefont text)
        background = new TitleScreenMap();
        background.setAdjustCamera(false);
        instructionsLabel = new SpriteFont("INSTRUCTIONS", 460, 50, "Times New Roman", 40, new Color(49, 207, 240));
        instructionsLabel.setOutlineColor(Color.black);
        instructionsLabel.setOutlineThickness(3);
    keyLabel = new SpriteFont("Arrow Keys / WASD: Movement\nX / N: Goose Grapple\nZ / M: Honk\nSpace: Talk to NPC", 470, 121, "Times New Roman", 30, Color.black);
        returnInstructionsLabel = new SpriteFont("Press Space to return to the menu", 20, 532, "Times New Roman", 30, Color.white);
        keyLocker.lockKey(Key.SPACE);
    }

    public void update() {
        background.update(null);

        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }

        // if space is pressed, go back to main menu
        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
            screenCoordinator.setGameState(GameState.MENU);
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        background.draw(graphicsHandler);
        graphicsHandler.drawFilledRectangle(440, 40, 540, 360, Color.white);
        instructionsLabel.draw(graphicsHandler);
        keyLabel.drawWithParsedNewLines(graphicsHandler, 20);
        returnInstructionsLabel.draw(graphicsHandler);
    }
}
