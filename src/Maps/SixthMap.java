package Maps;

import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.EvilGoose;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents the map that is used as a background for the main menu and credits menu screen
public class SixthMap extends Map {

    public SixthMap() {
        super("sixth_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(4, 44).getLocation();
    }

     @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(247, 45).getLocation());
        enhancedMapTiles.add(endLevelBox);
        
        // add spikes
        for (int i = 13; i <= 43; i++) {
            MapTile t = getMapTile(i, 48);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }
        
        int[][] floatingSpikes = {{13, 43}, {14, 43}, {15, 43}, {16, 43}, {17, 43}, {18, 43}, {19, 43}, {20, 43}, {21, 43}};
        for (int[] fs : floatingSpikes) {
            MapTile t = getMapTile(fs[0], fs[1]);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike s = new Spike(t.getLocation(), true);
                enhancedMapTiles.add(s);
            }
        }

        // add buzzsaws
        MapTile buzz1 = getMapTile(23, 45);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz1.getLocation()));
        }

        MapTile buzz2 = getMapTile(30, 44);
        if (buzz2 != null && buzz2.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz2.getLocation()));
        }

        MapTile buzz3 = getMapTile(27, 40);
        if (buzz3 != null && buzz3.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz3.getLocation()));
        }

        MapTile buzz4 = getMapTile(34, 47);
        if (buzz4 != null && buzz4.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz4.getLocation()));
        
         MapTile buzz5 = getMapTile(22, 36);
        if (buzz5 != null && buzz5.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz5.getLocation()));
        }

        MapTile buzz6 = getMapTile(34, 47);
        if (buzz6 != null && buzz6.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz6.getLocation()));
        }

        MapTile buzz7 = getMapTile(36, 42);
        if (buzz7 != null && buzz7.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz7.getLocation()));
        }

        MapTile buzz8 = getMapTile(36, 34);
        if (buzz8 != null && buzz8.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz8.getLocation()));
        }

        MapTile buzz9 = getMapTile(40, 40);
        if (buzz9 != null && buzz9.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz9.getLocation()));
        }

        MapTile buzz10 = getMapTile(44, 44);
        if (buzz10 != null && buzz10.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz10.getLocation()));
        }

        MapTile buzz11 = getMapTile(42, 36);
        if (buzz11 != null && buzz11.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz11.getLocation()));
        }
        MapTile buzz12 = getMapTile(47, 39);
        if (buzz12 != null && buzz12.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz12.getLocation()));
        }
    }
        return enhancedMapTiles;
}

    
    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        EvilGoose Egoose = new EvilGoose(getMapTile(127, 47).getLocation().subtractY(13));
        npcs.add(Egoose);

        return npcs;

    }
    
}
