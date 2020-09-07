package org.fxapps.battleship.app;

import java.util.function.Consumer;

import org.fxapps.battleship.app.model.GamePreparationData;
import org.fxapps.battleship.app.screens.GameScreen;
import org.fxapps.battleship.app.screens.HomeScreen;
import org.fxapps.battleship.app.screens.PreparationScreen;
import org.fxapps.battleship.app.screens.Screen;
import org.fxapps.battleship.app.screens.ScreenManager;
import javafx.scene.Parent;

public class ScreenManagerFactory {

    private ScreenManager screenManager;
    private Screen preparationScreen;
    private Screen homeScreen;
    private GameScreen gameScreen;

    public ScreenManager newScreenManager(double width, double height) {
        Runnable goToPreparation = () -> screenManager.goTo(preparationScreen.id());
        Consumer<GamePreparationData> preparationDataConsumer = gamePreparationData -> {
            gameScreen.setGamePreparationData(gamePreparationData);
            screenManager.goTo(gameScreen.id());
        };

        gameScreen = new GameScreen(goToPreparation);
        preparationScreen = new PreparationScreen(preparationDataConsumer);
        homeScreen = new HomeScreen(goToPreparation);
        screenManager = new ScreenManager(width, height, homeScreen, preparationScreen, gameScreen);
        screenManager.home();

        return screenManager;
    }

}