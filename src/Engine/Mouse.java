package Engine;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
    private static int mouseX, mouseY;
    private static boolean clicked = false;

    public static int getX() {
        return mouseX;
    }

    public static int getY() {
        return mouseY;
    }

    public static boolean wasClicked() {
        return clicked;
    }

    public static void resetClick() {
        clicked = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        clicked = true;
    }
}
