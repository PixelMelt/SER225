package Level;

import Engine.GraphicsHandler;
import Engine.Key;
import Engine.Keyboard;
import GameObject.Frame;
import GameObject.SpriteSheet;
import SpriteFont.SpriteFont;
import java.util.HashMap;

// This class is a base class for all npcs in the game -- all npcs should extend from it
public class NPC extends MapEntity {
    protected boolean isInteractable = false;
    protected boolean talkedTo = false;
    protected SpriteFont message;
    protected int talkedToTime; // how long after talking to NPC will textbox stay open -- use negative number to have it be infinite time
    protected int timer;
    protected Textbox textbox = new Textbox("");
    protected int textboxOffsetX = 0;
    protected int textboxOffsetY = 0;

    protected DialogueSequence dialogueSequence;
    protected DialogueBox dialogueBox;
    protected boolean isInDialogue = false;
    protected boolean spaceWasPressed = false;

    public NPC(float x, float y, SpriteSheet spriteSheet, String startingAnimation) {
        super(x, y, spriteSheet, startingAnimation);
        this.message = createMessage();
        this.dialogueBox = new DialogueBox();
    }

    public NPC(float x, float y, HashMap<String, Frame[]> animations, String startingAnimation) {
        super(x, y, animations, startingAnimation);
        this.message = createMessage();
        this.dialogueBox = new DialogueBox();
    }

    public NPC(float x, float y, Frame[] frames) {
        super(x, y, frames);
        this.message = createMessage();
        this.dialogueBox = new DialogueBox();
    }

    public NPC(float x, float y, Frame frame) {
        super(x, y, frame);
        this.message = createMessage();
        this.dialogueBox = new DialogueBox();
    }

    public NPC(float x, float y) {
        super(x, y);
        this.message = createMessage();
        this.dialogueBox = new DialogueBox();
    }

    protected SpriteFont createMessage() {
        return null;
    }

    public void update(Player player) {
        super.update();
        checkTalkedTo(player);
        textbox.setLocation((int)getCalibratedXLocation() + textboxOffsetX, (int)getCalibratedYLocation() + textboxOffsetY);
    }


    public void checkTalkedTo(Player player) {
        boolean spacePressed = Keyboard.isKeyDown(Key.SPACE);

        if (dialogueSequence != null) {
            if (isInteractable && intersects(player) && spacePressed && !spaceWasPressed) {
                if (!isInDialogue) {
                    // start dialogue
                    isInDialogue = true;
                    dialogueSequence.reset();
                    talkedTo = true;
                    updateDialogueBox();
                } else {
                    // advance dialogue
                    dialogueSequence.advance();
                    if (dialogueSequence.isComplete()) {
                        isInDialogue = false;
                        talkedTo = false;
                    } else {
                        updateDialogueBox();
                    }
                }
            }
        }

        spaceWasPressed = spacePressed;
    }

    // get the first frame of current animation for use as portrait
    protected Frame getPortraitFrame() {
        if (animations != null && animations.containsKey(currentAnimationName)) {
            Frame[] frames = animations.get(currentAnimationName);
            if (frames != null && frames.length > 0) {
                return frames[0];
            }
        }
        return null;
    }

    protected void updateDialogueBox() {
        if (dialogueSequence != null && !dialogueSequence.isComplete()) {
            dialogueBox.setCurrentMessage(dialogueSequence.getCurrentMessage());
        }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
    }

    public void drawDialogueBox(GraphicsHandler graphicsHandler) {
        if (isInDialogue && dialogueSequence != null) {
            dialogueBox.draw(graphicsHandler);
        }
    }

    public boolean hasActiveDialogue() {
        return isInDialogue && dialogueSequence != null;
    }
}
