package Players;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import Engine.Mouse;
import Engine.Keyboard;
import Engine.Key;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.Player;
import Level.Map;
import Level.Camera;
import Level.MapTile;
import Utils.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.HashMap;

public class Cat extends Player {

    // Grapple variables
    private boolean isGrappling = false;
    private Point grappleTarget;

    public Cat(float x, float y) {
        super(new SpriteSheet(ImageLoader.load("Cat.png"), 24, 24), x, y, "STAND_RIGHT");
        gravityAcceleration = 0.5f;
        maxFallSpeed = 8f;
        jumpVelocity = 15f;
        maxHorizontalSpeed = 6f;
        horizontalAcceleration = 1f;
        groundFriction = 0.6f;
        airFriction = 0.5f;
    }

    @Override
    public void update() {
        super.update();

        Camera camera = map.getCamera();

        // Start grapple on left click
        if (Mouse.wasClicked()) {
            // Convert mouse screen coordinates into world coordinates
            float worldX = Mouse.getX() + camera.getX();
            float worldY = Mouse.getY() + camera.getY();

            // Only allow grapple if a solid tile exists along the path
            grappleTarget = findGrappleTarget(worldX, worldY);
            isGrappling = (grappleTarget != null);
            Mouse.resetClick();
        }

        // Cancel grapple if player moves (left, right, jump)
        if (isGrappling) {
            if (Keyboard.isKeyDown(Key.LEFT) || Keyboard.isKeyDown(Key.RIGHT) || Keyboard.isKeyDown(Key.UP)) {
                isGrappling = false;
                grappleTarget = null;
            }
        }

        // Pull toward grapple target
        if (isGrappling && grappleTarget != null) {
            double dx = grappleTarget.x - getCenterX();
            double dy = grappleTarget.y - getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            // RELEASE if too close to avoid jitter
            if (distance < 60) {
                isGrappling = false;
                grappleTarget = null;
            } else {
                double speed = 12.0; // strong pull
                this.setX(this.getX() + (float)(dx / distance * speed));
                this.setY(this.getY() + (float)(dy / distance * speed));
            }
        }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);

        if (isGrappling && grappleTarget != null) {
            Graphics2D g = graphicsHandler.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4)); // thicker line

            Camera camera = map.getCamera();

            // anchor line to the *center* of the cat’s current bounding box
            int startX = (int) (getCenterX() - camera.getX());
            int startY = (int) (getCenterY() - camera.getY());

            // draw to grapple target in screen space
            int targetX = (int) (grappleTarget.x - camera.getX());
            int targetY = (int) (grappleTarget.y - camera.getY());

            g.drawLine(startX, startY, targetX, targetY);
        }
    }

    // Find nearest solid tile between cat and mouse click
    private Point findGrappleTarget(float targetX, float targetY) {
        if (map == null) return null;

        double dx = targetX - getCenterX();
        double dy = targetY - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        int steps = Math.max(1, (int)(distance / 4)); // avoid division by zero
        double stepX = dx / steps;
        double stepY = dy / steps;

        for (int i = 0; i <= steps; i++) {
            int checkX = (int)(getCenterX() + stepX * i);
            int checkY = (int)(getCenterY() + stepY * i);

            MapTile tile = map.getTileByPosition(checkX, checkY);
            if (tile != null && tile.isSolid()) {
                // Hook to the center of this solid tile
                return new Point(
                    tile.getX() + tile.getWidth() / 2,
                    tile.getY() + tile.getHeight() / 2
                );
            }
        }
        return null; // no solid tile found
    }

    // Helper to get center X of the cat sprite
    private float getCenterX() {
        return this.getX() + this.getWidth() / 2f;
    }

    // Helper to get center Y of the cat sprite
    private float getCenterY() {
        return this.getY() + this.getHeight() / 2f;
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
            put("STAND_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("STAND_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("WALK_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 14)
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 1), 14)
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 14)
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 3), 14)
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("WALK_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 14)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 1), 14)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 14)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 3), 14)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("JUMP_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(2, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("JUMP_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(2, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("FALL_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("FALL_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            // FIXED CROUCH BOUNDS
            put("CROUCH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(4, 0))
                            .withScale(3)
                            .withBounds(3, 10, 17, 12) // aligned X and width, shorter height
                            .build()
            });

            put("CROUCH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(4, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 10, 17, 12) // aligned X and width, shorter height
                            .build()
            });

            put("DEATH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0), 8)
                            .withScale(3)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(5, 1), 8)
                            .withScale(3)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(5, 2), -1)
                            .withScale(3)
                            .build()
            });

            put("DEATH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0), 8)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(5, 1), 8)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(5, 2), -1)
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build()
            });

            put("SWIM_STAND_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(6, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("SWIM_STAND_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(6, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });
        }};
    }
}
