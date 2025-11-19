package Maps;

import EnhancedMapTiles.BreakBox;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.Duck;
import Tilesets.CommonTileset;
import java.util.ArrayList;

import Engine.Config;

// Represents a test map to be used in a level
public class ThirdMap extends Map {

    public ThirdMap() {
        super("third_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 26).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        // add spikes
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                MapTile tile = getMapTile(x, y);
                if (tile != null && tile.getTileIndex() == 16) {
                    enhancedMapTiles.add(new Spike(tile.getLocation()));
                    if (!Config.IS_EDITOR_MODE) {
                        setTileIndex(x, y, -1);
                    }
                }
            }
        }

        // add buzzsaws
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                MapTile tile = getMapTile(x, y);
                if (tile != null && tile.getTileIndex() == 15) {
                    enhancedMapTiles.add(new Buzzsaw(tile.getLocation()));
                    if (!Config.IS_EDITOR_MODE) {
                        setTileIndex(x, y, -1);
                    }
                }
            }
        }

        // add breakboxes
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                MapTile tile = getMapTile(x, y);
                if (tile != null && tile.getTileIndex() == 14) {
                    enhancedMapTiles.add(new BreakBox(tile.getLocation()));
                    if (!Config.IS_EDITOR_MODE) {
                        setTileIndex(x, y, -1);
                    }
                }
            }
        }

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(97, 23).getLocation());
        enhancedMapTiles.add(endLevelBox);

        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        Duck duck = new Duck(getMapTile(93, 26).getLocation().subtractY(45));
        npcs.add(duck);

        return npcs;
    }
}
