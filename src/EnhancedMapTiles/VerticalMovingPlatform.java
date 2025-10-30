package EnhancedMapTiles;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import GameObject.Rectangle;
import Level.EnhancedMapTile;
import Level.Player;
import Level.TileType;
import Utils.Direction;
import Utils.Point;
import java.awt.image.BufferedImage;

// This class is for a vertical moving platform
// the platform will move back and forth between its start location and end location
// if the player is standing on top of it, the player will be moved the same amount as the platform is moving (so the platform will not slide out from under the player)
public final class VerticalMovingPlatform extends EnhancedMapTile {
    private final Point startLocation;
    private final Point endLocation;
    private final float movementSpeed = 1f;
    private final Direction startDirection;
    private Direction direction;

    public VerticalMovingPlatform(BufferedImage image, Point startLocation, Point endLocation, TileType tileType, float scale, Rectangle bounds, Direction startDirection) {
        super(startLocation.x, startLocation.y, new FrameBuilder(image).withBounds(bounds).withScale(scale).build(), tileType);
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startDirection = startDirection;
        this.initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        direction = startDirection;
    }

    @Override
    public void update(Player player) {
        boolean willBeRidingPlatform = touching(player) && Math.abs((player.getBounds().getY2() + 1) - getBounds().getY1()) <= 2;
        if (willBeRidingPlatform) {
            player.isOnMovingPlatform = true;
        }

        float startBound = startLocation.y;
        float endBound = endLocation.y;

        // move platform up or down based on its current direction
        int moveAmountY = 0;
        if (direction == Direction.DOWN) {
            moveAmountY += movementSpeed;
        } else if (direction == Direction.UP) {
            moveAmountY -= movementSpeed;
        }

        moveY(moveAmountY);

        // if platform reaches the start or end location, it turns around
        // platform may end up going a bit past the start or end location depending on movement speed
        // this calculates the difference and pushes the platform back a bit so it ends up right on the start or end location
        if (getY1() >= endBound) {
            float difference = endBound - getY1();
            moveY(difference);
            moveAmountY += difference;
            direction = Direction.UP;
        } else if (getY1() <= startBound) {
            float difference = startBound - getY1();
            moveY(difference);
            moveAmountY += difference;
            direction = Direction.DOWN;
        }

        boolean isRidingPlatform = touching(player) && Math.abs((player.getBounds().getY2() + 1) - getBounds().getY1()) <= 2;

        if (isRidingPlatform) {
            player.moveY(moveAmountY);
        }
        // if tile type is NOT PASSABLE, if the platform is moving and hits into the player (y axis), it will push the player
        // Only do this if player is NOT riding on top
        else if (tileType == TileType.NOT_PASSABLE) {
            if (intersects(player) && moveAmountY >= 0 && player.getBounds().getY1() <= getBounds().getY2()) {
                player.moveYHandleCollision(getBounds().getY2() - player.getBounds().getY1());
            } else if (intersects(player) && moveAmountY <= 0 && player.getBounds().getY2() >= getBounds().getY1()) {
                player.moveYHandleCollision(getBounds().getY1() - player.getBounds().getY2());
            }
        }

        super.update(player);
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
    }

}