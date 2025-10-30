package Game;

import Engine.DefaultScreen;
import Engine.GraphicsHandler;
import Engine.Screen;
import Screens.CreditsScreen;
import Screens.InstructionsScreen;
import Screens.LevelSelectorScreen;
import Screens.MenuScreen;
import Screens.PlayLevelScreen;
import Screens.TitleScreen;

/*
 * Based on the current game state, this class determines which Screen should be shown
 * There can only be one "currentScreen", although screens can have "nested" screens
 */
public class ScreenCoordinator extends Screen {
	// currently shown Screen
	protected Screen currentScreen = new DefaultScreen();

	// keep track of gameState so ScreenCoordinator knows which Screen to show
	protected GameState gameState;
	protected GameState previousGameState;

	// Level selection info to pass to PlayLevelScreen
	protected int selectedLevelId = 0;
	protected String selectedLevelClassName = "TestMap";

	public GameState getGameState() {
		return gameState;
	}

	// Other Screens can set the gameState of this class to force it to change the currentScreen
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	// Set the selected level for PlayLevelScreen to load
	public void setSelectedLevel(int levelId, String levelClassName) {
		this.selectedLevelId = levelId;
		this.selectedLevelClassName = levelClassName;
	}

	// Get the selected level ID
	public int getSelectedLevelId() {
		return selectedLevelId;
	}

	// Get the selected level class name
	public String getSelectedLevelClassName() {
		return selectedLevelClassName;
	}

	@Override
	public void initialize() {
		// start game off with Menu Screen
		gameState = GameState.TITLE;
	}

	@Override
	public void update() {
		do {
			// if previousGameState does not equal gameState, it means there was a change in gameState
			// this triggers ScreenCoordinator to bring up a new Screen based on what the gameState is
			if (previousGameState != gameState) {
				switch(gameState) {
					case TITLE:
						currentScreen = new TitleScreen(this);
						break;
					case MENU:
						currentScreen = new MenuScreen(this);
						break;
					case LEVEL_SELECT:
						currentScreen = new LevelSelectorScreen(this);
						break;
					case LEVEL:
						currentScreen = new PlayLevelScreen(this);
						break;
					case CREDITS:
						currentScreen = new CreditsScreen(this);
						break;
					case INSTRUCTIONS:
						currentScreen = new InstructionsScreen(this);
						break;
				}
				currentScreen.initialize();
			}
			previousGameState = gameState;

			// call the update method for the currentScreen
			currentScreen.update();
		} while (previousGameState != gameState);
	}

	@Override
	public void draw(GraphicsHandler graphicsHandler) {
		// call the draw method for the currentScreen
		currentScreen.draw(graphicsHandler);
	}
}
