package Maps;

import EnhancedMapTiles.BreakBox;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.BreadPC;
import Tilesets.CommonTileset;
import java.util.ArrayList;

import Engine.Config;

// Represents a test map to be used in a level
public class FourthMap extends Map {

    public FourthMap() {
        super("fourth_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 72).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(48, 2).getLocation());
        enhancedMapTiles.add(endLevelBox);

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

        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        BreadPC bread = new BreadPC(getMapTile(40, 3).getLocation().subtractY(25));
        npcs.add(bread);

        return npcs;
    }
}