package NPCs;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.SpriteSheet;
import Level.NPC;
import Level.Player;
import Utils.Point;
import java.util.HashMap;

// This class is for the BREADPC
public class Duck extends NPC {

    public Duck(Point location) {
        super(location.x, location.y, new SpriteSheet(ImageLoader.load("Duck.png"), 24, 24), "Stare");
        isInteractable = true;
        talkedToTime = 200;
        textbox.setText("What do you call a goose detective? \nSherlock Honks");
        textboxOffsetX = -4;
        textboxOffsetY = -34;
    }

    public void update(Player player) {
        // while npc is being talked to, it frowns
        if (talkedTo) {
            currentAnimationName = "EyeRoll";
        } else {
            currentAnimationName = "Stare";
        }

        super.update(player);
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
           put("Stare", new Frame[] {
                   new FrameBuilder(spriteSheet.getSprite(0, 0))
                           .withScale(4)
                           .build()
           });
            put("EyeRoll", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0))
                            .withScale(4)
                            .build()
            });
        }};
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
    }
}