package Players;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import Engine.Key;
import Engine.Keyboard;
import Engine.Mouse;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.Camera;
import Level.MapTile;
import Level.Player;
import Utils.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

public class Cat extends Player {

    // Grapple variables
    private boolean isGrappling = false;
    private Point grappleTarget;
    private float ropeLength;
    private float swingAngle;
    private float swingVelocity;
    private float swingAcceleration;
    private float gravity = 0.4f;

    // release momentum
    private boolean applyingReleaseMomentum = false;
    private float releaseVx = 0f;
    private float releaseVy = 0f;

    private final float RELEASE_DAMPING = 0.95f; 
    private final float RELEASE_THRESHOLD = 0.3f;

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

        // momentum carry
        if (applyingReleaseMomentum) {
            moveX(releaseVx);
            moveY(releaseVy);

            // gradually reduce over time
            releaseVx *= RELEASE_DAMPING;
            releaseVy *= RELEASE_DAMPING;

            if (Math.abs(releaseVx) < RELEASE_THRESHOLD && Math.abs(releaseVy) < RELEASE_THRESHOLD) {
                applyingReleaseMomentum = false;
            }
        }

        // Start grapple
        if (Mouse.wasClicked()) {
            float worldX = Mouse.getX() + camera.getX();
            float worldY = Mouse.getY() + camera.getY();
            Point hit = findGrappleTarget(worldX, worldY);
            if (hit != null) {
                grappleTarget = hit;
                isGrappling = true;

                float dx = grappleTarget.x - getCenterX();
                float dy = grappleTarget.y - getCenterY();
                ropeLength = (float) Math.sqrt(dx * dx + dy * dy);

                swingAngle = (float) Math.atan2(getCenterX() - grappleTarget.x, getCenterY() - grappleTarget.y);
                swingVelocity = 0f;

                applyingReleaseMomentum = false;
                releaseVx = 0;
                releaseVy = 0;
            }
            Mouse.resetClick();
        }

        // Cancel grapple
        if (isGrappling && (Keyboard.isKeyDown(Key.UP) || Mouse.wasClicked())) {
            releaseGrapple();
        }

        // Grapple physics
        if (isGrappling && grappleTarget != null) {
            if (ropeLength <= 1f) {
                releaseGrapple();
                return;
            }

            swingAcceleration = (-gravity / ropeLength) * (float) Math.sin(swingAngle);
            if (Keyboard.isKeyDown(Key.LEFT)) swingAcceleration -= 0.002;
            if (Keyboard.isKeyDown(Key.RIGHT)) swingAcceleration += 0.002;

            swingVelocity += swingAcceleration;
            swingVelocity *= 0.995f;
            swingAngle += swingVelocity;

            float targetCenterX = grappleTarget.x + (float) (ropeLength * Math.sin(swingAngle));
            float targetCenterY = grappleTarget.y + (float) (ropeLength * Math.cos(swingAngle));
            float newX = targetCenterX - getWidth() / 2f;
            float newY = targetCenterY - getHeight() / 2f;

            // Lets go if too close
            float distToHook = (float) Math.sqrt(
                Math.pow(grappleTarget.x - getCenterX(), 2) + Math.pow(grappleTarget.y - getCenterY(), 2)
            );
            if (distToHook < 18f) {
                releaseGrapple();
                return;
            }

            // Collision check
            MapTile tileAtNewX = map.getTileByPosition((int) (newX + getWidth() / 2f), (int) getCenterY());
            MapTile tileAtNewY = map.getTileByPosition((int) getCenterX(), (int) (newY + getHeight() / 2f));

            boolean collided = false;
            if (tileAtNewX != null && tileAtNewX.isSolid()) collided = true;
            if (tileAtNewY != null && tileAtNewY.isSolid()) collided = true;

            if (collided) {
                releaseGrapple();
                return;
            }

            setX(newX);
            setY(newY);
        }
    }

    private void releaseGrapple() {
        if (!isGrappling) return;

        // Give player momentum when releasing
        float impulseX = (float) (ropeLength * swingVelocity * Math.cos(swingAngle));
        float impulseY = (float) (-ropeLength * swingVelocity * Math.sin(swingAngle));

        releaseVx = impulseX;
        releaseVy = impulseY;
        applyingReleaseMomentum = true;

        isGrappling = false;
        grappleTarget = null;
        ropeLength = 0f;
        swingVelocity = 0f;
        swingAcceleration = 0f;
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);

        if (isGrappling && grappleTarget != null) {
            Graphics2D g = graphicsHandler.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));

            Camera camera = map.getCamera();
            int startX = (int) (getCenterX() - camera.getX());
            int startY = (int) (getCenterY() - camera.getY());
            int targetX = (int) (grappleTarget.x - camera.getX());
            int targetY = (int) (grappleTarget.y - camera.getY());

            g.drawLine(startX, startY, targetX, targetY);
        }
    }

    private Point findGrappleTarget(float targetX, float targetY) {
        if (map == null) return null;

        double dx = targetX - getCenterX();
        double dy = targetY - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        int steps = Math.max(1, (int) (distance / 4));
        double stepX = dx / steps;
        double stepY = dy / steps;

        for (int i = 0; i <= steps; i++) {
            int checkX = (int) (getCenterX() + stepX * i);
            int checkY = (int) (getCenterY() + stepY * i);

            MapTile tile = map.getTileByPosition(checkX, checkY);
            if (tile != null && tile.isSolid()) {
                return new Point(
                        tile.getX() + tile.getWidth() / 2,
                        tile.getY() + tile.getHeight() / 2
                );
            }
        }
        return null;
    }

    private float getCenterX() { return getX() + getWidth() / 2f; }
    private float getCenterY() { return getY() + getHeight() / 2f; }

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
        }};
    }
}
