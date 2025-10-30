package Screens;

import Engine.GraphicsHandler;
import Engine.Key;
import Engine.Keyboard;
import Engine.Screen;
import Engine.ScreenManager;
import Game.GameState;
import Game.ScreenCoordinator;
import Game.SpeedrunRecordManager;
import Level.Map;
import Level.Player;
import Level.PlayerListener;
import Players.Cat;
import Utils.MusicPlayer;
import java.awt.*;

// This class is for when the platformer game is actually being played
public class PlayLevelScreen extends Screen implements PlayerListener {
    protected ScreenCoordinator screenCoordinator;
    protected Map map;
    protected Player player;
    protected PlayLevelScreenState playLevelScreenState;
    protected int screenTimer;
    protected LevelClearedScreen levelClearedScreen;
    protected LevelLoseScreen levelLoseScreen;
    protected boolean levelCompletedStateChangeStart;
    private int currentLevelId;
    private String currentLevelClassName;
    private MusicPlayer music;
    private SpeedrunRecordManager recordManager;
    protected boolean speedrunStarted = false;
    protected boolean speedrunFinished = false;
    protected long speedrunStartNano = 0L;
    protected long speedrunEndNano = 0L;
    protected long speedrunElapsedMs = 0L;
    protected Font timerFont;

    public PlayLevelScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
        this.recordManager = SpeedrunRecordManager.getInstance();
    }

    @Override
    public void initialize() {
        // Get level info from ScreenCoordinator
        this.currentLevelId = screenCoordinator.getSelectedLevelId();
        this.currentLevelClassName = screenCoordinator.getSelectedLevelClassName();

        // Load map dynamically using reflection
        try {
            String fullClassName = "Maps." + currentLevelClassName;
            Class<?> mapClass = Class.forName(fullClassName);
            this.map = (Map) mapClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("Error loading map: " + currentLevelClassName);
            e.printStackTrace();
            // Fallback to TestMap if loading fails
            try {
                this.map = (Map) Class.forName("Maps.TestMap").getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Could not load any map!", ex);
            }
        }

        // setup player
        this.player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
        this.player.setMap(map);
        this.player.addListener(this);

    // initialize speedrun timer
    speedrunStarted = false;
    speedrunFinished = false;
    speedrunStartNano = 0L;
    speedrunEndNano = 0L;
    speedrunElapsedMs = 0L;
    timerFont = new Font("Arial", Font.BOLD, 20);

        levelClearedScreen = new LevelClearedScreen();
        levelLoseScreen = new LevelLoseScreen(this);

        this.playLevelScreenState = PlayLevelScreenState.RUNNING;

        // Start music
        if (music == null) {
            music = new MusicPlayer();
            music.play("Resources/Audio/Music/PentagramIncident.wav", true);
        }
    }

    @Override
    public void update() {
        // based on screen state, perform specific actions
        switch (playLevelScreenState) {
            case RUNNING -> {
                map.update(player);
                player.update();

                // start the timer when keys are pressed
                if (!speedrunStarted) {
                    boolean shouldStart = false;
                    try {
                        if (Math.abs(player.getVelocityX()) > 0.001f) {
                            shouldStart = true;
                        }
                    } catch (Exception ignored) {
                    }

                    // if velocity hasn't changed yet, also start when keys are pressed
                    if (!shouldStart) {
                        if (Keyboard.isKeyDown(Key.LEFT) || Keyboard.isKeyDown(Key.RIGHT)
                                || Keyboard.isKeyDown(Key.A) || Keyboard.isKeyDown(Key.D)
                                || Keyboard.isKeyDown(Key.UP) || Keyboard.isKeyDown(Key.DOWN)
                                || Keyboard.isKeyDown(Key.W) || Keyboard.isKeyDown(Key.S)) {
                            shouldStart = true;
                        }
                    }

                    if (shouldStart) {
                        speedrunStarted = true;
                        speedrunStartNano = System.nanoTime();
                    }
                }
            }
            case LEVEL_COMPLETED -> {
                if (levelCompletedStateChangeStart) {
                    screenTimer = 130;
                    levelCompletedStateChangeStart = false;
                } else {
                    levelClearedScreen.update();
                    screenTimer--;
                    if (screenTimer <= 0) {
                        // Return to level selector after completing a level
                        goBackToLevelSelector();
                    }
                }
            }
            case LEVEL_LOSE -> levelLoseScreen.update();
        }
        // if level is "running" update player and map to keep game logic for the platformer level going
        // if level has been completed, bring up level cleared screen
        // wait on level lose screen to make a decision (either resets level or sends player back to main menu)
            }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        // based on screen state, draw appropriate graphics
        switch (playLevelScreenState) {
            case RUNNING -> {
                map.draw(graphicsHandler);
                player.draw(graphicsHandler);

                // draw dialogue boxes on top of everything
                for (Level.NPC npc : map.getActiveNPCs()) {
                    if (npc.hasActiveDialogue()) {
                        npc.drawDialogueBox(graphicsHandler);
                    }
                }
            }
            case LEVEL_COMPLETED -> levelClearedScreen.draw(graphicsHandler);
            case LEVEL_LOSE -> levelLoseScreen.draw(graphicsHandler);
        }

        // speedrun timer as well as placement on top-right
        if (playLevelScreenState == PlayLevelScreenState.RUNNING) {
            String timeString = "00:00.00";
            if (speedrunStarted) {
                long displayMs = (speedrunFinished) ? speedrunElapsedMs : (System.nanoTime() - speedrunStartNano) / 1_000_000L;
                long minutes = (displayMs / 1000) / 60;
                long seconds = (displayMs / 1000) % 60;
                long milliseconds = (displayMs % 1000) / 10;
                timeString = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
            }

            // place on top right of screen
            int x = ScreenManager.getScreenWidth() - 110;
            int y = 30;
            graphicsHandler.drawStringWithOutline(timeString, x, y, timerFont, Color.white, Color.black, 3f);
        }
    }

    public PlayLevelScreenState getPlayLevelScreenState() {
        return playLevelScreenState;
    }

    @Override
    public void onLevelCompleted() {
        if (playLevelScreenState != PlayLevelScreenState.LEVEL_COMPLETED) {
            // stop the speedrun timer when level is completed
            if (speedrunStarted && !speedrunFinished) {
                speedrunFinished = true;
                speedrunEndNano = System.nanoTime();
                speedrunElapsedMs = (speedrunEndNano - speedrunStartNano) / 1_000_000L;

                // Save the speedrun record
                boolean isNewRecord = recordManager.saveRecord(currentLevelId, speedrunElapsedMs);
                if (isNewRecord) {
                    System.out.println("New record for level " + currentLevelId + ": " +
                        SpeedrunRecordManager.formatTime(speedrunElapsedMs));
                }
            }

            // pass the final time to the level cleared screen so it can display it
            if (levelClearedScreen != null) {
                levelClearedScreen.setFinalTime(speedrunElapsedMs);
            }

            playLevelScreenState = PlayLevelScreenState.LEVEL_COMPLETED;
            levelCompletedStateChangeStart = true;
        }
    }

    @Override
    public void onDeath() {
        if (playLevelScreenState != PlayLevelScreenState.LEVEL_LOSE) {
            playLevelScreenState = PlayLevelScreenState.LEVEL_LOSE;
        }
    }

    public void resetLevel() {
        initialize();
    }

    public void goBackToMenu() {
        screenCoordinator.setGameState(GameState.MENU);
    }

    public void goBackToLevelSelector() {
        screenCoordinator.setGameState(GameState.LEVEL_SELECT);
    }

    // This enum represents the different states this screen can be in
    private enum PlayLevelScreenState {
        RUNNING, LEVEL_COMPLETED, LEVEL_LOSE
    }
}
