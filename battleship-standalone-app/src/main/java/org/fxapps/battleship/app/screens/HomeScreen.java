package org.fxapps.battleship.app.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class HomeScreen implements Screen {

    private EventHandler<ActionEvent> startAction;
    BorderPane borderPane;

    public HomeScreen(EventHandler<ActionEvent> startAction) {
        super();
        this.startAction = startAction;
        init();
    }

    public void init() {
        var lblTop = new Label("BattleshipFX");
        var btnStart = new Button("Start");

        borderPane = new BorderPane();
        lblTop.getStyleClass().add("lbl-app-title");
        btnStart.getStyleClass().add("btn-start");

        btnStart.setOnAction(startAction);
        borderPane.setTop(lblTop);
        borderPane.setCenter(btnStart);

        BorderPane.setMargin(lblTop, new Insets(50, 0, 0, 0));
        BorderPane.setAlignment(lblTop, Pos.BOTTOM_CENTER);
        BorderPane.setAlignment(btnStart, Pos.CENTER);
    }

    @Override
    public String id() {
        return "HOME";
    }

    @Override
    public Node content() {
        return borderPane;
    }

    @Override
    public String name() {
        return "Home";
    }

    @Override
    public void onShow() {
        // TODO Auto-generated method stub
        
    }

}
