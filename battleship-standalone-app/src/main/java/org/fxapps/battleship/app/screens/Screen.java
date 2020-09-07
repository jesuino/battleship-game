package org.fxapps.battleship.app.screens;

import javafx.scene.Node;

public interface Screen {
    
    String id();

    Node content();
    
    String name();
    
    void onShow();

    default void resize(double width, double height) {
        // do nothing by default
    }
    
}