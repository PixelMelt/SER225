package Maps;

import Engine.ImageLoader;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import EnhancedMapTiles.Spike;
import EnhancedMapTiles.VerticalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
import NPCs.Walrus;
import Tilesets.CommonTileset;
import Utils.Direction;
import Utils.Point;
import java.util.ArrayList;
import java.util.Random;

// Represents a test map to be used in a level
public class TestMap extends Map {

    public TestMap() {
        super("test_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
    }

    /* Enemies if needed
    @Override
    public ArrayList<Enemy> loadEnemies() {
        ArrayList<Enemy> enemies = new ArrayList<>();

        BugEnemy bugEnemy = new BugEnemy(getMapTile(16, 10).getLocation().subtractY(25), Direction.LEFT);
        enemies.add(bugEnemy);

        DinosaurEnemy dinosaurEnemy = new DinosaurEnemy(getMapTile(19, 1).getLocation().addY(2), getMapTile(22, 1).getLocation().addY(2), Direction.RIGHT);
        enemies.add(dinosaurEnemy);

        return enemies;
    }
    */

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        HorizontalMovingPlatform hmp = new HorizontalMovingPlatform(
                ImageLoader.load("HorizontallyMovingPlatform.png"),
                getMapTile(24, 6).getLocation(),
                getMapTile(27, 6).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.RIGHT
        );
        enhancedMapTiles.add(hmp);

        VerticalMovingPlatform vmp = new VerticalMovingPlatform(
                ImageLoader.load("VerticallyMovingPlatform.png"),
                getMapTile(28, 8).getLocation(),
                getMapTile(28, 9).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.UP
        );
        enhancedMapTiles.add(vmp);

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(32, 7).getLocation());
        enhancedMapTiles.add(endLevelBox);

    // add spikes randomly across the map
        Random rnd = new Random();
        int placed = 0;
        int attempts = 0;
    int desired = 5; // total spikes
    int spawnAvoidX = 2;
    int spawnAvoidY = 11;
    int spawnAvoidRadius = 5; // reduce spawn avoidance radius
    int groundDesired = Math.min(2, desired); // guaranteed ground spikes
    int placedGround = 0;
    int groundAttempts = 0;
    int minSeparation = 4; // min tile distance between hazards
    while (placedGround < groundDesired && groundAttempts < desired * 50) {
        groundAttempts++;
        int xg = rnd.nextInt(getWidth());
        int yg = rnd.nextInt(getHeight());
        // avoid spawn area
        if (Math.abs(xg - spawnAvoidX) <= spawnAvoidRadius && Math.abs(yg - spawnAvoidY) <= spawnAvoidRadius) continue;
        MapTile tileG = getMapTile(xg, yg);
        if (tileG == null) continue;
        if (tileG.getTileType() != TileType.PASSABLE) continue;
        // require solid ground below
        if (yg + 1 >= getHeight()) continue;
        MapTile belowG = getMapTile(xg, yg + 1);
        if (belowG == null || belowG.getTileType() != TileType.NOT_PASSABLE) continue;

        boolean occupiedG = false;
        for (EnhancedMapTile emt : enhancedMapTiles) {
            Point idx = getTileIndexByPosition(emt.getX(), emt.getY());
            // exact occupancy check
            if (Math.round(idx.x) == xg && Math.round(idx.y) == yg) { occupiedG = true; break; }
            // separation check to avoid clumping
            double dx = idx.x - xg;
            double dy = idx.y - yg;
            if (Math.sqrt(dx * dx + dy * dy) < minSeparation) { occupiedG = true; break; }
        }
    if (occupiedG) continue;

    // place ground spike
    Spike spikeG = new Spike(tileG.getLocation(), false);
    enhancedMapTiles.add(spikeG);
    placedGround++;
    placed++;
    }
    // place spikes
    while (placed < desired && attempts < desired * 100) {
            attempts++;
            int x = rnd.nextInt(getWidth());
            int y = rnd.nextInt(getHeight());
            // avoid spawn area
            if (Math.abs(x - spawnAvoidX) <= spawnAvoidRadius && Math.abs(y - spawnAvoidY) <= spawnAvoidRadius) continue;
            MapTile tile = getMapTile(x, y);
            if (tile == null) continue;
            if (tile.getTileType() != TileType.PASSABLE) continue;

            boolean occupied = false;
            for (EnhancedMapTile emt : enhancedMapTiles) {
                Point idx = getTileIndexByPosition(emt.getX(), emt.getY());
                // exact occupancy to avoid overlap of spikes
                if (Math.round(idx.x) == x && Math.round(idx.y) == y) { occupied = true; break; }
                double dx = idx.x - x;
                double dy = idx.y - y;
                if (Math.sqrt(dx * dx + dy * dy) < minSeparation) { occupied = true; break; }
            }
            if (occupied) continue;

            // determine if spike is floating, if floating, flip
            boolean floating = true;
            if (y + 1 < getHeight()) {
                MapTile below = getMapTile(x, y + 1);
                if (below != null && below.getTileType() == TileType.NOT_PASSABLE) {
                    floating = false;
                }
            }
            Spike spike = new Spike(tile.getLocation(), floating);
            enhancedMapTiles.add(spike);
            placed++;
        }

    // place buzzsaws 
        int buzzPlaced = 0;
        int buzzDesired = 2;
        int buzzAttempts = 0;
        while (buzzPlaced < buzzDesired && buzzAttempts < 200) {
            buzzAttempts++;
            int bx = rnd.nextInt(getWidth());
            int by = rnd.nextInt(getHeight());
            if (Math.abs(bx - spawnAvoidX) <= spawnAvoidRadius && Math.abs(by - spawnAvoidY) <= spawnAvoidRadius) continue;
            MapTile bt = getMapTile(bx, by);
            if (bt == null) continue;
            if (bt.getTileType() != TileType.PASSABLE) continue;

            boolean occupiedB = false;
            for (EnhancedMapTile emt : enhancedMapTiles) {
                Point idx = getTileIndexByPosition(emt.getX(), emt.getY());
                if (Math.round(idx.x) == bx && Math.round(idx.y) == by) { occupiedB = true; break; }
                double dx = idx.x - bx;
                double dy = idx.y - by;
                if (Math.sqrt(dx * dx + dy * dy) < minSeparation) { occupiedB = true; break; }
            }
            if (occupiedB) continue;

            Buzzsaw buzz = new Buzzsaw(bt.getLocation());
            enhancedMapTiles.add(buzz);
            buzzPlaced++;
        }

        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        Walrus walrus = new Walrus(getMapTile(30, 10).getLocation().subtractY(13));
        npcs.add(walrus);

        return npcs;
    }
}
