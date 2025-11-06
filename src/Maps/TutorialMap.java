package Maps;

import Engine.GraphicsHandler;
import EnhancedMapTiles.Buzzsaw;
import EnhancedMapTiles.EndLevelBox;
import Level.*;
import NPCs.Walrus;
import Tilesets.CommonTileset;
import Utils.Point;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class TutorialMap extends Map {

    public TutorialMap() {
        super("tutorial_map.txt", new CommonTileset());

        // safely get start position
        MapTile startTile = safeGetMapTile(2, 11);
        if (startTile != null) {
            this.playerStartPosition = startTile.getLocation();
        } else {
            this.playerStartPosition = new Point(64, 64);
            System.err.println("Warning: start tile (2,11) not found in map, using fallback position.");
        }
    }

    @Override
    public ArrayList<EnhancedMapTile> loadEnhancedMapTiles() {
        ArrayList<EnhancedMapTile> enhancedMapTiles = new ArrayList<>();

        MapTile endTile = safeGetMapTile(94, 4);
        if (endTile != null) {
            enhancedMapTiles.add(new EndLevelBox(endTile.getLocation()));
        }

        MapTile buzz1 = safeGetMapTile(52, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz1.getLocation()));
        }
        MapTile buzz2 = safeGetMapTile(53, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz2.getLocation()));
        }
        MapTile buzz3 = safeGetMapTile(56, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz3.getLocation()));
        }
        MapTile buzz4 = safeGetMapTile(57, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz4.getLocation()));
        }
        MapTile buzz5 = safeGetMapTile(60, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz5.getLocation()));
        }
        MapTile buzz6 = safeGetMapTile(61, 18);
        if (buzz1 != null && buzz1.getTileType() == TileType.PASSABLE) {
            enhancedMapTiles.add(new Buzzsaw(buzz6.getLocation()));
        }

        return enhancedMapTiles;
    }

    @Override
    public ArrayList<NPC> loadNPCs() {
        ArrayList<NPC> npcs = new ArrayList<>();

        MapTile walrusTile = safeGetMapTile(40, 11);
        if (walrusTile != null) {
            Walrus walrus = new Walrus(walrusTile.getLocation().subtractY(13));
            npcs.add(walrus);
        }

        return npcs;
    }

    // Helper for testing tiles
    private MapTile safeGetMapTile(int x, int y) {
        try {
            MapTile tile = getMapTile(x, y);
            if (tile == null) {
                System.err.println("Warning: map tile at (" + x + "," + y + ") is null.");
            }
            return tile;
        } catch (Exception e) {
            System.err.println("Warning: map tile out of bounds (" + x + "," + y + ")");
            return null;
        }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);

        // camera offset
        int camX = (int) getCamera().getX();
        int camY = (int) getCamera().getY();

        // text style
        Font font = new Font("Arial", Font.BOLD, 20);
        Color color = Color.BLACK;

        graphicsHandler.drawString("Welcome to the Tutorial!", 200 - camX, 625 - camY, font, color);
        graphicsHandler.drawString("Use WASD or Arrow Keys to Move", 165 - camX, 675 - camY, font, color);
        graphicsHandler.drawString("Grapple Onto Nodes Using keys 'N' or 'X'", 900 - camX, 625 - camY, font, color);
        graphicsHandler.drawString("Nodes Will Deactivate for 3 Seconds After Use", 875 - camX, 675 - camY, font, color);
        graphicsHandler.drawString("Press SPACE to interact with NPC's", 1750 - camX, 450 - camY, font, color);
        graphicsHandler.drawString("Watch Out for Obstacles!", 2400 - camX, 675 - camY, font, color);
        graphicsHandler.drawString("Use Momentum From Swinging to Climb Up", 2900 - camX, 625 - camY, font, color);
        graphicsHandler.drawString("Remember That Nodes Deactivate!", 2935 - camX, 675 - camY, font, color);
        graphicsHandler.drawString("Press 'Z' or 'M' to Honk!", 3625 - camX, 150 - camY, font, color);
        graphicsHandler.drawString("Keep an Eye Out For End Level Boxes!", 4325 - camX, 150 - camY, font, color);
    }
}
