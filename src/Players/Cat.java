package Players;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.Camera;
import Level.Player;
import Utils.Point;

import java.awt.Graphics2D;
import java.util.HashMap;

public class Cat extends Player {

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
    public void draw(GraphicsHandler graphicsHandler) {
        // Draw neck/rope first if grappling
        if (isGrappling() && getGrappleTarget() != null) {
            Point grappleTarget = getGrappleTarget();
            Graphics2D g = graphicsHandler.getGraphics();
            Camera camera = map.getCamera();

            // Offset starting position based on facing direction
            float startOffsetX = 0;
            if (getFacingDirection() == Utils.Direction.LEFT) {
                startOffsetX = -8; // Offset to the left
            } else if (getFacingDirection() == Utils.Direction.RIGHT) {
                startOffsetX = 8; // Offset to the right
            }

            int startX = (int) (getCenterX() + startOffsetX - camera.getX());
            int startY = (int) (getCenterY() - camera.getY());
            int targetX = (int) (grappleTarget.x - camera.getX());
            int targetY = (int) (grappleTarget.y - camera.getY());

            // Calculate distance and angle
            float dx = targetX - startX;
            float dy = targetY - startY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, dx);

            // Draw neck.png every 50 units along the rope
            java.awt.image.BufferedImage neckImage = ImageLoader.load("neck.png");
            int segmentLength = 15;
            int numSegments = (int) (distance / segmentLength);

            for (int i = 0; i < numSegments; i++) {
                double t = (double) i / numSegments;
                int posX = (int) (startX + dx * t);
                int posY = (int) (startY + dy * t);

                // Rotate and draw the neck image (90 degrees + rope angle, scaled 5x)
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(posX, posY);
                g2d.rotate(angle + Math.PI / 2);
                int scaledWidth = neckImage.getWidth() * 3;
                int scaledHeight = neckImage.getHeight() * 3;
                g2d.drawImage(neckImage, -scaledWidth / 2, -scaledHeight / 2, scaledWidth, scaledHeight, null);
                g2d.dispose();
            }

            // Draw head.png at the end (grapple target), offset by 3 scaled pixels (rotated 90deg right)
            java.awt.image.BufferedImage headImage = ImageLoader.load("head.png");
            int offsetPixels = -5; // 3 pixels * 3 scale = 9 pixels
            int offsetX = (int) (targetX - Math.cos(angle + Math.PI / 2) * offsetPixels);
            int offsetY = (int) (targetY - Math.sin(angle + Math.PI / 2) * offsetPixels);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(offsetX, offsetY);
            g2d.rotate(angle + Math.PI / 2);
            int headWidth = headImage.getWidth() * 3;
            int headHeight = headImage.getHeight() * 3;
            g2d.drawImage(headImage, -headWidth / 2, -headHeight / 2, headWidth, headHeight, null);
            g2d.dispose();
        }

        // Draw player sprite on top
        super.draw(graphicsHandler);
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

            put("CROUCH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(4, 0))
                            .withScale(3)
                            .withBounds(3, 5, 17, 17)
                            .build()
            });

            put("CROUCH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(4, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 5, 17, 17)
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

            put("GRAPPLE_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("GRAPPLE_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });
        }};
    }
}
