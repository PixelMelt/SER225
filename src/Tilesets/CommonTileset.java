package Tilesets;

import Builders.FrameBuilder;
import Builders.MapTileBuilder;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.ImageEffect;
import Level.TileType;
import Level.Tileset;
import Utils.SlopeTileLayoutUtils;
import java.util.ArrayList;

// This class represents a "common" tileset of standard tiles defined in the CommonTileset.png file
public class CommonTileset extends Tileset {

    // Keep reference to bread frames for easy switching
    private static Frame breadActiveFrame;
    private static Frame breadInactiveFrame;

    public CommonTileset() {
        super(ImageLoader.load("CommonTileset.png"), 16, 16, 3);
    }

    @Override
    public ArrayList<MapTileBuilder> defineTiles() {
        ArrayList<MapTileBuilder> mapTiles = new ArrayList<>();

        // 0: grass
        Frame grassFrame = new FrameBuilder(getSubImage(0, 0))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(grassFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // 1: sky
        Frame skyFrame = new FrameBuilder(getSubImage(0, 1))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(skyFrame));

        // 2: bread flies
        Frame dirtFrame = new FrameBuilder(getSubImage(0, 2))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(dirtFrame)
                .withTileType(TileType.PASSABLE));

        // 3: bread active (grappling node)
        breadActiveFrame = new FrameBuilder(getSubImage(2, 0))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(breadActiveFrame)
                .withTileType(TileType.PASSABLE));

        // 4: bitten bread frame
        breadInactiveFrame = new FrameBuilder(getSubImage(2, 1))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(breadInactiveFrame)
                .withTileType(TileType.PASSABLE));

        // left end branch
        Frame leftEndBranchFrame = new FrameBuilder(getSubImage(1, 5))
                .withScale(tileScale)
                .withBounds(0, 6, 16, 4)
                .build();
        mapTiles.add(new MapTileBuilder(leftEndBranchFrame)
                .withTileType(TileType.JUMP_THROUGH_PLATFORM));

        // right end branch
        Frame rightEndBranchFrame = new FrameBuilder(getSubImage(1, 5))
                .withScale(tileScale)
                .withBounds(0, 6, 16, 4)
                .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                .build();
        mapTiles.add(new MapTileBuilder(rightEndBranchFrame)
                .withTileType(TileType.JUMP_THROUGH_PLATFORM));

        // tree trunk
        Frame treeTrunkFrame = new FrameBuilder(getSubImage(1, 0))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(treeTrunkFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // tree top leaves
        Frame treeTopLeavesFrame = new FrameBuilder(getSubImage(1, 1))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(treeTopLeavesFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // yellow flower (animated)
        Frame[] yellowFlowerFrames = new Frame[] {
                new FrameBuilder(getSubImage(1, 2), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(1, 3), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(1, 2), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(1, 4), 65).withScale(tileScale).build()
        };
        mapTiles.add(new MapTileBuilder(yellowFlowerFrames));

        // purple flower (animated)
        Frame[] purpleFlowerFrames = new Frame[] {
                new FrameBuilder(getSubImage(0, 3), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(0, 4), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(0, 3), 65).withScale(tileScale).build(),
                new FrameBuilder(getSubImage(0, 5), 65).withScale(tileScale).build()
        };
        mapTiles.add(new MapTileBuilder(purpleFlowerFrames));

        // middle branch
        Frame middleBranchFrame = new FrameBuilder(getSubImage(2, 3))
                .withScale(tileScale)
                .withBounds(0, 6, 16, 4)
                .build();
        mapTiles.add(new MapTileBuilder(middleBranchFrame)
                .withTileType(TileType.JUMP_THROUGH_PLATFORM));

        // tree trunk hole top
        Frame treeTrunkHoleTopFrame = new FrameBuilder(getSubImage(2, 4))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(treeTrunkHoleTopFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // tree trunk hole bottom
        Frame treeTrunkHoleBottomFrame = new FrameBuilder(getSubImage(2, 5))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(treeTrunkHoleBottomFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // top water
        Frame topWaterFrame = new FrameBuilder(getSubImage(3, 0))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(topWaterFrame));

        // water
        Frame waterFrame = new FrameBuilder(getSubImage(3, 1))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(waterFrame)
                .withTileType(TileType.WATER));

        // grey rock
        Frame greyRockFrame = new FrameBuilder(getSubImage(3, 2))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(greyRockFrame)
                .withTileType(TileType.NOT_PASSABLE));

        // left 45 degree slope
        Frame leftSlopeFrame = new FrameBuilder(getSubImage(3, 3))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(leftSlopeFrame)
                .withTileType(TileType.SLOPE)
                .withTileLayout(SlopeTileLayoutUtils.createLeft45SlopeLayout(spriteWidth, (int) tileScale)));

        // right 45 degree slope
        Frame rightSlopeFrame = new FrameBuilder(getSubImage(3, 4))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(rightSlopeFrame)
                .withTileType(TileType.SLOPE)
                .withTileLayout(SlopeTileLayoutUtils.createRight45SlopeLayout(spriteWidth, (int) tileScale)));

        // left 30 degree slope bottom
        Frame leftStairsBottomFrame = new FrameBuilder(getSubImage(4, 0))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(leftStairsBottomFrame)
                .withTileType(TileType.SLOPE)
                .withTileLayout(SlopeTileLayoutUtils.createBottomLeft30SlopeLayout(spriteWidth, (int) tileScale)));

        // left 30 degree slope top
        Frame leftStairsTopFrame = new FrameBuilder(getSubImage(4, 1))
                .withScale(tileScale)
                .build();
        mapTiles.add(new MapTileBuilder(leftStairsTopFrame)
                .withTileType(TileType.SLOPE)
                .withTileLayout(SlopeTileLayoutUtils.createTopLeft30SlopeLayout(spriteWidth, (int) tileScale)));

        return mapTiles;
    }

    public static Frame getBreadActiveFrame() {
        return breadActiveFrame;
    }

    public static Frame getBreadInactiveFrame() {
        return breadInactiveFrame;
    }
}
