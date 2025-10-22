package Level;

import GameObject.Frame;
import java.awt.image.BufferedImage;

public class DialogueMessage {
    private final String text;
    private BufferedImage portrait; // can be null

    public DialogueMessage(String text) {
        this.text = text;
        this.portrait = null;
    }

    public DialogueMessage(String text, Frame portraitFrame) {
        this.text = text;
        this.portrait = portraitFrame != null ? portraitFrame.getImage() : null;
    }

    public DialogueMessage(String text, BufferedImage portrait) {
        this.text = text;
        this.portrait = portrait;
    }

    public String getText() {
        return text;
    }

    public BufferedImage getPortrait() {
        return portrait;
    }

    public boolean hasPortrait() {
        return portrait != null;
    }
}
