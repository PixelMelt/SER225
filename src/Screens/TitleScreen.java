package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import SpriteFont.SpriteFont;
import java.awt.*;
import javax.swing.Timer;

public class TitleScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected SpriteFont title;
    protected SpriteFont menuChange;
    protected Color transparent;
    protected Timer fadeTimer;

    private float alpha = 1.0f;
    private boolean fadingOut = true;
    

    public TitleScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    @Override
    public void initialize() {

        
        title = new SpriteFont("Speed Step", Config.BASE_GAME_WIDTH/8, 125, "Courier New", 100, Color.white);
        title.setOutlineColor(Color.black);
        title.setOutlineThickness(10);
        

        menuChange = new SpriteFont("Press Space to Continue", 250, 450, "Verdana", 25, Color.white);
        menuChange.setOutlineColor(Color.black);
        menuChange.setOutlineThickness(5);

        // start fade timer
        fadeTimer = new Timer(100, e ->  {
            if (fadingOut) {
                alpha -= 0.05f;
                if(alpha <= 0f) {
                    alpha = 0f;
                    fadingOut = false;
                }
            } else {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadingOut = true;
                }
            }
            Color fadeColor = new Color(255, 255, 255, (int)(alpha * 255));
            Color fadeColorOutline = new Color(0, 0, 0, (int)(alpha * 255));
            menuChange.setColor(fadeColor);
            menuChange.setOutlineColor(fadeColorOutline);
        });
        fadeTimer.start();
    }

    public void update() {
        if (Keyboard.isKeyDown(Key.SPACE)){
            screenCoordinator.setGameState(GameState.MENU);
            fadeTimer.stop();
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        
        title.draw(graphicsHandler);
        menuChange.draw(graphicsHandler);
    }
}