package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Game.SpeedrunRecordManager;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import Utils.Colors;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Level selector screen showing a grid of available levels with their best times
 */
public class LevelSelectorScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected Map background;
    protected KeyLocker keyLocker = new KeyLocker();
    protected List<LevelInfo> levels;
    protected int selectedLevel = 0;
    protected int keyPressTimer;
    protected SpeedrunRecordManager recordManager;
    protected SpriteFont titleText;
    protected SpriteFont instructionText;

    // Visual constants - calculated based on screen size from Config
    private static final int GRID_COLS = 3;
    private static final int BOX_SIZE = Config.GAME_WINDOW_WIDTH / 8;
    private static final int BOX_SPACING = Config.GAME_WINDOW_WIDTH / 26;
    private static final int GRID_START_X = (Config.GAME_WINDOW_WIDTH - (GRID_COLS * BOX_SIZE + (GRID_COLS - 1) * BOX_SPACING)) / 2;
    private static final int GRID_START_Y = Config.GAME_WINDOW_HEIGHT / 4;

    public LevelSelectorScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
        this.recordManager = SpeedrunRecordManager.getInstance();
    }

    @Override
    public void initialize() {
        background = new TitleScreenMap();
        background.setAdjustCamera(false);

        titleText = new SpriteFont("SELECT LEVEL", Config.GAME_WINDOW_WIDTH / 2 - 120, 50, "Arial", 40, new Color(255, 215, 0));
        titleText.setOutlineColor(Colors.BLACK);
        titleText.setOutlineThickness(3);

        instructionText = new SpriteFont("Use Number Keys or Arrow Keys + SPACE to Select | ESC to Go Back",
            Config.GAME_WINDOW_WIDTH / 10, Config.GAME_WINDOW_HEIGHT - 55, "Arial", 16, Colors.WHITE);
        instructionText.setOutlineColor(Colors.BLACK);
        instructionText.setOutlineThickness(2);

        loadLevels();
        keyPressTimer = 0;
        keyLocker.lockKey(Key.SPACE);
        keyLocker.lockKey(Key.ESC);
    }

    /**
     * Load level configuration from levels.json
     */
    private void loadLevels() {
        levels = new ArrayList<>();
        File configFile = new File("levels.json");

        if (!configFile.exists()) {
            System.err.println("levels.json not found! Creating default levels.");
            createDefaultLevels();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            parseLevelsJson(json.toString());
        } catch (IOException e) {
            System.err.println("Error loading levels.json: " + e.getMessage());
            createDefaultLevels();
        }
    }

    /**
     * Parse levels from JSON string
     * Format: {"levels":[{"id":0,"name":"Test Map","className":"TestMap"},...]}
     */
    private void parseLevelsJson(String json) {
        // Remove whitespace
        json = json.replaceAll("\\s+", "");

        // Find the levels array
        int levelsStart = json.indexOf("[");
        int levelsEnd = json.lastIndexOf("]");

        if (levelsStart == -1 || levelsEnd == -1) {
            createDefaultLevels();
            return;
        }

        String levelsArray = json.substring(levelsStart + 1, levelsEnd);

        // Split by level objects
        String[] levelObjects = levelsArray.split("\\},\\{");

        for (String levelObj : levelObjects) {
            // Clean up
            levelObj = levelObj.replace("{", "").replace("}", "").replace("\"", "");

            if (levelObj.isEmpty()) continue;

            // Parse fields
            String[] fields = levelObj.split(",");
            Integer id = null;
            String name = null;
            String className = null;

            for (String field : fields) {
                String[] keyValue = field.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "id" -> id = Integer.parseInt(value);
                        case "name" -> name = value;
                        case "className" -> className = value;
                    }
                }
            }

            if (id != null && name != null && className != null) {
                levels.add(new LevelInfo(id, name, className));
            }
        }

        if (levels.isEmpty()) {
            createDefaultLevels();
        }
    }

    /**
     * Fallback if levels.json is missing or invalid
     */
    private void createDefaultLevels() {
        levels.add(new LevelInfo(0, "Tutorial Map", "TutorialMap"));
        levels.add(new LevelInfo(1, "Test Map", "TestMap"));
        levels.add(new LevelInfo(2, "Second Map", "SecondMap"));
        levels.add(new LevelInfo(3, "Third Map", "ThirdMap"));
        levels.add(new LevelInfo(4, "Fourth Map", "FourthMap"));
    }

    @Override
    public void update() {
        background.update(null);

        // Arrow key navigation
        if ((Keyboard.isKeyDown(Key.DOWN) || Keyboard.isKeyDown(Key.S)) && keyPressTimer == 0) {
            keyPressTimer = 14;
            selectedLevel += GRID_COLS;
            if (selectedLevel >= levels.size()) {
                selectedLevel = selectedLevel % GRID_COLS;
            }
        } else if ((Keyboard.isKeyDown(Key.UP) || Keyboard.isKeyDown(Key.W)) && keyPressTimer == 0) {
            keyPressTimer = 14;
            selectedLevel -= GRID_COLS;
            if (selectedLevel < 0) {
                selectedLevel = ((levels.size() - 1) / GRID_COLS) * GRID_COLS + (selectedLevel + GRID_COLS);
                if (selectedLevel >= levels.size()) {
                    selectedLevel = levels.size() - 1;
                }
            }
        } else if ((Keyboard.isKeyDown(Key.LEFT) || Keyboard.isKeyDown(Key.A)) && keyPressTimer == 0) {
            keyPressTimer = 14;
            selectedLevel--;
            if (selectedLevel < 0) {
                selectedLevel = levels.size() - 1;
            }
        } else if ((Keyboard.isKeyDown(Key.RIGHT) || Keyboard.isKeyDown(Key.D)) && keyPressTimer == 0) {
            keyPressTimer = 14;
            selectedLevel++;
            if (selectedLevel >= levels.size()) {
                selectedLevel = 0;
            }
        } else {
            if (keyPressTimer > 0) {
                keyPressTimer--;
            }
        }

        // Number key selection (1-9)
        for (int i = 0; i < Math.min(levels.size(), 9); i++) {
            Key numberKey = getNumberKey(i + 1);
            if (numberKey != null && Keyboard.isKeyDown(numberKey)) {
                selectedLevel = i;
                loadSelectedLevel();
                return;
            }
        }

        // Space to select current level
        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }
        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
            keyLocker.lockKey(Key.SPACE);
            loadSelectedLevel();
        }

        // ESC to go back to menu
        if (Keyboard.isKeyUp(Key.ESC)) {
            keyLocker.unlockKey(Key.ESC);
        }
        if (!keyLocker.isKeyLocked(Key.ESC) && Keyboard.isKeyDown(Key.ESC)) {
            keyLocker.lockKey(Key.ESC);
            screenCoordinator.setGameState(GameState.MENU);
        }
    }

    /**
     * Load the selected level and transition to play screen
     */
    private void loadSelectedLevel() {
        if (selectedLevel >= 0 && selectedLevel < levels.size()) {
            LevelInfo level = levels.get(selectedLevel);
            screenCoordinator.setSelectedLevel(level.id, level.className);
            screenCoordinator.setGameState(GameState.LEVEL);
        }
    }

    /**
     * Get the Key enum for number keys 1-9
     */
    private Key getNumberKey(int num) {
        return switch (num) {
            case 1 -> Key.ONE;
            case 2 -> Key.TWO;
            case 3 -> Key.THREE;
            case 4 -> Key.FOUR;
            case 5 -> Key.FIVE;
            case 6 -> Key.SIX;
            case 7 -> Key.SEVEN;
            case 8 -> Key.EIGHT;
            case 9 -> Key.NINE;
            default -> null;
        };
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        background.draw(graphicsHandler);

        // Draw semi-transparent overlay
        graphicsHandler.drawFilledRectangle(0, 0, Config.GAME_WINDOW_WIDTH, Config.GAME_WINDOW_HEIGHT,
            new Color(0, 0, 0, 180));

        // Draw title
        titleText.draw(graphicsHandler);
        instructionText.draw(graphicsHandler);

        // Draw level grid
        for (int i = 0; i < levels.size(); i++) {
            int row = i / GRID_COLS;
            int col = i % GRID_COLS;
            int x = GRID_START_X + col * (BOX_SIZE + BOX_SPACING);
            int y = GRID_START_Y + row * (BOX_SIZE + BOX_SPACING);

            drawLevelBox(graphicsHandler, levels.get(i), x, y, i == selectedLevel, i + 1);
        }
    }

    /**
     * Draw a "squircle" box for a level with its info
     */
    private void drawLevelBox(GraphicsHandler graphicsHandler, LevelInfo level, int x, int y, boolean selected, int displayNum) {
        // Box colors
        Color boxColor = selected ? new Color(255, 215, 0) : new Color(49, 207, 240);
        Color borderColor = selected ? new Color(200, 170, 0) : new Color(30, 150, 180);

        // Draw squircle-like effect using Graphics2D directly
        Graphics2D g2d = (Graphics2D) graphicsHandler.getGraphics();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw filled rounded rectangle
        g2d.setColor(boxColor);
        g2d.fillRoundRect(x, y, BOX_SIZE, BOX_SIZE, 40, 40);

        // Draw border
        g2d.setColor(borderColor);
        g2d.setStroke(new java.awt.BasicStroke(4));
        g2d.drawRoundRect(x, y, BOX_SIZE, BOX_SIZE, 40, 40);

        // Draw level number (centered)
        String numberStr = String.valueOf(displayNum);
        SpriteFont numberText = new SpriteFont(numberStr, 0, 0, "Arial", 36, Colors.BLACK);
        int numberWidth = numberStr.length() * 20; // Approximate width for size 36 font
        numberText.setX(x + (BOX_SIZE - numberWidth) / 2);
        numberText.setY(y + 20);
        numberText.draw(graphicsHandler);

        // Draw level name (centered)
        SpriteFont nameText = new SpriteFont(level.name, 0, 0, "Arial", 14, Colors.BLACK);
        int nameWidth = level.name.length() * 8; // Approximate width for size 14 font
        nameText.setX(x + (BOX_SIZE - nameWidth) / 2);
        nameText.setY(y + 65);
        nameText.draw(graphicsHandler);

        // Draw best time at bottom of box (centered)
        String timeStr = recordManager.getBestTimeFormatted(level.id);
        SpriteFont timeText = new SpriteFont(timeStr, 0, 0, "Arial", 11, Colors.BLACK);
        int timeWidth = timeStr.length() * 7; // Approximate width for size 11 font
        timeText.setX(x + (BOX_SIZE - timeWidth) / 2);
        timeText.setY(y + BOX_SIZE - 20);
        timeText.draw(graphicsHandler);
    }

    /**
     * Inner class to hold level information
     */
    private static class LevelInfo {
        int id;
        String name;
        String className;

        LevelInfo(int id, String name, String className) {
            this.id = id;
            this.name = name;
            this.className = className;
        }
    }
}
