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

// This class is for the Duck
public class CrystalGoose extends NPC {

    public CrystalGoose(Point location) {
        super(location.x, location.y, new SpriteSheet(ImageLoader.load("CrystalGoose.png"), 24, 24), "closed");
        isInteractable = true;

        // Get portrait frames
        Frame npcPortrait = getPortraitFrame();
        SpriteSheet playerSheet = new SpriteSheet(ImageLoader.load("Cat.png"), 24, 24);
        Frame playerPortrait = new Builders.FrameBuilder(playerSheet.getSprite(0, 0))
                .withScale(1)
                .withBounds(3, 3, 17, 19)
                .build();

        // Setup dialogue sequence
        dialogueSequence = new Level.DialogueSequence();
        dialogueSequence.addMessage("Welcome, I predict you will have a crumby time", npcPortrait);
        dialogueSequence.addMessage("You must be a duck psychic, don't bread me mind.", playerPortrait);
        dialogueSequence.addMessage("You can crust me not to.", npcPortrait);
        dialogueSequence.addMessage("*waddles backwards, wings covering ears. honking in bad bread puns*", playerPortrait);
    }

    public void update(Player player) {
        // while npc is being talked to, it does an eye roll
        if (isInDialogue) {
            currentAnimationName = "closed";
        } else {
            currentAnimationName = "open";
        }

        super.update(player);
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
           put("closed", new Frame[] {
                   new FrameBuilder(spriteSheet.getSprite(0, 0))
                           .withScale(4)
                           .build()
           });
            put("open", new Frame[] {
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