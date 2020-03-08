package org.fxapps.battleship.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxapps.battleship.app.screens.GameScreen;
import org.fxapps.battleship.app.screens.HomeScreen;
import org.fxapps.battleship.app.screens.PreparationScreen;
import org.fxapps.battleship.app.screens.Screen;
import org.fxapps.battleship.app.screens.ScreenManager;

public class AppEntryPoint extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;

    private ScreenManager screenManager;
    Screen preparationScreen;
    Screen homeScreen;
    GameScreen gameScreen;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Runnable goToPreparation = () -> screenManager.goTo(preparationScreen.id());
        gameScreen = new GameScreen(goToPreparation);
        preparationScreen = new PreparationScreen(shipPositions -> {
            gameScreen.setShipsPositions(shipPositions);
            screenManager.goTo(gameScreen.id());
        });
        homeScreen = new HomeScreen(goToPreparation);
        screenManager = new ScreenManager(WIDTH, HEIGHT, homeScreen, preparationScreen, gameScreen);
        screenManager.home();
        var scene = new Scene(new StackPane(screenManager.root()), WIDTH, HEIGHT);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.show();
    }

}
