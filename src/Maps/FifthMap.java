package Maps;

import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.EvilGoose;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents the map that is used as a background for the main menu and credits menu screen
public class FifthMap extends Map {

    public FifthMap() {
        super("fifth_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(3, 47).getLocation();
    }

     @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(246, 43).getLocation());
        enhancedMapTiles.add(endLevelBox);

        for(int i = 6; i < 124; i++){
            Spike spike1 = new Spike(getMapTile(i, 48).getLocation());
            enhancedMapTiles.add(spike1);
        }

        for(int i = 132; i < 244; i++){
            Spike spike1 = new Spike(getMapTile(i, 48).getLocation());
            enhancedMapTiles.add(spike1);
        }

        for(int i = 1; i < 123; i++){
            Spike spike1 = new Spike(getMapTile(i, 28).getLocation(), true);
            enhancedMapTiles.add(spike1);
        }

        for(int i = 133; i < 249; i++){
            Spike spike1 = new Spike(getMapTile(i, 28).getLocation(),true);
            enhancedMapTiles.add(spike1);
        }

        for(int i = 123; i < 133; i++){
            Spike spike1 = new Spike(getMapTile(i, 39).getLocation(),true);
            enhancedMapTiles.add(spike1);
        }

        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        EvilGoose Egoose = new EvilGoose(getMapTile(127, 47).getLocation().subtractY(35));
        npcs.add(Egoose);

        return npcs;
    }
}
