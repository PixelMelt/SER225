package Maps;

import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.BreadPC;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents the map that is used as a background for the main menu and credits menu screen
public class SeventhMap extends Map {

    public SeventhMap() {
        super("seventh_map.txt", new CommonTileset());
        //this.playerStartPosition = getMapTile(2, 72).getLocation();
    }

     @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        //EndLevelBox endLevelBox = new EndLevelBox(getMapTile(52, 12).getLocation());
        //enhancedMapTiles.add(endLevelBox);

        return enhancedMapTiles;
    }

    /*
    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        Walrus walrus = new Walrus(getMapTile(28, 18).getLocation().subtractY(13));
        npcs.add(walrus);

        return npcs;
    }
    */
}
