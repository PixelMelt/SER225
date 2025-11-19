package Screens;

import Engine.*;
import SpriteFont.SpriteFont;
import java.awt.*;
import java.util.Random;

// This is the class for the level lose screen
public class LevelLoseScreen extends Screen {
    protected SpriteFont loseMessage;
    protected SpriteFont instructions;
    protected KeyLocker keyLocker = new KeyLocker();
    protected PlayLevelScreen playLevelScreen;
    protected Random random = new Random();

    protected String[] insults = {
        "Maybe gaming isn't for you...",
        "Have you tried the tutorial?",
        "Did you mean to do that?",
        "That was embarrassing to watch...",
        "Are you even trying?",
        "You died? Shocking.",
        "Skill issue detected!",
        "Better luck next time... you'll need it.",
        "Is this your first video game?",
        "That was painful to witness.",
        "Press Alt+F4 for godmode!",
        "You managed to fail successfully!",
        "Achievement unlocked: Professional Loser",
        "How did you even manage that?",
        "Your keyboard must be broken... right?",
        "At least you're consistent!",
        "Try using both hands next time.",
        "The game isn't THAT hard...",
        "Were you playing with your eyes closed?",
        "You make this look difficult!",
        "The tutorial was pretty clear about that...",
        "Did you forget which buttons do what?",
        "That's not how you're supposed to play...",
        "Your efforts are... noted.",
        "You've got spirit... just not skill.",
        "Close! If close meant nowhere near success.",
        "Is losing your speedrun strategy?",
        // Goose-specific insults
        "Even a duck could've grabbed that bread!",
        "Maybe stick to honking at tourists?",
        "That goose is cooked!",
        "The bread was RIGHT THERE!",
        "That neck ain't built for this!",
        "You call that a swing? Embarrassing!",
        "Geese are supposed to be graceful!",
        "That's the worst bread grab I've ever seen!",
        "The pond is calling, it wants its goose back.",
        "Your neck extended... your skill didn't.",
        "Bread: 1, Goose: 0",
        "You got outplayed by literal bread."
    };

    public LevelLoseScreen(PlayLevelScreen playLevelScreen) {
        this.playLevelScreen = playLevelScreen;
        initialize();
    }

    @Override
    public void initialize() {
        String randomInsult = insults[random.nextInt(insults.length)];

        Font tempFont = new Font("Arial", Font.PLAIN, 30);
        Canvas canvas = new Canvas();
        FontMetrics metrics = canvas.getFontMetrics(tempFont);
        int textWidth = metrics.stringWidth(randomInsult);

        int insultX = (ScreenManager.getScreenWidth() / 2) - (textWidth / 2);
        loseMessage = new SpriteFont(randomInsult, insultX, 239, "Arial", 30, Color.white);

        instructions = new SpriteFont("Press Space to try again or Escape to go back to the main menu", 120, 279,"Arial", 20, Color.white);
        keyLocker.lockKey(Key.SPACE);
        keyLocker.lockKey(Key.ESC);
    }

    @Override
    public void update() {
        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }
        if (Keyboard.isKeyUp(Key.ESC)) {
            keyLocker.unlockKey(Key.ESC);
        }

        // if space is pressed, reset level. if escape is pressed, go back to main menu
        if (Keyboard.isKeyDown(Key.SPACE) && !keyLocker.isKeyLocked(Key.SPACE)) {
            playLevelScreen.resetLevel();
        } else if (Keyboard.isKeyDown(Key.ESC) && !keyLocker.isKeyLocked(Key.ESC)) {
            playLevelScreen.goBackToMenu();
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        graphicsHandler.drawFilledRectangle(0, 0, ScreenManager.getScreenWidth(), ScreenManager.getScreenHeight(), Color.black);
        loseMessage.draw(graphicsHandler);
        instructions.draw(graphicsHandler);
    }
}
