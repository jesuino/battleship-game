package org.fxapps.battleship.app.screens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ScreenManager {

    private final Duration millis = Duration.millis(300);
    private StackPane root;

    private FadeTransition ft;
    private List<Screen> screens;
    private Screen homeScren;
    private Screen currentScreen;
    private Map<String, Label> labels = new HashMap<>();
    private double width;
    private double height;
    
    public ScreenManager(double width, double height, Screen... screens) {
        this.width = width;
        this.height = height;
        homeScren = screens[0];
        this.screens = List.of(screens);
        init();
    }

    public void init() {
        var btnClose = new Button("X");
        var screenContainer = new BorderPane();
        root = new StackPane();
        
        btnClose.setOnAction(e -> goTo(homeScren.id()));
        
        btnClose.getStyleClass().add("btn-close");

        StackPane.setAlignment(btnClose, Pos.TOP_RIGHT);
        StackPane.setMargin(btnClose, new Insets(5, 5, 0, 0));
                
        root.setPrefSize(width, height);
        root.setMaxSize(width, height);
        root.setMinSize(width, height);

        ft = new FadeTransition(millis);
        ft.setNode(root);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.currentTimeProperty().addListener((obs, old, n) -> {
            if (ft.getNode().getOpacity() < 0.01) {
                Screen screen = getCurrentScreen();
                screen.onShow();
                var content = screen.content();
                screenContainer.getChildren().clear();
                root.getChildren().clear();
                screenContainer.setCenter(content);
                root.getChildren().add(screenContainer);
                if (content != homeScren.content()) {
                    screenContainer.setTop(title(screen.name()));
                    root.getChildren().add(btnClose);
                }
            }
        });
    }

    private Label title(String name) {
        return labels.computeIfAbsent(name, n -> {
            var newLblTitle = new Label(name);
            newLblTitle.getStyleClass().add("lbl-screen-title");
            BorderPane.setAlignment(newLblTitle, Pos.CENTER);
            BorderPane.setMargin(newLblTitle, new Insets(25, 0, 0, 0));
            return newLblTitle;
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