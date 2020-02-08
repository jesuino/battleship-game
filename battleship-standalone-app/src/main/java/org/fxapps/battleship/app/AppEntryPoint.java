package org.fxapps.battleship.app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxapps.battleship.app.screens.HomeScreen;
import org.fxapps.battleship.app.screens.PreparationScreen;
import org.fxapps.battleship.app.screens.Screen;
import org.fxapps.battleship.app.screens.ScreenManager;

public class AppEntryPoint extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final int TOTAL_RECT = 10;

    private static final int BOARD_WIDTH = 400;
    private static final int BOARD_HEIGHT = 400;

    private ScreenManager screenManager;
    Screen preparationScreen;
    Screen homeScreen;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        var userBoard = new Canvas(BOARD_WIDTH, BOARD_HEIGHT);
        preparationScreen = new PreparationScreen(e -> screenManager.goTo(homeScreen.id()));
        homeScreen = new HomeScreen(e -> screenManager.goTo(preparationScreen.id()));
        screenManager = new ScreenManager(homeScreen, preparationScreen);
        screenManager.goTo(homeScreen.id());
        var root = new VBox(30, userBoard);
        root.setAlignment(Pos.CENTER);
        var scene = new Scene(new StackPane(screenManager.root()), WIDTH, HEIGHT);
        scene.getStylesheets().add("style.css");
        printGrid(userBoard);
        stage.setScene(scene);
        stage.show();
    }

    private void printGrid(Canvas canvas) {
        var ctx = canvas.getGraphicsContext2D();
        ctx.setStroke(Color.GRAY);
        var rectWidth = BOARD_WIDTH / TOTAL_RECT;
        var rectHeight = BOARD_HEIGHT / TOTAL_RECT;
        for (int i = 0; i <= TOTAL_RECT; i++) {
            var x = i * rectWidth;
            var y = i * rectHeight;
            ctx.strokeLine(0, y, BOARD_WIDTH, y);
            ctx.strokeLine(x, 0, x, BOARD_HEIGHT);
        }
    }

}
