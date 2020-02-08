package org.fxapps.battleship.app.screens;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ScreenManager {

    private final Duration millis = Duration.millis(300);
    private final StackPane root = new StackPane();

    private FadeTransition ft;
    private List<Screen> screens;
    private Screen currentScreen;

    public ScreenManager(Screen... screens) {
        this.screens = List.of(screens);
        init();
    }

    public void init() {
        ft = new FadeTransition(millis);
        ft.setNode(root);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.currentTimeProperty().addListener((obs, old, n) -> {
            if (ft.getNode().getOpacity() < 0.01) {
                var content = getCurrentScreen().content();
                root.getChildren().clear();
                root.getChildren().add(content);
            }
        });
    }

    public void goTo(String id) {
        currentScreen = screens.stream()
                               .filter(s -> s.id().equals(id)).findFirst()
                               .orElseThrow(() -> new IllegalArgumentException("Screen " + id + " is not registered"));
        ft.playFromStart();
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public Parent root() {
        return root;
    }

}
