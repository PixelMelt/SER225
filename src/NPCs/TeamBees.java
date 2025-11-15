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

// This class is for the trademarked TEAM BEES NPC
public class TeamBees extends NPC {

    public TeamBees(Point location) {
        super(location.x, location.y, new SpriteSheet(ImageLoader.load("TeamBees.png"), 24, 24), "Bee1");
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
        dialogueSequence.addMessage("I am the trademarked bee of TeamBees.", npcPortrait);
        dialogueSequence.addMessage("Well, nice to meet you I'm Gertrude.", playerPortrait);
        dialogueSequence.addMessage("I can't beelieve that you are the Gertrude.", npcPortrait);
        dialogueSequence.addMessage("Why, yes I am.", playerPortrait);
        dialogueSequence.addMessage("Hive never been happier to meet you.\nBut I bet you got to bee going now.", npcPortrait);
        dialogueSequence.addMessage("I bee-lieve you are right. *waddles off*", playerPortrait);
    }

    public void update(Player player) {
        // while npc is being talked to, it frowns
        if (isInDialogue) {
            currentAnimationName = "Bee1";
        } else {
            currentAnimationName = "Bee2";
        }

        super.update(player);
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
           put("Bee1", new Frame[] {
                   new FrameBuilder(spriteSheet.getSprite(0, 0))
                           .withScale(4)
                           .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                           .build()
           });
            put("Bee2", new Frame[] {
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