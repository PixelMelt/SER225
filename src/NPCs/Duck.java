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

        // Get portrait frames
        Frame npcPortrait = getPortraitFrame();
        SpriteSheet playerSheet = new SpriteSheet(ImageLoader.load("Cat.png"), 24, 24);
        Frame playerPortrait = new Builders.FrameBuilder(playerSheet.getSprite(0, 0))
                .withScale(3)
                .withBounds(3, 3, 17, 19)
                .build();

        // Setup dialogue sequence
        dialogueSequence = new Level.DialogueSequence();
        dialogueSequence.addMessage("What do you call a goose detective?", npcPortrait);
        dialogueSequence.addMessage("Hmm... no idea.", playerPortrait);
        dialogueSequence.addMessage("Sherlock Honks!", npcPortrait);
        dialogueSequence.addMessage("You know im not sure why I exptected\nbetter then that.", playerPortrait);
        dialogueSequence.addMessage("You try coming up with a coherant\ngoose joke then.", npcPortrait);
        dialogueSequence.addMessage("Fair enough.", playerPortrait);
    }

    public void update(Player player) {
        // while npc is being talked to, it does an eye roll
        if (isInDialogue) {
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