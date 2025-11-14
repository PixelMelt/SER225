package Maps;

import Engine.ImageLoader;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import EnhancedMapTiles.Spike;
import EnhancedMapTiles.VerticalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
import NPCs.Umbrella;
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
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                MapTile tile = getMapTile(x, y);
                if (tile != null && tile.getTileIndex() == 16) {
                    enhancedMapTiles.add(new Spike(tile.getLocation()));
                    // replace tile 16 with tile -1 to hide it from rendering
                    setTileIndex(x, y, -1);
                }
            }
        }

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(97, 13).getLocation());
        enhancedMapTiles.add(endLevelBox);

        // add buzzsaws
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                MapTile tile = getMapTile(x, y);
                if (tile != null && tile.getTileIndex() == 15) {
                    enhancedMapTiles.add(new Buzzsaw(tile.getLocation()));
                    // replace tile 15 with tile -1 to hide it from rendering
                    setTileIndex(x, y, -1);
                }
            }
        }

        return enhancedMapTiles;
    }
    
    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        Umbrella umbre = new Umbrella(getMapTile(95, 16).getLocation().subtractY(45));
        npcs.add(umbre);

        return npcs;
    }
}
