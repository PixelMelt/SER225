package Maps;

import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.BreadGoose;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents the map that is used as a background for the main menu and credits menu screen
public class NinthMap extends Map {

    public NinthMap() {
        super("ninth_map.txt", new CommonTileset());
        //this.playerStartPosition = getMapTile(3, 47).getLocation();
    }

     @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        //EndLevelBox endLevelBox = new EndLevelBox(getMapTile(246, 43).getLocation());
        //enhancedMapTiles.add(endLevelBox);
        
        return enhancedMapTiles;

    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        //BreadGoose gread = new BreadGoose(getMapTile(127, 47).getLocation().subtractY(35));
        //npcs.add(gread);

        return npcs;
    }
}
