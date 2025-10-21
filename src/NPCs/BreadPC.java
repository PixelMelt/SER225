package NPCs;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.NPC;
import Level.Player;
import Utils.Point;
import java.util.HashMap;

// This class is for the BREADPC
public class BreadPC extends NPC {

    public BreadPC(Point location) {
        super(location.x, location.y, new SpriteSheet(ImageLoader.load("BreadPC.png"), 24, 24), "Smile");
        isInteractable = true;
        talkedToTime = 200;
        textbox.setText("Why? WHY?? WHYYY??? WOuld you eat me?");
        textboxOffsetX = -4;
        textboxOffsetY = -34;
    }

    public void update(Player player) {
        // while npc is being talked to, it frowns
        if (talkedTo) {
            currentAnimationName = "Frown";
        } else {
            currentAnimationName = "Smile";
        }

        super.update(player);
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
           put("Smile", new Frame[] {
                   new FrameBuilder(spriteSheet.getSprite(0, 0))
                           .withScale(4)
                           .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                           .build()
           });
            put("Frown", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0))
                            .withScale(4)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build()
            });
        }};
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
    }
}

