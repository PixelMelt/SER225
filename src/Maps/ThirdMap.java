package Maps;

import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import Level.*;
import NPCs.Duck;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents a test map to be used in a level
public class ThirdMap extends Map {

    public ThirdMap() {
        super("third_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 26).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        for(int i = 7; i < 24; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, 28).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 76; i < 92; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, 28).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        //Saw Maze Hell
        Buzzsaw buzz20 = new Buzzsaw(getMapTile(47, 13).getLocation());
        enhancedMapTiles.add(buzz20);

        Buzzsaw buzz21 = new Buzzsaw(getMapTile(62, 15).getLocation());
        enhancedMapTiles.add(buzz21);

        Buzzsaw buzz22 = new Buzzsaw(getMapTile(69, 17).getLocation());
        enhancedMapTiles.add(buzz22);

        for(int i = 3; i < 24; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(43, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 63; i < 71; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, 12).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 17; i < 24; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(44, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 44; i < 61; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, 3).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 6; i < 15; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(62, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 17; i < 27; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(66, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 21; i < 27; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(67, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 13; i < 24; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(70, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 16; i < 27; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(54, i).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 0; i < 2; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(50+i, 9).getLocation());
            enhancedMapTiles.add(buzz1);
            Buzzsaw buzz2 = new Buzzsaw(getMapTile(54+i, 9).getLocation());
            enhancedMapTiles.add(buzz2);
            Buzzsaw buzz3 = new Buzzsaw(getMapTile(58+i, 9).getLocation());
            enhancedMapTiles.add(buzz3);
            Buzzsaw buzz4 = new Buzzsaw(getMapTile(58+i, 15).getLocation());
            enhancedMapTiles.add(buzz4);
        }

        for(int i = 59; i < 63; i++){
            for(int j = 16; j < 24; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
            }
        }

        for(int i = 55; i < 57; i++){
            for(int j = 19; j < 27; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
            }
        }

        for(int i = 45; i < 51; i++){
            for(int j = 20; j < 24; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
            }
        }

        for(int i = 61; i < 63; i++){
            for(int j = 3; j < 6; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
            }
        }

        for(int i = 48; i < 60; i++){
            for(int j = 10; j < 12; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
            }
        }

        for(int i = 48; i < 55; i++){
            for(int j = 12; j < 16; j++){
                Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, j).getLocation());
                enhancedMapTiles.add(buzz1);
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
