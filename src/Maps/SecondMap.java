package Maps;

import Engine.ImageLoader;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import EnhancedMapTiles.Spike;
import EnhancedMapTiles.VerticalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
import Tilesets.CommonTileset;
import Utils.Direction;
import java.util.ArrayList;

// second Map
public class SecondMap extends Map {

    public SecondMap() {
        super("second_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        // horizontal platform
        HorizontalMovingPlatform hmp = new HorizontalMovingPlatform(
                ImageLoader.load("HorizontallyMovingPlatform.png"),
                getMapTile(12, 15).getLocation(),
                getMapTile(24, 15).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0, 16, 16),
                Direction.RIGHT
        );
        enhancedMapTiles.add(hmp);

        // vertical platform
        VerticalMovingPlatform vmp = new VerticalMovingPlatform(
                ImageLoader.load("VerticallyMovingPlatform.png"),
                getMapTile(2, 10).getLocation(),
                getMapTile(2, 14).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0, 16, 16),
                Direction.UP
        );
        enhancedMapTiles.add(vmp);

        // add spikes
        for (int i = 9; i < 27; i++) {
            Spike spikeG = new Spike(getMapTile(i, 17).getLocation());
            enhancedMapTiles.add(spikeG);
        }

        for (int i = 31; i <= 48; i++) {
            MapTile t = getMapTile(i, 17);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        for (int i = 81; i <= 94; i++) {
            MapTile t = getMapTile(i, 17);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        int[] spikeXs = {58, 59, 62, 63, 64, 65, 68, 69};
        for (int x : spikeXs) {
            MapTile t = getMapTile(x, 9);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(97, 13).getLocation());
        enhancedMapTiles.add(endLevelBox);

        // add buzzsaws
        MapTile buzz1 = getMapTile(11, 10);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz1.getLocation()));
        }

        MapTile buzz2 = getMapTile(15, 10);
        if (buzz2 != null && buzz2.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz2.getLocation()));
        }

        MapTile buzz3 = getMapTile(19, 10);
        if (buzz3 != null && buzz3.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz3.getLocation()));
        }

        MapTile buzz4 = getMapTile(23, 10);
        if (buzz4 != null && buzz4.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz4.getLocation()));
        }

        MapTile buzz5 = getMapTile(27, 10);
        if (buzz5 != null && buzz5.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz5.getLocation()));
        }

        MapTile buzz6 = getMapTile(13, 13);
        if (buzz6 != null && buzz6.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz6.getLocation()));
        }

        MapTile buzz7 = getMapTile(17, 13);
        if (buzz7 != null && buzz7.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz7.getLocation()));
        }

        MapTile buzz8 = getMapTile(21, 13);
        if (buzz8 != null && buzz8.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz8.getLocation()));
        }

        MapTile buzz9 = getMapTile(25, 13);
        if (buzz9 != null && buzz9.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz9.getLocation()));
        }

        MapTile buzz10 = getMapTile(34, 10);
        if (buzz10 != null && buzz10.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz10.getLocation()));
        }

        MapTile buzz11 = getMapTile(44, 10);
        if (buzz11 != null && buzz11.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz11.getLocation()));
        }

        MapTile buzz12 = getMapTile(39, 6);
        if (buzz12 != null && buzz12.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz12.getLocation()));
        }

        MapTile buzz13 = getMapTile(71, 9);
        if (buzz13 != null && buzz13.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz13.getLocation()));
        }

        MapTile buzz14 = getMapTile(74, 12);
        if (buzz14 != null && buzz14.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz14.getLocation()));
        }

        MapTile buzz15 = getMapTile(77, 15);
        if (buzz15 != null && buzz15.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz15.getLocation()));
        }

        MapTile buzz16 = getMapTile(51, 14);
        if (buzz16 != null && buzz16.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz16.getLocation()));
        }

        MapTile buzz17 = getMapTile(55, 10);
        if (buzz17 != null && buzz17.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz17.getLocation()));
        }

        return enhancedMapTiles;
    }

}
