package Screens;

import Engine.GraphicsHandler;
import Engine.Screen;
import Engine.ScreenManager;
import Engine.Keyboard;
import Engine.Key;
import java.awt.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Level.Player;
import Level.PlayerListener;
import Maps.FourthMap;
import Maps.SecondMap;
import Maps.TestMap;
import Maps.ThirdMap;
import Players.Cat;

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
    private int levelCounter = 0;
    protected boolean speedrunStarted = false;
    protected boolean speedrunFinished = false;
    protected long speedrunStartNano = 0L;
    protected long speedrunEndNano = 0L;
    protected long speedrunElapsedMs = 0L;
    protected Font timerFont;

    public PlayLevelScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    public void initialize() {
        // define/setup map
        switch (levelCounter) {
            case 1:
                this.map = new SecondMap();
                break;
            case 2:
                this.map = new ThirdMap();
                break;
            case 3:
                this.map = new FourthMap();
                break;
            default:
                this.map = new TestMap();
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
    }

    public void update() {
        // based on screen state, perform specific actions
        switch (playLevelScreenState) {
            // if level is "running" update player and map to keep game logic for the platformer level going
            case RUNNING:
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
                break;
            // if level has been completed, bring up level cleared screen
            case LEVEL_COMPLETED:
                if (levelCompletedStateChangeStart) {
                    screenTimer = 130;
                    levelCompletedStateChangeStart = false;
                } else {
                    levelClearedScreen.update();
                    screenTimer--;
                    if (screenTimer <= 0) {
                        levelCounter++;
                        // checks level counter and either call new level or return to main menu
                        switch (levelCounter) {
                            case 1:
                                initialize();
                                break;
                            case 2:
                                initialize();
                                break;
                            case 3:
                                initialize();
                                break;
                            default:
                                goBackToMenu();
                                break;
                        }
                    }
                }
                break;
            // wait on level lose screen to make a decision (either resets level or sends player back to main menu)
            case LEVEL_LOSE:
                levelLoseScreen.update();
                break;
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        // based on screen state, draw appropriate graphics
        switch (playLevelScreenState) {
            case RUNNING:
                map.draw(graphicsHandler);
                player.draw(graphicsHandler);

                // draw dialogue boxes on top of everything
                for (Level.NPC npc : map.getActiveNPCs()) {
                    if (npc.hasActiveDialogue()) {
                        npc.drawDialogueBox(graphicsHandler);
                    }
                }
                break;
            case LEVEL_COMPLETED:
                levelClearedScreen.draw(graphicsHandler);
                break;
            case LEVEL_LOSE:
                levelLoseScreen.draw(graphicsHandler);
                break;
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

    // This enum represents the different states this screen can be in
    private enum PlayLevelScreenState {
        RUNNING, LEVEL_COMPLETED, LEVEL_LOSE
    }
}
