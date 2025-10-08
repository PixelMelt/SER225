package Maps;

import EnhancedMapTiles.EndLevelBox;
import Level.*;
import Tilesets.CommonTileset;
import java.util.ArrayList;

// Represents a test map to be used in a level
public class FourthMap extends Map {

    public FourthMap() {
        super("fourth_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 47).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(14, 44).getLocation());
        enhancedMapTiles.add(endLevelBox);

        return enhancedMapTiles;
    }
}
