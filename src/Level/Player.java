package Level;

import Engine.Key;
import Engine.KeyLocker;
import Engine.Keyboard;
import Engine.Mouse;
import GameObject.Frame;
import GameObject.GameObject;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Utils.AirGroundState;
import Utils.Direction;
import Utils.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Player extends GameObject {
    // Death animation timer
    private int deathAnimationTimer = 0;
    // Constants
    private static final float CROUCH_FRICTION_MULTIPLIER = 0.8f;
    private static final float LEVEL_COMPLETE_SPEED_MULTIPLIER = 0.5f;

    // Jump timing
    private static final int JUMP_BUFFER_FRAMES = 10;
    private static final int COYOTE_TIME_FRAMES = 7;

    // Animation mappings
    private static final Map<PlayerState, String> STATE_ANIMATIONS = Map.of(
        PlayerState.STANDING, "STAND",
        PlayerState.WALKING, "WALK",
        PlayerState.CROUCHING, "CROUCH"
    );

    // Physics constants, these should be set in a subclass
    protected float maxHorizontalSpeed;
    protected float horizontalAcceleration;
    protected float groundFriction;
    protected float airFriction;
    protected float gravityAcceleration;
    protected float jumpVelocity;
    protected float maxFallSpeed;

    // Velocity values
    protected float velocityX;
    protected float velocityY;

    // Movement amounts for this frame
    protected float moveAmountX, moveAmountY;
    protected float lastAmountMovedX, lastAmountMovedY;

    // Jump state
    private int jumpBufferTimer = 0;
    private int coyoteTimeTimer = 0;
    private boolean jumpedIntoAir = false;

    // State tracking
    protected PlayerState playerState;
    protected PlayerState previousPlayerState;
    protected Direction facingDirection;
    protected AirGroundState airGroundState;
    protected AirGroundState previousAirGroundState;
    protected LevelState levelState;

    // Listeners
    protected List<PlayerListener> listeners = new ArrayList<>();

    // Input handling
    protected KeyLocker keyLocker = new KeyLocker();
    protected Key JUMP_KEY = Key.UP;
    protected Key MOVE_LEFT_KEY = Key.LEFT;
    protected Key MOVE_RIGHT_KEY = Key.RIGHT;
    protected Key CROUCH_KEY = Key.DOWN;

    // Input state
    private final InputState inputState = new InputState();

    // Flags
    protected boolean isInvincible;
    public boolean isOnMovingPlatform;
    protected boolean wantsFallThroughPlatform;

    // Grapple variables
    protected boolean isGrappling = false;
    protected Point grappleTarget;
    protected float ropeLength;
    protected float swingAngle;
    protected float swingVelocity;
    protected float swingAcceleration;
    protected float gravity = 0.4f;

    // Release momentum
    protected boolean applyingReleaseMomentum = false;
    protected float releaseVx = 0f;
    protected float releaseVy = 0f;

    protected final float RELEASE_DAMPING = 0.95f;
    protected final float RELEASE_THRESHOLD = 0.3f;

    public Player(SpriteSheet spriteSheet, float x, float y, String startingAnimationName) {
        super(spriteSheet, x, y, startingAnimationName);
        facingDirection = Direction.RIGHT;
        airGroundState = AirGroundState.AIR;
        previousAirGroundState = airGroundState;
        playerState = PlayerState.STANDING;
        previousPlayerState = playerState;
        levelState = LevelState.RUNNING;
    }

    // Inner class for input state management
    private class InputState {
        boolean moveLeft;
        boolean moveRight;
        boolean jump;
        boolean crouch;

        void update() {
            moveLeft = Keyboard.isKeyDown(MOVE_LEFT_KEY);
            moveRight = Keyboard.isKeyDown(MOVE_RIGHT_KEY);
            jump = Keyboard.isKeyDown(JUMP_KEY);
            crouch = Keyboard.isKeyDown(CROUCH_KEY);
        }
    }

    public void update() {
        moveAmountX = 0;
        moveAmountY = 0;

        switch (levelState) {
            case RUNNING -> updateRunning();
            case LEVEL_COMPLETED -> updateLevelCompleted();
            case PLAYER_DEAD -> updatePlayerDead();
        }

        isOnMovingPlatform = false;
    }

    private void updateRunning() {
        inputState.update();

        // Set fall through platform flag when crouching on ground
        wantsFallThroughPlatform = inputState.crouch && airGroundState == AirGroundState.GROUND;

        updateGrappling();
        updateJumpTimers();
        updatePhysics();

        // Handle state transitions
        do {
            previousPlayerState = playerState;
            handlePlayerState();
        } while (previousPlayerState != playerState);

        if (previousAirGroundState == AirGroundState.GROUND && airGroundState == AirGroundState.AIR && !jumpedIntoAir) {  // Only set coyote time if we didn't jump
            coyoteTimeTimer = COYOTE_TIME_FRAMES;
        }

        // Reset jump flag when landing
        if (previousAirGroundState == AirGroundState.AIR && airGroundState == AirGroundState.GROUND) {
            jumpedIntoAir = false;
            coyoteTimeTimer = 0;
        }

        previousAirGroundState = airGroundState;

        // Calculate movement for this frame
        moveAmountX = velocityX;
        moveAmountY = velocityY;

        // Move with collision detection
        lastAmountMovedX = super.moveXHandleCollision(moveAmountX);
        lastAmountMovedY = super.moveYHandleCollision(moveAmountY);

        handlePlayerAnimation();
        updateLockedKeys();
        super.update();
        // if player falls below the bottom of the map, player dies
        if (map != null) {
            float centerY = getY() + getHeight() / 2f;
            if (centerY >= map.getHeightPixels()) {
                levelState = LevelState.PLAYER_DEAD;
            }
        }
    }

    private void updateJumpTimers() {
        if (jumpBufferTimer > 0) {
            jumpBufferTimer--;
        }
        if (coyoteTimeTimer > 0) {
            coyoteTimeTimer--;
        }

        if (inputState.jump && !keyLocker.isKeyLocked(JUMP_KEY)) {
            jumpBufferTimer = JUMP_BUFFER_FRAMES;
            keyLocker.lockKey(JUMP_KEY);
        }
    }

    private void updatePhysics() {
        applyGravity();
        applyFriction();
        clampVelocities();
    }

    protected void applyGravity() {
        velocityY += gravityAcceleration;
    }

    protected void applyFriction() {
        boolean isOnGround = airGroundState == AirGroundState.GROUND;
        float currentFriction = isOnGround ? groundFriction : airFriction;

        if (velocityX != 0) {
            // Apply friction opposite to the direction of movement
            float newVelocityX = velocityX - Math.signum(velocityX) * currentFriction;
            // Prevent overshooting to the opposite direction
            boolean willCrossZero = Math.signum(newVelocityX) != Math.signum(velocityX);
            // If it would cross zero, just set to zero
            velocityX = willCrossZero ? 0 : newVelocityX;
        }
    }

    // Clamp velocities to their max values
    private void clampVelocities() {
        if (Math.abs(velocityX) > maxHorizontalSpeed) {
            velocityX = Math.signum(velocityX) * maxHorizontalSpeed;
        }
        if (velocityY > maxFallSpeed) {
            velocityY = maxFallSpeed;
        }
    }

    // Clean jump check that combines buffer and coyote time
    private boolean canJump() {
        // Can't jump if we already jumped (but CAN if we just walked off!)
        if (jumpedIntoAir) {
            return false;
        }

        // Check if we have buffered jump input
        boolean hasBufferedJump = jumpBufferTimer > 0;

        // Check if we can actually execute the jump
        boolean onGroundOrCoyoteTime = airGroundState == AirGroundState.GROUND || coyoteTimeTimer > 0;

        return hasBufferedJump && onGroundOrCoyoteTime;
    }

    private void executeJump() {
        velocityY = -jumpVelocity;
        airGroundState = AirGroundState.AIR;
        jumpedIntoAir = true;
        jumpBufferTimer = 0;
        coyoteTimeTimer = 0;
        playerState = PlayerState.JUMPING;

        currentAnimationName = getAnimationName("JUMP");
    }

    protected void handlePlayerState() {
        switch (playerState) {
            case STANDING -> playerStanding();
            case WALKING -> playerWalking();
            case CROUCHING -> playerCrouching();
            case JUMPING -> playerJumping();
        }
    }

    private void applyMovementInput() {
        if (inputState.moveLeft) {
            facingDirection = Direction.LEFT;
            float acceleration = (velocityX > 0) ? horizontalAcceleration * 3.5f : horizontalAcceleration;
            velocityX -= acceleration;
        } else if (inputState.moveRight) {
            facingDirection = Direction.RIGHT;
            float acceleration = (velocityX < 0) ? horizontalAcceleration * 3.5f : horizontalAcceleration;
            velocityX += acceleration;
        }
    }

    protected void playerStanding() {
        if (canJump()) {
            executeJump();
        }
        else if (inputState.moveLeft || inputState.moveRight) {
            playerState = PlayerState.WALKING;
        }
        else if (inputState.crouch) {
            playerState = PlayerState.CROUCHING;
        }
    }

    protected void playerWalking() {
        if (canJump()) {
            executeJump();
        }
        else if (!inputState.moveLeft && !inputState.moveRight) {
            playerState = PlayerState.STANDING;
        }
        else {
            applyMovementInput();
            if (inputState.crouch) {
                playerState = PlayerState.CROUCHING;
            }
        }
    }

    protected void playerCrouching() {
        // Apply extra friction when crouching
        velocityX *= CROUCH_FRICTION_MULTIPLIER;

        if (canJump()) {
            executeJump();
        }
        else if (!inputState.crouch) {
            playerState = PlayerState.STANDING;
        }
    }

    protected void playerJumping() {
        // Handle air movement AND check for coyote time jumps
        if (airGroundState == AirGroundState.AIR) {
            applyMovementInput();

            // Check for coyote time jump while falling!
            if (canJump()) {
                executeJump();  // This will use up the coyote time
            }
        }
        // Handle landing
        else if (airGroundState == AirGroundState.GROUND) {
            // Just landed so check for buffered jump
            if (canJump()) {
                executeJump();
            } else {
                playerState = PlayerState.STANDING;
            }
        }
    }

    protected void updateLockedKeys() {
        if (Keyboard.isKeyUp(JUMP_KEY)) {
            keyLocker.unlockKey(JUMP_KEY);
        }
    }

    // Helper method for animation names
    private String getAnimationName(String action) {
        return action + "_" + (facingDirection == Direction.RIGHT ? "RIGHT" : "LEFT");
    }

    private boolean isInWater() {
        int centerX = Math.round(getBounds().getX1()) + Math.round(getBounds().getWidth() / 2f);
        int centerY = Math.round(getBounds().getY1()) + Math.round(getBounds().getHeight() / 2f);
        MapTile currentMapTile = map.getTileByPosition(centerX, centerY);
        return currentMapTile != null && currentMapTile.getTileType() == TileType.WATER;
    }

    protected void handlePlayerAnimation() {
        // Handle grappling animation first
        if (isGrappling) {
            this.currentAnimationName = getAnimationName("GRAPPLE");
            return;
        }

        String baseAnim = STATE_ANIMATIONS.get(playerState);

        if (baseAnim != null) {
            this.currentAnimationName = getAnimationName(baseAnim);
            // Special case for water when standing
            if (playerState == PlayerState.STANDING && isInWater()) {
                this.currentAnimationName = getAnimationName("SWIM_STAND");
            }
        } else if (playerState == PlayerState.JUMPING) {
            this.currentAnimationName = getAnimationName(velocityY < 0 ? "JUMP" : "FALL");
        }
    }

    protected void updateGrappling() {
        Camera camera = map.getCamera();

        // Momentum carry
        if (applyingReleaseMomentum) {
            moveX(releaseVx);
            moveY(releaseVy);

            // Gradually reduce over time
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

            // Let go if too close
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

    protected void releaseGrapple() {
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

    protected Point findGrappleTarget(float targetX, float targetY) {
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

    protected float getCenterX() { return getX() + getWidth() / 2f; }
    protected float getCenterY() { return getY() + getHeight() / 2f; }

    @Override
    public void onEndCollisionCheckX(boolean hasCollided, Direction direction, MapEntity entityCollidedWith) {
        if (hasCollided) {
            velocityX = 0;
        }
    }

    @Override
    public void onEndCollisionCheckY(boolean hasCollided, Direction direction, MapEntity entityCollidedWith) {
        if (direction == Direction.DOWN) {
            if (hasCollided) {
                velocityY = 0;
                airGroundState = AirGroundState.GROUND;
            } else {
                // Don't set to air if on a moving platform
                if (!isOnMovingPlatform) {
                    playerState = PlayerState.JUMPING;
                    airGroundState = AirGroundState.AIR;
                }
            }
        }
        else if (direction == Direction.UP) {
            if (hasCollided) {
                velocityY = 0;
            }
        }
    }

    public void hurtPlayer(MapEntity mapEntity) {
        if (!isInvincible) {
            if (mapEntity instanceof Enemy) {
                levelState = LevelState.PLAYER_DEAD;
            }
        }
    }

    public void completeLevel() {
        levelState = LevelState.LEVEL_COMPLETED;
    }

    public void updateLevelCompleted() {
        if (airGroundState != AirGroundState.GROUND && map.getCamera().containsDraw(this)) {
            currentAnimationName = "FALL_RIGHT";
            applyGravity();
            moveAmountY = velocityY;
            super.update();
            moveYHandleCollision(moveAmountY);
        }
        else if (map.getCamera().containsDraw(this)) {
            currentAnimationName = "WALK_RIGHT";
            velocityX = maxHorizontalSpeed * LEVEL_COMPLETE_SPEED_MULTIPLIER;
            super.update();
            moveXHandleCollision(velocityX);
        } else {
            notifyLevelCompleted();
        }
    }

    public void updatePlayerDead() {
        // Flip the sprite vertically to indicate death
        Frame[] frames = getCurrentAnimation();
        if (frames != null) {
            for (Frame frame : frames) {
                frame.setImageEffect(ImageEffect.FLIP_VERTICAL);
            }
        }
        // still showing death animation but shorter
        if (deathAnimationTimer < 10) { 
            deathAnimationTimer++;
        } else {
            deathAnimationTimer = 0;
            notifyDeath();
        }
    }

    private void notifyLevelCompleted() {
        for (PlayerListener listener : listeners) {
            listener.onLevelCompleted();
        }
    }

    private void notifyDeath() {
        for (PlayerListener listener : listeners) {
            listener.onDeath();
        }
    }

    // Getters and setters
    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public AirGroundState getAirGroundState() {
        return airGroundState;
    }

    public void setAirGroundState(AirGroundState airGroundState) {
        this.airGroundState = airGroundState;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Direction facingDirection) {
        this.facingDirection = facingDirection;
    }

    public void setLevelState(LevelState levelState) {
        this.levelState = levelState;
    }

    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    public boolean isGrappling() {
        return isGrappling;
    }

    public Point getGrappleTarget() {
        return grappleTarget;
    }

    public boolean wantsFallThroughPlatform() {
        return wantsFallThroughPlatform;
    }
}