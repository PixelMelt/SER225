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
        maxRunSpeed = 6f;
        maxPhysicsSpeed = 18f; // Allow grapple swings to go 3x faster than running
        horizontalAcceleration = 1f;
        groundFriction = 0.6f;
        airFriction = 0.5f;
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        // Determine the target point based on grapple state
        Point targetPoint = null;
        float extensionProgress = 1.0f; // Full extension by default

        switch (getGrappleAnimationState()) {
            case EXTENDING -> {
                targetPoint = getGrappleTarget();
                extensionProgress = getGrappleExtensionProgress();
            }
            case ATTACHED -> {
                targetPoint = getGrappleTarget();
                extensionProgress = 1.0f;
            }
            case RETRACTING -> {
                targetPoint = getGrappleTarget();
                extensionProgress = getGrappleExtensionProgress();
            }
            case NONE -> {
            }
        }
        // No grapple to draw

        // Draw neck/rope if in any grapple state
        if (targetPoint != null && extensionProgress > 0) {
            Graphics2D g = graphicsHandler.getGraphics();
            Camera camera = map.getCamera();

            boolean isLeft = getFacingDirection() == Utils.Direction.LEFT;

            // Offset starting position based on facing direction
            float startOffsetX = 0;
            if (getFacingDirection() == Utils.Direction.LEFT) {
                startOffsetX = -8;
            } else if (getFacingDirection() == Utils.Direction.RIGHT) {
                startOffsetX = 8;
            }

            int startX = (int) (getCenterX() + startOffsetX - camera.getX());
            int startY = (int) (getCenterY() - camera.getY());
            int targetX = (int) (targetPoint.x - camera.getX());
            int targetY = (int) (targetPoint.y - camera.getY());

            // Calculate the actual extension distance based on animation progress
            float dx = (targetX - startX) * extensionProgress;
            float dy = (targetY - startY) * extensionProgress;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, dx);

            // Draw neck.png along the rope (measuring tape style)
            java.awt.image.BufferedImage neckImage = ImageLoader.load("neck.png");
            int segmentLength = 15;
            int numSegments = (int) (distance / segmentLength);

            for (int i = 0; i < numSegments; i++) {
                double t = (double) i / numSegments;
                int posX = (int) (startX + dx * t);
                int posY = (int) (startY + dy * t);

                // Rotate and scale the neck image
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(posX, posY);
                g2d.rotate(angle + Math.PI / 2);
                int scaledWidth = neckImage.getWidth() * 3;
                int scaledHeight = neckImage.getHeight() * 3;
                g2d.drawImage(neckImage, -scaledWidth / 2, -scaledHeight / 2, scaledWidth, scaledHeight, null);
                g2d.dispose();
            }

            // Draw head at the current extension point
            String headImageName = (getGrappleAnimationState() == GrappleAnimationState.EXTENDING)
                ? "head_open.png"
                : "head.png";
            java.awt.image.BufferedImage headImage = ImageLoader.load(headImageName);
            int endX = (int) (startX + dx);
            int endY = (int) (startY + dy);
            int offsetForRealsies = headImageName.equals("head_open.png") ? 2 : 5;
            int offsetPixels = isLeft ? offsetForRealsies : -offsetForRealsies;
            int offsetX = (int) (endX - Math.cos(angle + Math.PI / 2) * offsetPixels);
            int offsetY = (int) (endY - Math.sin(angle + Math.PI / 2) * offsetPixels);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(offsetX, offsetY);

            // Adjust rotation and flip based on facing direction
            if (isLeft) {
                g2d.rotate(angle - Math.PI / 2);
                g2d.scale(1, -1);
            } else {
                g2d.rotate(angle + Math.PI / 2);
            }

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

            // Extending animation (same as grapple for now)
            put("GRAPPLE_EXTEND_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("GRAPPLE_EXTEND_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            // Retracting animation (same as grapple for now)
            put("GRAPPLE_RETRACT_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });

            put("GRAPPLE_RETRACT_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(5, 0))
                            .withScale(3)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(3, 3, 17, 19)
                            .build()
            });
        }};
    }
}
