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
import GameObject.SpriteSheet;
import java.util.HashMap;
import GameObject.ImageEffect;

// stationary spike kills player on contact
public class Spike extends EnhancedMapTile {

    public Spike(Point location) {
        // default orientation
        super(location.x, location.y, createSpikeFrame(ImageEffect.NONE), TileType.PASSABLE);
    }

    public Spike(Point location, boolean flippedVertical) {
        super(location.x, location.y, createSpikeFrame(flippedVertical ? ImageEffect.FLIP_VERTICAL : ImageEffect.NONE), TileType.PASSABLE);
    }

    @Override
    public void update(Player player) {
        super.update(player);
        if (intersects(player)) {
            player.setLevelState(LevelState.PLAYER_DEAD);
        }
    }

    private static Frame createSpikeFrame(ImageEffect effect) {
        // create image the size of a single tile so the bottom aligns with the tile bottom
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            // transparent background
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, size, size);
            g.setComposite(AlphaComposite.SrcOver);
            // three spikes
            g.setColor(new Color(255, 0, 0));
            int peakY = 2;
            int baseY = size - 1; // draw the spike bases at the bottom of the image
            int spikeWidth = size / 3;
            for (int i = 0; i < 3; i++) {
                int x0 = i * spikeWidth;
                int x1 = x0 + spikeWidth / 2;
                int x2 = x0 + spikeWidth;
                int[] xs = new int[] { x0 + 1, x1, x2 - 1 };
                int[] ys = new int[] { baseY, peakY, baseY };
                g.fillPolygon(xs, ys, 3);
                // outline
                // g.setColor(Color.BLACK);
                g.drawPolygon(xs, ys, 3);
            }
        } finally {
            g.dispose();
        }

        // smaller hitbox than the visible sprite
        Frame frame = new FrameBuilder(img)
                .withScale(3)
                // move hitbox down so collisions happen near the bottom of the tile
                .withBounds(2, 4, 12, 10)
                .withImageEffect(effect)
                .build();
        return frame;
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet ignored) {
        return new HashMap<String, Frame[]>() {{
            put("DEFAULT", new Frame[] { createSpikeFrame(ImageEffect.NONE) });
        }};
    }
}
