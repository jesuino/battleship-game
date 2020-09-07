package org.fxapps.battleship.apps;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;

import org.fxapps.battleship.app.ScreenManagerFactory;
import org.fxapps.battleship.app.screens.ScreenManager;

import javafx.geometry.Dimension2D;
import javafx.scene.Scene;


public class Main extends MobileApplication {

    ScreenManagerFactory factory = new ScreenManagerFactory();
    ScreenManager screenManager;

    @Override
    public void init() {
        screenManager = factory.newScreenManager(300, 400);
        addViewFactory(HOME_VIEW, () -> {
            
            View view = new View(screenManager.root()) {
                @Override
                protected void updateAppBar(AppBar appBar) {
                    appBar.setTitleText("BattleshipFX");
                }
            };

            return view;
        });        
    }

    @Override
    public void postInit(Scene scene) {
        getAppBar().setVisible(false);
        scene.getStylesheets().add(Main.class.getResource("/battleship-style.css").toExternalForm());

        if (Platform.isDesktop()) {
            Dimension2D dimension2D = DisplayService.create()
                    .map(DisplayService::getDefaultDimensions)
                    .orElse(new Dimension2D(640, 480));
            scene.getWindow().setWidth(dimension2D.getWidth());
            scene.getWindow().setHeight(dimension2D.getHeight());
        }

        Runnable resize = () -> screenManager.resize(scene.getWidth(), scene.getHeight());

        scene.widthProperty().addListener(l -> resize.run());
        scene.heightProperty().addListener(l -> resize.run());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
