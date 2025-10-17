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
import java.util.ArrayList;

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
                getMapTile(26, 15).getLocation(),
                getMapTile(29, 15).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.RIGHT
        );
        enhancedMapTiles.add(hmp);

        VerticalMovingPlatform vmp = new VerticalMovingPlatform(
                ImageLoader.load("VerticallyMovingPlatform.png"),
                getMapTile(26, 9).getLocation(),
                getMapTile(26, 12).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.UP
        );
        enhancedMapTiles.add(vmp);

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(52, 12).getLocation());
        enhancedMapTiles.add(endLevelBox);

        // place a row of ground spikes
        for (int i = 4; i < 18; i++) {
            MapTile t = getMapTile(i, 17);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike s = new Spike(t.getLocation(), false);
                enhancedMapTiles.add(s);
            }
        }

    // place a few floating spikes
    int[][] floatingSpikes = {{22, 11}, {12, 14}, {39, 10},{40, 10}};
        for (int[] fs : floatingSpikes) {
            MapTile t = getMapTile(fs[0], fs[1]);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike s = new Spike(t.getLocation(), true);
                enhancedMapTiles.add(s);
            }
        }

        // place buzzsaws at visible positions
        MapTile buzz1 = getMapTile(20, 10);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz1.getLocation()));
        }
        MapTile buzz2 = getMapTile(27, 5);
        if (buzz2 != null && buzz2.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz2.getLocation()));
        }
        MapTile buzz3 = getMapTile(12, 12);
        if (buzz3 != null && buzz3.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz3.getLocation()));
        }
        MapTile buzz4 = getMapTile(14, 9);
        if (buzz4 != null && buzz4.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz4.getLocation()));
        }
        MapTile buzz5 = getMapTile(45, 11);
        if (buzz5 != null && buzz5.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz5.getLocation()));
        }
        MapTile buzz6 = getMapTile(45, 12);
        if (buzz6 != null && buzz6.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz6.getLocation()));
        }
        MapTile buzz7= getMapTile(45, 13);
        if (buzz7 != null && buzz7.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz7.getLocation()));
        }
        MapTile buzz8 = getMapTile(45, 14);
        if (buzz8 != null && buzz8.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz8.getLocation()));
        }
        MapTile buzz9 = getMapTile(47, 11);
        if (buzz9 != null && buzz9.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz9.getLocation()));
        }
        MapTile buzz10 = getMapTile(47, 12);
        if (buzz10 != null && buzz10.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz10.getLocation()));
        }
        MapTile buzz11 = getMapTile(47, 13);
        if (buzz11 != null && buzz11.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz11.getLocation()));
        }
        MapTile buzz12 = getMapTile(47, 14);
        if (buzz12 != null && buzz12.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz12.getLocation()));
        }
        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        Walrus walrus = new Walrus(getMapTile(28, 18).getLocation().subtractY(13));
        npcs.add(walrus);

        return npcs;
    }
}
