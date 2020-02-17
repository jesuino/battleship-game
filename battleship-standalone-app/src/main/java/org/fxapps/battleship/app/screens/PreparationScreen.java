package org.fxapps.battleship.app.screens;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Ship;

public class PreparationScreen implements Screen {

    BorderPane root;
    Board board = Board.create();
    private static final int boardWidth = 550;
    private static final int boardHeight = 550;
    private ComboBox<Ship> cbShips;
    private ToggleButton tbIsVertical;
    private Canvas canvas;

    public PreparationScreen(EventHandler<ActionEvent> evt) {
        init(evt);
    }

    public void init(EventHandler<ActionEvent> evt) {
        var btnRandom = new Button("RANDOM");
        var btnReset = new Button("RESET");
        var btnStart = new Button("START");

        canvas = new Canvas(boardWidth, boardHeight);
        cbShips = new ComboBox<>();

        initCmbShips();

        btnReset.getStyleClass().add("danger");
        btnStart.getStyleClass().add("btn-start-game");

        btnStart.disableProperty().bind(cbShips.disableProperty().not());

        tbIsVertical = new ToggleButton("Vertical");

        root = new BorderPane();

        btnReset.setAlignment(Pos.CENTER_LEFT);

        btnStart.setPrefSize(canvas.getWidth(), 100);

        var hbShipConf = new HBox(20,
                                  cbShips,
                                  tbIsVertical,
                                  btnRandom,
                                  btnReset);

        root.setTop(hbShipConf);
        root.setCenter(buildBoardRepresentation());
        root.setBottom(btnStart);

        BorderPane.setAlignment(hbShipConf, Pos.CENTER);
        BorderPane.setAlignment(btnStart, Pos.CENTER);

        BorderPane.setMargin(btnStart, new Insets(10));
        BorderPane.setMargin(hbShipConf, new Insets(20, 0, 0, 20));

        cbShips.getItems().addListener((Observable obs) -> cbShips.setDisable(cbShips.getItems().isEmpty()));
        tbIsVertical.disableProperty().bind((cbShips.disableProperty()));

        btnReset.setOnAction(e -> reset());
        btnRandom.setOnAction(e -> {
            board.addRandomShipPositions();
            cbShips.getItems().clear();
            paintBoard();
        });
    }

    private void reset() {
        board = Board.create();
        initCmbShips();
        paintBoard();
    }

    private void initCmbShips() {
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
                Location location = getLocation(e);
                board.placeShip(ship, location, tbIsVertical.isSelected())
                     .ifPresent(pos -> cleanShipAndSelectNext(ship));
            }
        });
        canvas.setOnMouseMoved(e -> {
            Ship ship = cbShips.getSelectionModel().getSelectedItem();
            if (ship != null) {
                board.removeShip(ship);
                Location location = getLocation(e);
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

    private Location getLocation(MouseEvent e) {
        var x = e.getX();
        var y = e.getY();
        int posX = (int) (x / (boardWidth / board.getCols()));
        int posY = (int) (y / (boardHeight / board.getRows()));
        return Location.of(posX, posY);
    }

    private void paintBoard() {
        BattleshipPainter.paintBoard(board, canvas);
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
