package Maps;

import Engine.ImageLoader;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import EnhancedMapTiles.Spike;
import EnhancedMapTiles.VerticalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
import Tilesets.CommonTileset;
import Utils.Direction;
import java.util.ArrayList;

// Represents a test map to be used in a level
public class SecondMap extends Map {

    public SecondMap() {
        super("second_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        
        HorizontalMovingPlatform hmp = new HorizontalMovingPlatform(
                ImageLoader.load("HorizontallyMovingPlatform.png"),
                getMapTile(12, 9).getLocation(),
                getMapTile(21, 9).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.RIGHT
        );
        enhancedMapTiles.add(hmp);
        
        VerticalMovingPlatform vmp = new VerticalMovingPlatform(
                ImageLoader.load("VerticallyMovingPlatform.png"),
                getMapTile(3, 2).getLocation(),
                getMapTile(3, 9).getLocation(),
                TileType.JUMP_THROUGH_PLATFORM,
                3,
                new Rectangle(0, 0,16,16),
                Direction.UP
        );
        enhancedMapTiles.add(vmp);
        
        for(int i = 9; i < 27; i++){
            Spike spikeG = new Spike(getMapTile(i,11).getLocation());
            enhancedMapTiles.add(spikeG);
        }

        EndLevelBox endLevelBox = new EndLevelBox(getMapTile(32, 7).getLocation());
        enhancedMapTiles.add(endLevelBox);

        return enhancedMapTiles;
    }
}
