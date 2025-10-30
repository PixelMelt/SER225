package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import java.awt.*;
import java.awt.image.BufferedImage;

// This is the class for the main menu screen
public class MenuScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected int currentMenuItemHovered = 0; // current menu item being "hovered" over
    protected int menuItemSelected = -1;
    protected SpriteFont playGame;
    protected SpriteFont credits;
    protected SpriteFont instructions;
    protected Map background;
    protected int keyPressTimer;
    protected int pointerLocationX, pointerLocationY;
    protected KeyLocker keyLocker = new KeyLocker();

    private BufferedImage titleImage;

    public MenuScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    @Override
    public void initialize() {
        titleImage = ImageLoader.load("title.png");
        playGame = new SpriteFont("PLAY GAME", 550, 123, "Arial", 30, new Color(49, 207, 240));
        playGame.setOutlineColor(Color.black);
        playGame.setOutlineThickness(3);
        instructions = new SpriteFont("INSTRUCTIONS", 550, 223, "Arial", 30, new Color(49, 207, 240));
        instructions.setOutlineColor(Color.black);
        instructions.setOutlineThickness(3);
        credits = new SpriteFont("CREDITS", 550, 323, "Arial", 30, new Color(49, 207, 240));
        credits.setOutlineColor(Color.black);
        credits.setOutlineThickness(3);
        background = new TitleScreenMap();
        background.setAdjustCamera(false);
        keyPressTimer = 0;
        menuItemSelected = -1;
        keyLocker.lockKey(Key.SPACE);
    }

    @Override
    public void update() {
        // update background map (to play tile animations)
        background.update(null);

        // if down or up is pressed or S and W, change menu item "hovered" over (blue square in front of text will move along with currentMenuItemHovered changing)
        if ((Keyboard.isKeyDown(Key.DOWN) || Keyboard.isKeyDown(Key.S)) &&  keyPressTimer == 0) {
            keyPressTimer = 14;
            currentMenuItemHovered++;
        } else if ((Keyboard.isKeyDown(Key.UP) || Keyboard.isKeyDown(Key.W)) &&  keyPressTimer == 0) {
            keyPressTimer = 14;
            currentMenuItemHovered--;
        } else {
            if (keyPressTimer > 0) {
                keyPressTimer--;
            }
        }

        // if down is pressed on last menu item or up is pressed on first menu item, "loop" the selection back around to the beginning/end
        if (currentMenuItemHovered > 2) {
            currentMenuItemHovered = 0;
        } else if (currentMenuItemHovered < 0) {
            currentMenuItemHovered = 2;
        }

        // sets location for blue square in front of text (pointerLocation) and also sets color of spritefont text based on which menu item is being hovered
        switch (currentMenuItemHovered) {
            case 0 -> {
                playGame.setColor(new Color(255, 215, 0));
                instructions.setColor(new Color(49, 207, 240));
                credits.setColor(new Color(49, 207, 240));
                pointerLocationX = 500;
                pointerLocationY = 130;
            }
            case 1 -> {
                playGame.setColor(new Color(49, 207, 240));
                instructions.setColor(new Color(255, 215, 0));
                credits.setColor(new Color(49, 207, 240));
                pointerLocationX = 500;
                pointerLocationY = 230;
            }
            case 2 -> {
                playGame.setColor(new Color(49, 207, 240));
                instructions.setColor(new Color(49, 207, 240));
                credits.setColor(new Color(255, 215, 0));
                pointerLocationX = 500;
                pointerLocationY = 330;
            }
            default -> {
            }
        }

        // if space is pressed on menu item, change to appropriate screen based on which menu item was chosen
        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }
        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
            menuItemSelected = currentMenuItemHovered;
            switch (menuItemSelected) {
                case 0 -> screenCoordinator.setGameState(GameState.LEVEL);
                case 1 -> screenCoordinator.setGameState(GameState.INSTRUCTIONS);
                case 2 -> screenCoordinator.setGameState(GameState.CREDITS);
                default -> {
                }
            }
        }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        background.draw(graphicsHandler);
        graphicsHandler.drawFilledRectangle(440, 110, 540, 310, Color.white);
        //graphicsHandler.drawImage(titleImage, 320, 0, 560, 300);
        playGame.draw(graphicsHandler);
        instructions.draw(graphicsHandler);
        credits.draw(graphicsHandler);
        graphicsHandler.drawFilledRectangleWithBorder(pointerLocationX, pointerLocationY, 20, 20, new Color(255, 215, 0), Color.black, 2);
    }
}
