package org.fxapps.battleship.app.screens;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxapps.battleship.app.utils.BattleshipPainter;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Ship;
import org.fxapps.battleship.model.ShipPosition;

public class PreparationScreen implements Screen {

    BorderPane root;
    Board board = Board.create();
    private ComboBox<Ship> cbShips;
    private ToggleButton tbIsVertical;
    private Canvas canvas;

    public PreparationScreen(Consumer<List<ShipPosition>> onPreparationFinished) {
        init(onPreparationFinished);
    }

    public void init(Consumer<List<ShipPosition>> onPreparationFinished) {
        var btnRandom = new Button("RANDOM");
        var btnReset = new Button("RESET");
        var btnStart = new Button("START");
        var hbShipConf = new HBox(20);

        canvas = new Canvas(550, 550);
        cbShips = new ComboBox<>();
        root = new BorderPane();
        tbIsVertical = new ToggleButton("Vertical");

        initCbShips();

        btnReset.getStyleClass().add("danger");
        btnReset.setAlignment(Pos.CENTER_LEFT);
        btnReset.setOnAction(e -> reset());
        btnStart.getStyleClass().add("btn-start-game");
        btnStart.disableProperty().bind(cbShips.disableProperty().not());
        btnStart.setPrefSize(canvas.getWidth(), 100);
        btnStart.setOnAction(e -> onPreparationFinished.accept(board.getShipsPositions()));
        btnRandom.setOnAction(e -> {
            board.addRandomShipPositions();
            cbShips.getItems().clear();
            paintBoard();
        });

        hbShipConf.getChildren().addAll(cbShips, tbIsVertical, btnRandom, btnReset);
        root.setTop(hbShipConf);
        root.setCenter(buildBoardRepresentation());
        root.setBottom(btnStart);

        BorderPane.setAlignment(hbShipConf, Pos.CENTER);
        BorderPane.setAlignment(btnStart, Pos.CENTER);

        BorderPane.setMargin(btnStart, new Insets(10));
        BorderPane.setMargin(hbShipConf, new Insets(20, 0, 0, 20));

        cbShips.getItems().addListener((Observable obs) -> cbShips.setDisable(cbShips.getItems().isEmpty()));
        tbIsVertical.disableProperty().bind((cbShips.disableProperty()));
    }

    private void reset() {
        board = Board.create();
        initCbShips();
        paintBoard();
    }

    private void initCbShips() {
        cbShips.getItems().clear();
        cbShips.getItems().addAll(Ship.values());
        cbShips.getSelectionModel().select(0);
    }

    private Canvas buildBoardRepresentation() {
        paintBoard();
        canvas.setOnMouseClicked(e -> {
            Ship ship = cbShips.getSelectionModel().getSelectedItem();
            if (ship != null) {
                board.removeShip(ship);
                Location location = BattleshipPainter.getLocationOnBoard(e);
                board.placeShip(ship, location, tbIsVertical.isSelected())
                     .ifPresent(pos -> cleanShipAndSelectNext(ship));
            }
        });
        canvas.setOnMouseMoved(e -> {
            Ship ship = cbShips.getSelectionModel().getSelectedItem();
            if (ship != null) {
                board.removeShip(ship);
                Location location = BattleshipPainter.getLocationOnBoard(e);
                board.placeShip(ship, location, tbIsVertical.isSelected());
            }
            paintBoard();
        });
        canvas.setOnMouseExited(e -> {
            Ship ship = cbShips.getSelectionModel().getSelectedItem();
            board.removeShip(ship);
            paintBoard();
        });
        return canvas;
    }

    private void cleanShipAndSelectNext(Ship ship) {
        cbShips.getItems().remove(ship);
        if (!cbShips.getItems().isEmpty()) {
            cbShips.getSelectionModel().select(0);
        }
    }

    private void paintBoard() {
        BattleshipPainter.paintBoard(canvas, board);
    }

    @Override
    public String id() {
        return "PREPARATION";
    }

    @Override
    public Node content() {
        return root;
    }

    @Override
    public String name() {
        return "Board Preparation";
    }

    @Override
    public void onShow() {
        reset();
    }

}
