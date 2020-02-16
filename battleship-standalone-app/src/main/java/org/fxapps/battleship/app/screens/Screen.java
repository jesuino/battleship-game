package org.fxapps.battleship.app.screens;

import javafx.scene.Node;

public interface Screen {
    
    public String id();

    public Node content();
    
    public String name();
    
    public void onShow();
    
}
