package Level;

import GameObject.Frame;
import java.util.ArrayList;
import java.util.List;

public class DialogueSequence {
    private final List<DialogueMessage> messages;
    private int currentIndex;

    public DialogueSequence() {
        this.messages = new ArrayList<>();
        this.currentIndex = 0;
    }

    public void addMessage(String text) {
        messages.add(new DialogueMessage(text));
    }

    public void addMessage(String text, Frame portraitFrame) {
        messages.add(new DialogueMessage(text, portraitFrame));
    }

    public void addMessage(DialogueMessage message) {
        messages.add(message);
    }

    public DialogueMessage getCurrentMessage() {
        if (currentIndex < messages.size()) {
            return messages.get(currentIndex);
        }
        return null;
    }

    public void advance() {
        if (currentIndex < messages.size()) {
            currentIndex++;
        }
    }

    public boolean isComplete() {
        return currentIndex >= messages.size();
    }

    public void reset() {
        currentIndex = 0;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalMessages() {
        return messages.size();
    }

    public List<DialogueMessage> getMessages() {
        return messages;
    }
}
