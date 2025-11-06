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
public class GoodGoose extends NPC {

    public GoodGoose(Point location) {
        super(location.x, location.y, new SpriteSheet(ImageLoader.load("GoodGoose.png"), 24, 24), "Goose");
        isInteractable = true;

        // Get portrait frames
        Frame npcPortrait = getPortraitFrame();
        SpriteSheet playerSheet = new SpriteSheet(ImageLoader.load("Cat.png"), 24, 24);
        Frame playerPortrait = new Builders.FrameBuilder(playerSheet.getSprite(0, 0))
                .withScale(3)
                .withBounds(3, 3, 17, 19)
                .build();

        // Setup dialogue sequence
        dialogueSequence = new Level.DialogueSequence();
        dialogueSequence.addMessage("You have done well Sir Honk-a-lots.", npcPortrait);
        dialogueSequence.addMessage("What is it you visit me for?", playerPortrait);
        dialogueSequence.addMessage("To take this bread as it is not safe to go alone.", npcPortrait);
        dialogueSequence.addMessage("I'll pass. *waddles away, waddle waddle*", playerPortrait);
    }

    public void update(Player player) {
        // while npc is being talked to, it frowns
        if (isInDialogue) {
            currentAnimationName = "Goose";
        } else {
            currentAnimationName = "Goose1";
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
