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
import Utils.SoundController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Player extends GameObject {
    // Death animation timer
    private int deathAnimationTimer = 0;
    // Constants
    private static final float CROUCH_FRICTION_MULTIPLIER = 0.8f;
    private static final float LEVEL_COMPLETE_SPEED_MULTIPLIER = 0.5f;

    // Jump timing
    private static final int JUMP_BUFFER_FRAMES = 10;
    private static final int COYOTE_TIME_FRAMES = 7;
    private static final float JUMP_RELEASE_MULTIPLIER = 0.9f;

    // Animation mappings
    private static final Map<PlayerState, String> STATE_ANIMATIONS = Map.of(
        PlayerState.STANDING, "STAND",
        PlayerState.WALKING, "WALK",
        PlayerState.CROUCHING, "CROUCH"
    );

    // Physics constants, these should be set in a subclass
    protected float maxRunSpeed;
    protected float maxPhysicsSpeed;
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
    private boolean isHoldingJump = false;

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

    protected Key NODE_KEY = Key.X;
    protected Key HONK_KEY = Key.Z;
    protected static final int NODE_TILE_INDEX = 3;
    private static final int BITTEN_NODE_TILE_INDEX = 4; // new tile for bitten node
    protected static final float NODE_RADIUS = 1000f;

    private final InputState inputState = new InputState();
    protected boolean isInvincible;
    public boolean isOnMovingPlatform;
    protected boolean wantsFallThroughPlatform;

    protected boolean isGrappling = false;
    protected Point grappleTarget;
    protected float ropeLength;
    protected float swingAngle;
    protected float swingVelocity;
    protected float swingAcceleration;
    protected float gravity = 0.4f;

    private final Map<String, Long> inactiveNodes = new HashMap<>();
    private static final long NODE_COOLDOWN_MS = 3000; // 3s

    protected enum GrappleAnimationState {
        NONE,
        EXTENDING,
        ATTACHED,
        RETRACTING
    }

    protected GrappleAnimationState grappleAnimationState = GrappleAnimationState.NONE;
    protected int grappleAnimationFrame = 0;
    protected static final int GRAPPLE_ANIMATION_MAX_FRAMES = 8;

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

        SoundController.getInstance().loadSound("goose", "Resources/Audio/SFX/goose.wav");
    }

    private class InputState {
        boolean moveLeft;
        boolean moveRight;
        boolean jump;
        boolean crouch;

        void update() {
            moveLeft = Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(Key.A);
            moveRight = Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(Key.D);
            jump = Keyboard.isKeyDown(JUMP_KEY) || Keyboard.isKeyDown(Key.W);
            crouch = Keyboard.isKeyDown(CROUCH_KEY) || Keyboard.isKeyDown(Key.S);
        }
    }

    @Override
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
        restoreInactiveNodesIfReady(); // Restore nodes
        inputState.update();

        wantsFallThroughPlatform = inputState.crouch && airGroundState == AirGroundState.GROUND;

        updateGrappling();
        updateJumpTimers();
        updatePhysics();

        do {
            previousPlayerState = playerState;
            handlePlayerState();
        } while (previousPlayerState != playerState);

        if (previousAirGroundState == AirGroundState.GROUND && airGroundState == AirGroundState.AIR && !jumpedIntoAir) {
            coyoteTimeTimer = COYOTE_TIME_FRAMES;
        }

        if (previousAirGroundState == AirGroundState.AIR && airGroundState == AirGroundState.GROUND) {
            jumpedIntoAir = false;
            coyoteTimeTimer = 0;
        }

        previousAirGroundState = airGroundState;

        moveAmountX = velocityX;
        moveAmountY = velocityY;

        lastAmountMovedX = super.moveXHandleCollision(moveAmountX);
        lastAmountMovedY = super.moveYHandleCollision(moveAmountY);

        handlePlayerAnimation();
        updateLockedKeys();
        super.update();

        if (map != null) {
            float centerY = getY() + getHeight() / 2f;
            if (centerY >= map.getHeightPixels()) {
                levelState = LevelState.PLAYER_DEAD;
            }
        }
    }

    // visuals of inactive nodes
    private void restoreInactiveNodesIfReady() {
        if (map == null || inactiveNodes.isEmpty()) return;
        long now = System.currentTimeMillis();
        List<String> expired = new ArrayList<>();
        for (var e : inactiveNodes.entrySet()) {
            if (now - e.getValue() >= NODE_COOLDOWN_MS) {
                String[] parts = e.getKey().split(",");
                int tx = Integer.parseInt(parts[0]);
                int ty = Integer.parseInt(parts[1]);

                tx = Math.max(0, Math.min(map.getWidth() - 1, tx));
                ty = Math.max(0, Math.min(map.getHeight() - 1, ty));

                MapTile tile = map.getMapTile(tx, ty);
                if (tile != null && tile.getTileIndex() == BITTEN_NODE_TILE_INDEX) {
                    map.setTileIndex(tx, ty, NODE_TILE_INDEX);
                }
                expired.add(e.getKey());
            }
        }
        for (String key : expired) inactiveNodes.remove(key);
    }

    private void updateJumpTimers() {
        if (jumpBufferTimer > 0) jumpBufferTimer--;
        if (coyoteTimeTimer > 0) coyoteTimeTimer--;

        isHoldingJump = inputState.jump;
        if (inputState.jump && !keyLocker.isKeyLocked(JUMP_KEY)) {
            jumpBufferTimer = JUMP_BUFFER_FRAMES;
            keyLocker.lockKey(JUMP_KEY);
            keyLocker.lockKey(Key.W);
        }
    }

    private void updatePhysics() {
        applyGravity();
        applyFriction();
        clampVelocities();
    }

    protected void applyGravity() {
        if (velocityY < 0 && !isHoldingJump && jumpedIntoAir) {
            velocityY *= JUMP_RELEASE_MULTIPLIER;
        }
        velocityY += gravityAcceleration;
    }

    protected void applyFriction() {
        boolean isOnGround = airGroundState == AirGroundState.GROUND;
        float currentFriction = isOnGround ? groundFriction : airFriction;
        if (velocityX != 0) {
            float newVelocityX = velocityX - Math.signum(velocityX) * currentFriction;
            boolean willCrossZero = Math.signum(newVelocityX) != Math.signum(velocityX);
            velocityX = willCrossZero ? 0 : newVelocityX;
        }
    }

    private void clampVelocities() {
        if (Math.abs(velocityX) > maxPhysicsSpeed)
            velocityX = Math.signum(velocityX) * maxPhysicsSpeed;
        if (velocityY > maxFallSpeed)
            velocityY = maxFallSpeed;
    }

    private boolean canJump() {
        if (jumpedIntoAir) return false;
        boolean hasBufferedJump = jumpBufferTimer > 0;
        boolean onGroundOrCoyoteTime = airGroundState == AirGroundState.GROUND || coyoteTimeTimer > 0;
        return hasBufferedJump && onGroundOrCoyoteTime;
    }

    private void executeJump() {
        velocityY = -jumpVelocity;
        airGroundState = AirGroundState.AIR;
        jumpedIntoAir = true;
        isHoldingJump = true;
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
            if (velocityX > -maxRunSpeed) {
                float acceleration = (velocityX > 0) ? horizontalAcceleration * 3.5f : horizontalAcceleration;
                velocityX -= acceleration;
            }
        } else if (inputState.moveRight) {
            facingDirection = Direction.RIGHT;
            if (velocityX < maxRunSpeed) {
                float acceleration = (velocityX < 0) ? horizontalAcceleration * 3.5f : horizontalAcceleration;
                velocityX += acceleration;
            }
        }
    }

    protected void playerStanding() {
        if (canJump()) executeJump();
        else if (inputState.moveLeft || inputState.moveRight) playerState = PlayerState.WALKING;
        else if (inputState.crouch) playerState = PlayerState.CROUCHING;
    }

    protected void playerWalking() {
        if (canJump()) executeJump();
        else if (!inputState.moveLeft && !inputState.moveRight) playerState = PlayerState.STANDING;
        else {
            applyMovementInput();
            if (inputState.crouch) playerState = PlayerState.CROUCHING;
        }
    }

    protected void playerCrouching() {
        velocityX *= CROUCH_FRICTION_MULTIPLIER;
        if (canJump()) executeJump();
        else if (!inputState.crouch) playerState = PlayerState.STANDING;
    }

    protected void playerJumping() {
        if (airGroundState == AirGroundState.AIR) {
            applyMovementInput();
            if (canJump()) executeJump();
        } else if (airGroundState == AirGroundState.GROUND) {
            if (canJump()) executeJump();
            else playerState = PlayerState.STANDING;
        }
    }

    protected void updateLockedKeys() {
        if (Keyboard.isKeyUp(JUMP_KEY) && Keyboard.isKeyUp(Key.W)) {
            keyLocker.unlockKey(JUMP_KEY);
            keyLocker.unlockKey(Key.W);
        }
        if (Keyboard.isKeyUp(NODE_KEY) && Keyboard.isKeyUp(Key.N)) {
            keyLocker.unlockKey(NODE_KEY);
            keyLocker.unlockKey(Key.N);
        }
        if (Keyboard.isKeyUp(HONK_KEY) && Keyboard.isKeyUp(Key.M)) {
            keyLocker.unlockKey(HONK_KEY);
            keyLocker.unlockKey(Key.M);
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
        switch (grappleAnimationState) {
            case EXTENDING -> {
                this.currentAnimationName = getAnimationName("GRAPPLE_EXTEND");
                return;
            }
            case ATTACHED -> {
                this.currentAnimationName = getAnimationName("GRAPPLE");
                return;
            }
            case RETRACTING -> {
                this.currentAnimationName = getAnimationName("GRAPPLE_RETRACT");
                return;
            }
            case NONE -> {
            }
        }

        String baseAnim = STATE_ANIMATIONS.get(playerState);

        if (baseAnim != null) {
            this.currentAnimationName = getAnimationName(baseAnim);
            if (playerState == PlayerState.STANDING && isInWater()) {
                this.currentAnimationName = getAnimationName("SWIM_STAND");
            }
        } else if (playerState == PlayerState.JUMPING) {
            this.currentAnimationName = getAnimationName(velocityY < 0 ? "JUMP" : "FALL");
        }
    }

    protected void updateGrappling() {
        switch (grappleAnimationState) {
            case EXTENDING -> {
                grappleAnimationFrame++;
                if (grappleAnimationFrame >= GRAPPLE_ANIMATION_MAX_FRAMES) {
                    grappleAnimationState = GrappleAnimationState.ATTACHED;
                    grappleAnimationFrame = 0;
                }
            }

            case RETRACTING -> {
                grappleAnimationFrame--;
                if (grappleAnimationFrame <= 0) {
                    grappleAnimationState = GrappleAnimationState.NONE;
                    grappleAnimationFrame = 0;
                    grappleTarget = null;
                }
            }

            case ATTACHED -> {
            }

            case NONE -> {
            }
        }


        // Allow either X or N to start a grapple
        if (grappleAnimationState == GrappleAnimationState.NONE && !keyLocker.isKeyLocked(NODE_KEY) && !keyLocker.isKeyLocked(Key.N)
                && (Keyboard.isKeyDown(NODE_KEY) || Keyboard.isKeyDown(Key.N))) {
            Point anchor = findNearestNodeAnchorWithRaycast(getCenterX(), getCenterY(), NODE_RADIUS);

            if (anchor != null) {
                startGrappleExtension(anchor);
                // lock both possible node keys
                keyLocker.lockKey(NODE_KEY);
                keyLocker.lockKey(Key.N);
            }
        }

        // allow both UP and W to trigger a grapple-release jump
        if (isGrappling && (Keyboard.isKeyDown(Key.UP) || Keyboard.isKeyDown(Key.W)) && !keyLocker.isKeyLocked(JUMP_KEY)) {
            releaseGrappleWithJump();
            keyLocker.lockKey(JUMP_KEY);
            keyLocker.lockKey(Key.W);
        }

        // allow DOWN or S to release grapple, or pressing the node key again (X or N)
        if (isGrappling && (Keyboard.isKeyDown(Key.DOWN) || Keyboard.isKeyDown(Key.S) ||
            ((Keyboard.isKeyDown(NODE_KEY) || Keyboard.isKeyDown(Key.N)) && !keyLocker.isKeyLocked(NODE_KEY) && !keyLocker.isKeyLocked(Key.N)))) {
            releaseGrapple();
            if (Keyboard.isKeyDown(NODE_KEY) || Keyboard.isKeyDown(Key.N)) {
                keyLocker.lockKey(NODE_KEY);
                keyLocker.lockKey(Key.N);
            }
        }

        // play goose sound when Z or M is pressed
        if ((Keyboard.isKeyDown(HONK_KEY) || Keyboard.isKeyDown(Key.M)) && !keyLocker.isKeyLocked(HONK_KEY) && !keyLocker.isKeyLocked(Key.M)) {
            SoundController.getInstance().playSound("goose");
            keyLocker.lockKey(HONK_KEY);
            keyLocker.lockKey(Key.M);
        }

        if (isGrappling && grappleTarget != null) {
            if (ropeLength <= 1f) {
                releaseGrapple();
                return;
            }

            swingAcceleration = (-gravity / ropeLength) * (float) Math.sin(swingAngle);
            // allow A/D as alternatives to left/right while swinging
            if (Keyboard.isKeyDown(Key.LEFT) || Keyboard.isKeyDown(Key.A)) swingAcceleration -= 0.002;
            if (Keyboard.isKeyDown(Key.RIGHT) || Keyboard.isKeyDown(Key.D)) swingAcceleration += 0.002;

            swingVelocity += swingAcceleration;
            swingVelocity *= 0.995f;
            swingAngle += swingVelocity;

            float targetCenterX = grappleTarget.x + (float) (ropeLength * Math.sin(swingAngle));
            float targetCenterY = grappleTarget.y + (float) (ropeLength * Math.cos(swingAngle));
            float newX = targetCenterX - getWidth() / 2f;
            float newY = targetCenterY - getHeight() / 2f;

            float distToHook = (float) Math.sqrt(
                Math.pow(grappleTarget.x - getCenterX(), 2) + Math.pow(grappleTarget.y - getCenterY(), 2)
            );
            if (distToHook < 18f) {
                releaseGrapple();
                return;
            }

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

    protected void startGrappleExtension(Point anchor) {
        this.grappleAnimationState = GrappleAnimationState.EXTENDING;
        this.grappleAnimationFrame = 0;
        // Start swinging immediately
        activateGrappleAt(anchor);
    }

    public void activateGrappleAt(Point anchor) {
        if (anchor == null || map == null) return;

        this.grappleTarget = anchor;
        this.isGrappling = true;

        float dx = grappleTarget.x - getCenterX();
        float dy = grappleTarget.y - getCenterY();
        this.ropeLength = (float) Math.sqrt(dx * dx + dy * dy);

        this.swingAngle = (float) Math.atan2(getCenterX() - grappleTarget.x, getCenterY() - grappleTarget.y);

        float tangentialVelocity = (velocityX * -dy + velocityY * dx) / ropeLength;
        this.swingVelocity = tangentialVelocity / ropeLength;

        this.velocityX = 0f;
        this.velocityY = 0f;

        this.applyingReleaseMomentum = false;
        this.releaseVx = 0f;
        this.releaseVy = 0f;
    }

    protected void releaseGrapple() {
        releaseGrappleInternal(false);
    }

    protected void releaseGrappleWithJump() {
        releaseGrappleInternal(true);
    }

    // form tile key 
    private String tileKey(int tx, int ty) {
        return tx + "," + ty;
    }

    // mark node inactive 
    private void markNodeInactiveByWorldPoint(Point worldPoint) {
        if (map == null || worldPoint == null) return;
        Point idx = map.getTileIndexByPosition(worldPoint.x, worldPoint.y);
        if (idx == null) return;

        int tx = Math.round(idx.x);
        int ty = Math.round(idx.y);
        tx = Math.max(0, Math.min(map.getWidth() - 1, tx));
        ty = Math.max(0, Math.min(map.getHeight() - 1, ty));

        inactiveNodes.put(tileKey(tx, ty), System.currentTimeMillis());

        // visually switch to bitten tile 
        MapTile tile = map.getMapTile(tx, ty);
        if (tile != null && tile.getTileIndex() == NODE_TILE_INDEX) {
            map.setTileIndex(tx, ty, BITTEN_NODE_TILE_INDEX);
        }
    }


    private void releaseGrappleInternal(boolean shouldJump) {
        if (grappleAnimationState == GrappleAnimationState.NONE || grappleAnimationState == GrappleAnimationState.RETRACTING) {
            return;
        }

        // Mark node inactive by tile index 
        if (grappleTarget != null) {
            markNodeInactiveByWorldPoint(grappleTarget);
        }

        if (grappleAnimationState == GrappleAnimationState.EXTENDING) {
            isGrappling = false;
            ropeLength = 0f;
            swingVelocity = 0f;
            swingAcceleration = 0f;
            grappleAnimationState = GrappleAnimationState.RETRACTING;
            return;
        }

        float savedSwingVelocity = swingVelocity;
        float savedSwingAngle = swingAngle;
        float savedRopeLength = ropeLength;

        float impulseX = (float) (savedRopeLength * savedSwingVelocity * Math.cos(savedSwingAngle));
        float impulseY = (float) (-savedRopeLength * savedSwingVelocity * Math.sin(savedSwingAngle));

        float momentumMultiplier = 1.5f;
        impulseX *= momentumMultiplier;
        impulseY *= momentumMultiplier;

        // Apply momentum immediately
        if (shouldJump) {
            velocityX += impulseX;
            velocityY = -jumpVelocity + (impulseY * 0.3f);
            airGroundState = AirGroundState.AIR;
            jumpedIntoAir = true;
            isHoldingJump = true;
            jumpBufferTimer = 0;
            coyoteTimeTimer = 0;
            playerState = PlayerState.JUMPING;
            currentAnimationName = getAnimationName("JUMP");
        } else {
            velocityX += impulseX;
            velocityY += impulseY;
        }

        // Release grapple physics immediately but keep visual animation
        isGrappling = false;
        ropeLength = 0f;
        swingVelocity = 0f;
        swingAcceleration = 0f;

        // Start retract animation (visual only)
        grappleAnimationState = GrappleAnimationState.RETRACTING;
        grappleAnimationFrame = GRAPPLE_ANIMATION_MAX_FRAMES;
    }

    private Point findNearestNodeAnchorWithRaycast(float px, float py, float maxRadiusPx) {
        if (map == null) return null;

        List<Point> nearbyNodes = new ArrayList<>();

        Point idx = map.getTileIndexByPosition(px, py);
        int cx = Math.round(idx.x);
        int cy = Math.round(idx.y);

        int searchRadius = (int) Math.ceil(maxRadiusPx / map.getTileset().getScaledSpriteWidth()) + 1;

        for (int dy = -searchRadius; dy <= searchRadius; dy++) {
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                int tx = cx + dx;
                int ty = cy + dy;
                MapTile tile = map.getMapTile(tx, ty);
                if (tile == null) continue;

                // active and bitten bread tiles for cooldown 
                int tileIdx = tile.getTileIndex();
                if (tileIdx == NODE_TILE_INDEX || tileIdx == BITTEN_NODE_TILE_INDEX) {
                    // check cooldown
                    String key = tileKey(tx, ty);
                    if (inactiveNodes.containsKey(key)) {
                        long timeSince = System.currentTimeMillis() - inactiveNodes.get(key);
                        if (timeSince < NODE_COOLDOWN_MS) {
                            if (tile.getTileIndex() != BITTEN_NODE_TILE_INDEX) {
                                map.setTileIndex(tx, ty, BITTEN_NODE_TILE_INDEX);
                            }
                            continue; 
                        } else {
                            // cooldown expire
                            inactiveNodes.remove(key);
                            if (map.getMapTile(tx, ty) != null && map.getMapTile(tx, ty).getTileIndex() == BITTEN_NODE_TILE_INDEX) {
                                map.setTileIndex(tx, ty, NODE_TILE_INDEX);
                            }
                        }
                    }

                    // Only add active nodes (not bitten)
                    if (map.getMapTile(tx, ty).getTileIndex() != NODE_TILE_INDEX) {
                        continue;
                    }

                    float ax = tile.getX() + tile.getWidth() / 2f;
                    float ay = tile.getY() + tile.getHeight() / 2f;
                    float ddx = ax - px;
                    float ddy = ay - py;
                    float dist = (float)Math.sqrt(ddx * ddx + ddy * ddy);

                    if (dist <= maxRadiusPx) {
                        nearbyNodes.add(new Point(ax, ay));
                    }
                }
            }
        }

        if (nearbyNodes.isEmpty()) {
            return null;
        }

        nearbyNodes.sort((a, b) -> {
            float distA = (float)Math.sqrt(Math.pow(a.x - px, 2) + Math.pow(a.y - py, 2));
            float distB = (float)Math.sqrt(Math.pow(b.x - px, 2) + Math.pow(b.y - py, 2));
            return Float.compare(distA, distB);
        });

        int maxNodesToCheck = Math.min(10, nearbyNodes.size());
        for (int i = 0; i < maxNodesToCheck; i++) {
            Point node = nearbyNodes.get(i);
            if (hasLineOfSight(px, py, node.x, node.y)) {
                return node;
            }
        }

        return null;
    }

    private boolean hasLineOfSight(float fromX, float fromY, float toX, float toY) {
        float dx = toX - fromX;
        float dy = toY - fromY;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        int steps = Math.max(1, (int)(distance / 4));
        float stepX = dx / steps;
        float stepY = dy / steps;

        for (int i = 1; i < steps; i++) {
            int checkX = (int)(fromX + stepX * i);
            int checkY = (int)(fromY + stepY * i);

            MapTile tile = map.getTileByPosition(checkX, checkY);
            if (tile != null && tile.isSolid()) {
                return false;
            }
        }

        return true;
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
        notifyLevelCompleted();
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
            velocityX = maxRunSpeed * LEVEL_COMPLETE_SPEED_MULTIPLIER;
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

    public GrappleAnimationState getGrappleAnimationState() {
        return grappleAnimationState;
    }

    public float getGrappleExtensionProgress() {
        if (grappleAnimationState == GrappleAnimationState.NONE) {
            return 0.0f;
        }
        return (float) grappleAnimationFrame / (float) GRAPPLE_ANIMATION_MAX_FRAMES;
    }

    // horoizontal velocity getter
    public float getVelocityX() {
        return velocityX;
    }
}
