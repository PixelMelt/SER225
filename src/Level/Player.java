
// On what makes certain movement schemes fitting for certain games

// Mario Bros has long, horizontal areas with elements that are usually
// fairly spaced out, and travel occurs generally in one direction.
// Acceleration makes sense because you're in the best flow state when
// you're moving forward unimpeded. Instant momentum would remove the
// satisfaction from moving at max speed only because you didn't hit
// anything or have to turn around, you wouldn't be as rewarded for
// playing skillfully. Hollow Knight, conversely, has much more compact
// rooms that feature more vertical movement and generally closer spacing
// of enemies, platforms, etc. It wouldn't make sense to include acceleration,
// especially to the degree of Mario, since it'd constantly be interrupted
// by zigzagging movement or fights with higher-health enemies and the player
// would just get frustrated by not being allowed to play at full speed.
// You also aren't limited to a single speed once you get the two dash
// abilities, neither of which Mario has an equivalent to.


package Level;

import Engine.Key;
import Engine.KeyLocker;
import Engine.Keyboard;
import GameObject.GameObject;
import GameObject.SpriteSheet;
import Utils.AirGroundState;
import Utils.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Player extends GameObject {
    // Constants
    private static final float CROUCH_FRICTION_MULTIPLIER = 0.8f;
    private static final float LEVEL_COMPLETE_SPEED_MULTIPLIER = 0.5f;
    private static final float DEATH_BOUNCE_VELOCITY = 10f;

    // Animation mappings
    private static final Map<PlayerState, String> STATE_ANIMATIONS = Map.of(
        PlayerState.STANDING, "STAND",
        PlayerState.WALKING, "WALK",
        PlayerState.CROUCHING, "CROUCH"
    );

    // Physics constants - these should be set in a subclass
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

    // Jump buffering
    protected boolean jumpBuffer;

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

    // Input state cache
    private final InputState inputState = new InputState();

    // Flags
    protected boolean isInvincible;

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
    }

    private void updateRunning() {
        inputState.update();

        updateJumpBuffer();
        updatePhysics();

        // Handle state transitions
        do {
            previousPlayerState = playerState;
            handlePlayerState();
        } while (previousPlayerState != playerState);

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
    }

    private void updateJumpBuffer() {
        if (inputState.jump && !keyLocker.isKeyLocked(JUMP_KEY)) {
            if (!jumpBuffer) { // Only set buffer if it's not already set
                jumpBuffer = true;
            }
        } else if (jumpBuffer) {
            consumeJump();
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

    protected void handlePlayerState() {
        switch (playerState) {
            case STANDING -> playerStanding();
            case WALKING -> playerWalking();
            case CROUCHING -> playerCrouching();
            case JUMPING -> playerJumping();
        }
    }

    // Helper method to check and handle jump from any ground state
    private boolean checkAndHandleJump() {
        if (jumpBuffer && canJump()) {
            consumeJump();
            playerState = PlayerState.JUMPING;
            return true;
        }
        return false;
    }

    private void consumeJump() {
        jumpBuffer = false;
        keyLocker.lockKey(JUMP_KEY);
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
        if (inputState.moveLeft || inputState.moveRight) {
            playerState = PlayerState.WALKING;
        }
        else if (!checkAndHandleJump() && inputState.crouch) {
            playerState = PlayerState.CROUCHING;
        }
    }

    protected void playerWalking() {
        if (!inputState.moveLeft && !inputState.moveRight) {
            playerState = PlayerState.STANDING;
        } else {
            applyMovementInput();
        }

        if (!checkAndHandleJump() && inputState.crouch) {
            playerState = PlayerState.CROUCHING;
        }
    }

    protected void playerCrouching() {
        // Apply extra friction when crouching
        velocityX *= CROUCH_FRICTION_MULTIPLIER;

        if (!inputState.crouch) {
            playerState = PlayerState.STANDING;
        }

        checkAndHandleJump();
    }

    protected void playerJumping() {
        // Initial jump - should only happen once when entering JUMPING state
        if (previousAirGroundState == AirGroundState.GROUND && airGroundState == AirGroundState.GROUND) {
            currentAnimationName = getAnimationName("JUMP");
            airGroundState = AirGroundState.AIR;
            velocityY = -jumpVelocity;
            consumeJump();
        }

        // Air control
        else if (airGroundState == AirGroundState.AIR) {
            applyMovementInput();
        }
        // Landing
        else if (previousAirGroundState == AirGroundState.AIR && airGroundState == AirGroundState.GROUND) {
            // Check if jump is buffered for immediate re-jump
            if (jumpBuffer && !keyLocker.isKeyLocked(JUMP_KEY)) {
                // Stay in jumping state for immediate re-jump
                velocityY = -jumpVelocity;
                airGroundState = AirGroundState.AIR;
                consumeJump();
            } else {
                playerState = PlayerState.STANDING;
            }
        }
    }

    // Helper method to check if player can jump
    private boolean canJump() {
        return (airGroundState == AirGroundState.GROUND)
               && !keyLocker.isKeyLocked(JUMP_KEY);
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
                playerState = PlayerState.JUMPING;
                airGroundState = AirGroundState.AIR;
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
        // Set death animation on first frame of death
        if (!currentAnimationName.startsWith("DEATH")) {
            currentAnimationName = getAnimationName("DEATH");
            // Reset y velocity for death animation
            velocityY = -DEATH_BOUNCE_VELOCITY;
            velocityX = velocityX / 2; // Keep some horizontal momentum
            super.update();
        }
        // Continue playing death animation
        else if (getCurrentAnimation() != null && currentFrameIndex != getCurrentAnimation().length - 1) {
            super.update();
        }
        // After death animation completes, fall off screen using velocity system
        else if (getCurrentAnimation() != null && currentFrameIndex == getCurrentAnimation().length - 1) {
            if (map.getCamera().containsDraw(this)) {
                // Apply gravityAcceleration for natural falling
                applyGravity();
                // Move using velocity system
                moveAmountY = velocityY;
                moveAmountX = velocityX;
                // Apply the movement without collision checking for death fall
                moveY(moveAmountY);
                moveX(moveAmountX);
            } else {
                // Player has fallen off screen, notify listeners
                notifyDeath();
            }
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
}