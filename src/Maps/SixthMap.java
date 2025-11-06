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

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(202, 45).getLocation());
        enhancedMapTiles.add(endLevelBox);
        
        // add spikes
        for (int i = 13; i <= 43; i++) {
            MapTile t = getMapTile(i, 48);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        for (int i = 121; i <= 149; i++) {
            MapTile t = getMapTile(i, 48);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        for (int i = 185; i <= 185; i++) {
            MapTile t = getMapTile(i, 49);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike spikeG = new Spike(t.getLocation());
                enhancedMapTiles.add(spikeG);
            }
        }

        for (int i = 13; i <= 21; i++) {
            MapTile t = getMapTile(i, 43);
            if (t != null && t.getTileType() == TileType.PASSABLE) {
                Spike s = new Spike(t.getLocation(), true);
                enhancedMapTiles.add(s);
            }
        }
        for (int i = 75; i <= 108; i++) {
            MapTile t = getMapTile(i, 41);
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
        MapTile buzz13 = getMapTile(58, 39);
        if (buzz13 != null && buzz12.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz13.getLocation()));
        }
        MapTile buzz14 = getMapTile(56, 35);
        if (buzz14 != null && buzz14.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz14.getLocation())); 
        }
        MapTile buzz15 = getMapTile(58, 32);
        if (buzz15 != null && buzz15.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz15.getLocation())); 
        }
        MapTile buzz16 = getMapTile(58, 25);
        if (buzz16 != null && buzz16.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz16.getLocation()));
        }
        MapTile buzz17 = getMapTile(56, 21);
        if (buzz17 != null && buzz17.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz17.getLocation()));
        }
        MapTile buzz18 = getMapTile(79, 45);
        if (buzz18 != null && buzz18.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz18.getLocation()));
        }
        MapTile buzz19 = getMapTile(85, 45);
        if (buzz19 != null && buzz19.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz19.getLocation()));
        }
        MapTile buzz20 = getMapTile(91, 47);
        if (buzz20 != null && buzz20.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz20.getLocation()));
        }
        MapTile buzz21 = getMapTile(100, 45);
        if (buzz21 != null && buzz21.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz21.getLocation()));
        }
        MapTile buzz22 = getMapTile(107, 47);
        if (buzz22 != null && buzz22.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz22.getLocation()));
        }
        MapTile buzz23 = getMapTile(123, 44);
        if (buzz23 != null && buzz23.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz23.getLocation()));
        }
        MapTile buzz24 = getMapTile(130, 46);
        if (buzz24 != null && buzz24.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz24.getLocation()));
        }
        MapTile buzz25 = getMapTile(139, 46);
        if (buzz25 != null && buzz25.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz25.getLocation()));
        }
        MapTile buzz26 = getMapTile(146, 44);
        if (buzz26 != null && buzz26.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz26.getLocation()));
        }
    }
        return enhancedMapTiles;
}

    
    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        EvilGoose Egoose = new EvilGoose(getMapTile(19, 41).getLocation().subtractY(13));
        npcs.add(Egoose);

        return npcs;

    }
    
}
