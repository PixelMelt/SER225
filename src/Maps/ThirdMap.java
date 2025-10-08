package Maps;

import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import Level.*;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents a test map to be used in a level
public class ThirdMap extends Map {

    public ThirdMap() {
        super("third_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        for(int i = 8; i < 10; i++){
            Buzzsaw buzz1 = new Buzzsaw(getMapTile(i, 9).getLocation());
            enhancedMapTiles.add(buzz1);
        }

        for(int i = 12; i < 14; i++){
            Buzzsaw buzz3 = new Buzzsaw(getMapTile(i, 11).getLocation());
            enhancedMapTiles.add(buzz3);
        }

        for(int i = 18; i < 20; i++){
            Buzzsaw buzz4 = new Buzzsaw(getMapTile(i, 7).getLocation());
            enhancedMapTiles.add(buzz4);
        }

        for(int i = 26; i < 30; i++){
            Buzzsaw buzz7 = new Buzzsaw(getMapTile(i, 2).getLocation());
            Buzzsaw buzz5 = new Buzzsaw(getMapTile(i, 3).getLocation());
            Buzzsaw buzz6 = new Buzzsaw(getMapTile(i, 10).getLocation());
            enhancedMapTiles.add(buzz5);
            enhancedMapTiles.add(buzz6);
            enhancedMapTiles.add(buzz7);
        }

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(32, 7).getLocation());
        enhancedMapTiles.add(endLevelBox);

        return enhancedMapTiles;
    }
}
