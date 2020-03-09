package org.fxapps.battleship.app.screens;

import java.util.function.Consumer;

import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxapps.battleship.app.model.Difficult;
import org.fxapps.battleship.app.model.GamePreparationData;
import org.fxapps.battleship.app.utils.BattleshipPainter;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Ship;

public class PreparationScreen implements Screen {

    VBox root;
    Board board = Board.create();
    private ComboBox<Ship> cbShips;
    private ToggleButton tbIsVertical;
    private Canvas canvas;

    public PreparationScreen(Consumer<GamePreparationData> onPreparationFinished) {
        init(onPreparationFinished);
    }

    public void init(Consumer<GamePreparationData> onPreparationFinished) {
        var btnRandom = new Button();
        var btnReset = new Button();
        var btnStart = new Button("START");
        var hbShipConf = new HBox(5);
        var cbDifficult = new ComboBox<Difficult>();

        canvas = new Canvas(700, 700);
        cbShips = new ComboBox<>();
        root = new VBox(10);
        tbIsVertical = new ToggleButton();
        tbIsVertical.getStyleClass().add("tb-vertical");

        initCbShips();

        btnReset.getStyleClass().add("btn-clear");
        btnReset.setOnAction(e -> reset());

        btnStart.getStyleClass().add("btn-start-game");
        btnStart.disableProperty().bind(cbShips.disableProperty().not());
        btnStart.setPrefSize(canvas.getWidth(), 100);

        cbDifficult.getItems().addAll(Difficult.values());
        cbDifficult.getSelectionModel().select(Difficult.MEDIUM);

        btnStart.setOnAction(e -> {
            var gameData = GamePreparationData.of(board.getShipsPositions(),
                                                  cbDifficult.getSelectionModel()
                                                             .getSelectedItem());
            onPreparationFinished.accept(gameData);
        });
        btnRandom.getStyleClass().add("btn-random");
        btnRandom.setOnAction(e -> {
            if (board.getShipsPositions().size() == Ship.values().length) {
                reset();
            }
            board.addRandomShipPositions();
            cbShips.getItems().clear();
            paintBoard();
        });

        hbShipConf.getChildren().addAll(new Label("Ship "),
                                        cbShips,
                                        tbIsVertical,
                                        btnRandom,
                                        btnReset,
                                        new Separator(Orientation.VERTICAL),
                                        new Label("Difficult "),
                                        cbDifficult);
        hbShipConf.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hbShipConf, buildBoardRepresentation(), btnStart);
        root.setAlignment(Pos.CENTER);

        BorderPane.setMargin(btnStart, new Insets(10));

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
