package EnhancedMapTiles;

import Builders.FrameBuilder;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.SpriteSheet;
import Level.EnhancedMapTile;
import Level.Player;
import Level.TileType;
import Utils.Point;

import java.util.HashMap;

public class BreakBox extends EnhancedMapTile {
    private static final float BREAK_VELOCITY_THRESHOLD = 0.1f;
    private static final int ANIMATION_FRAME_DELAY = 4;

    private boolean isBroken = false;
    private boolean isBreaking = false;
    private int breakAnimationFrame = 0;
    private int animationCounter = 0;

    public BreakBox(Point location) {
        super(location.x, location.y,
              new SpriteSheet(ImageLoader.load("BreakBox.png"), 16, 16),
              TileType.NOT_PASSABLE);
    }

    @Override
    public void initialize() {
        super.initialize();
        isBroken = false;
        isBreaking = false;
        breakAnimationFrame = 0;
        animationCounter = 0;
        this.currentAnimationName = "DEFAULT";
        this.tileType = TileType.NOT_PASSABLE;
    }

    @Override
    public void update(Player player) {
        super.update(player);

        if (isBroken) {
            return;
        }

        if (isBreaking) {
            animationCounter++;
            if (animationCounter >= ANIMATION_FRAME_DELAY) {
                animationCounter = 0;
                breakAnimationFrame++;

                if (breakAnimationFrame == 1) {
                    this.currentAnimationName = "BREAK_1";
                } else if (breakAnimationFrame == 2) {
                    this.currentAnimationName = "BREAK_2";
                } else if (breakAnimationFrame == 3) {
                    this.currentAnimationName = "BREAK_3";
                } else if (breakAnimationFrame >= 4) {
                    this.currentAnimationName = "BLANK";
                    isBroken = true;
                    isBreaking = false;
                }
            }
            return;
        }

        if (touching(player)) {
            float velocityX = player.getVelocityX();
            float velocityY = player.getVelocityY();
            float velocityMagnitude = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);

            if (velocityMagnitude > BREAK_VELOCITY_THRESHOLD) {
                isBreaking = true;
                breakAnimationFrame = 0;
                animationCounter = 0;
                this.tileType = TileType.PASSABLE;
            }
        }
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
            put("DEFAULT", new Frame[] {
                new FrameBuilder(spriteSheet.getSprite(0, 0))
                    .withScale(3)
                    .withBounds(0, 0, 16, 16)
                    .build()
            });

            put("BREAK_1", new Frame[] {
                new FrameBuilder(spriteSheet.getSprite(0, 1))
                    .withScale(3)
                    .build()
            });

            put("BREAK_2", new Frame[] {
                new FrameBuilder(spriteSheet.getSprite(0, 2))
                    .withScale(3)
                    .build()
            });

            put("BREAK_3", new Frame[] {
                new FrameBuilder(spriteSheet.getSprite(0, 3))
                    .withScale(3)
                    .build()
            });

            put("BLANK", new Frame[] {
                new FrameBuilder(spriteSheet.getSprite(0, 4))
                    .withScale(3)
                    .build()
            });
        }};
    }
}
