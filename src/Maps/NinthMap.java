package Maps;

import EnhancedMapTiles.BreakBox;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.TeamBees;
import Tilesets.CommonTileset;
import java.util.ArrayList;

import Engine.Config;

// Represents the map that is used as a background for the main menu and credits menu screen
public class NinthMap extends Map {

    public NinthMap() {
        super("ninth_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(0, 42).getLocation();
    }

     @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(92, 46).getLocation());
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

        TeamBees Buzz = new TeamBees(getMapTile(0, 38).getLocation().subtractY(35));
        npcs.add(Buzz);

        return npcs;
    }
}
