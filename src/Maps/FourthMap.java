package Maps;

import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.Spike;
import Level.*;
import NPCs.BreadPC;
import Tilesets.CommonTileset;
import java.util.ArrayList;

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

        //Spikes

        int array0[] = {8,9,10,20,21,22};
        for(int i: array0){
            Spike spikesFloor = new Spike(getMapTile(i,72).getLocation()); 
            enhancedMapTiles.add(spikesFloor);
        }

        int array[] = {39,43,63};
        for(int i: array){
            Spike spikes0 = new Spike(getMapTile(0,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes0);
        }

        Spike spikes1 = new Spike(getMapTile(1,43).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes1);

        int array1[] = {27,51,67};
        for(int i: array1){
            Spike spikes2 = new Spike(getMapTile(2,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes2);
        }

        Spike spikes4 = new Spike(getMapTile(4,55).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes4);

        Spike spikes8 = new Spike(getMapTile(8,51).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes8);

        Spike spikes9 = new Spike(getMapTile(9,35).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes9);

        int array2[] = {35,65};
        for(int i: array2){
            Spike spikes10 = new Spike(getMapTile(10,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes10);
        }

        int array3[] = {35,51,65};
        for(int i: array3){
            Spike spikes11 = new Spike(getMapTile(11,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes11);
        }

        int array4[] = {35,51,65};
        for(int i: array4){
            Spike spikes12 = new Spike(getMapTile(12,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes12);
        }

        Spike spikes18 = new Spike(getMapTile(18,43).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes18);

        int array5[] = {31,43};
        for(int i: array5){
            Spike spikes19 = new Spike(getMapTile(19,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes19);
        }

        Spike spikes20 = new Spike(getMapTile(20,47).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes20);

        Spike spikes22 = new Spike(getMapTile(22,39).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes22);

        int array6[] = {26,30};
        for(int i: array6){
            Spike spikes42 = new Spike(getMapTile(42,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes42);
        }

        Spike spikes42b = new Spike(getMapTile(42,63).getLocation()); 
        enhancedMapTiles.add(spikes42b);

        Spike spikes43b = new Spike(getMapTile(43,64).getLocation()); 
        enhancedMapTiles.add(spikes43b);

        Spike spikes43 = new Spike(getMapTile(43,30).getLocation().addY(20)); 
        enhancedMapTiles.add(spikes43);

        int array8[] = {18,46,57};
        for(int i: array8){
            Spike spikes46 = new Spike(getMapTile(46,i).getLocation().addY(20)); 
            enhancedMapTiles.add(spikes46);
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