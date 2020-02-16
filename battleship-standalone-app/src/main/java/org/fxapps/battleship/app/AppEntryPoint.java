package org.fxapps.battleship.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        preparationScreen = new PreparationScreen(e -> screenManager.goTo(homeScreen.id()));
        homeScreen = new HomeScreen(e -> screenManager.goTo(preparationScreen.id()));
        screenManager = new ScreenManager(WIDTH, HEIGHT, homeScreen, preparationScreen);
        screenManager.goTo(homeScreen.id());
        var scene = new Scene(new StackPane(screenManager.root()), WIDTH, HEIGHT);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.show();
    }

}
