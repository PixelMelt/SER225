package EnhancedMapTiles;

import Builders.FrameBuilder;
import GameObject.Frame;
import Level.EnhancedMapTile;
import Level.LevelState;
import Level.Player;
import Level.TileType;
import Utils.Point;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import GameObject.SpriteSheet;

// buzzsaw main frame
public class Buzzsaw extends EnhancedMapTile {

    public Buzzsaw(Point location) {
        super(location.x, location.y, makeFrames(), TileType.PASSABLE);
    }

    private static Frame[] makeFrames() {
        return new Frame[] {
            createFrame(0), createFrame(1), createFrame(2), createFrame(3),
            createFrame(4), createFrame(5), createFrame(6), createFrame(7)
        };
    }

    @Override
    public void update(Player player) {
        super.update(player);
        if (intersects(player)) {
            player.setLevelState(LevelState.PLAYER_DEAD);
        }
    }

    private static Frame createFrame(int rotateStep) {
        int size = 32;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            // clear frame
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, size, size);
            g.setComposite(AlphaComposite.SrcOver);

            // teeth of buzzsaw (render)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = size / 2;
            int cy = size / 2;
            int rInner = 12; //  circular body 
            int rTip = 18; // teeth radius 

            // draw main gray body
            g.setColor(new Color(170, 170, 170));
            g.fillOval(cx - rInner, cy - rInner, rInner * 2, rInner * 2);

            // draw triangular teeth around circle
            int teeth = 12;
            g.setColor(new Color(110, 110, 110));
            for (int i = 0; i < teeth; i++) {
                double angle = (i * 2 * Math.PI / teeth) + (rotateStep * (Math.PI * 2 / (teeth * 8)));
                double mid = angle;
                // teeth of buzzsaw
                int tx = cx + (int) Math.round(Math.cos(mid) * rTip);
                int ty = cy + (int) Math.round(Math.sin(mid) * rTip);
                // placing them left and right
                double leftA = mid - (Math.PI / teeth) * 0.6;
                double rightA = mid + (Math.PI / teeth) * 0.6;
                int bx1 = cx + (int) Math.round(Math.cos(leftA) * rInner);
                int by1 = cy + (int) Math.round(Math.sin(leftA) * rInner);
                int bx2 = cx + (int) Math.round(Math.cos(rightA) * rInner);
                int by2 = cy + (int) Math.round(Math.sin(rightA) * rInner);
                int[] xs = new int[] { bx1, tx, bx2 };
                int[] ys = new int[] { by1, ty, by2 };
                g.fillPolygon(xs, ys, 3);
            }

            // draw outer stroke
            g.setColor(new Color(80, 80, 80));
            g.setStroke(new BasicStroke(1f));
            g.drawOval(cx - rInner, cy - rInner, rInner * 2, rInner * 2);

            // inner part
            g.setColor(new Color(100, 100, 100));
            g.fillOval(cx - 2, cy - 2, 4, 4);

        } finally {
            g.dispose();
        }

    // non-zero delay
    Frame f = new FrameBuilder(img, 6)
        .withScale(1.5f)
        .withBounds(4, 4, 24, 24)
        .build();
        return f;
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet ignored) {
        // rotating animation of 8 frames
        return new HashMap<String, Frame[]>() {{
            put("DEFAULT", new Frame[] {
                createFrame(0), createFrame(1), createFrame(2), createFrame(3),
                createFrame(4), createFrame(5), createFrame(6), createFrame(7)
            });
        }};
    }
}
