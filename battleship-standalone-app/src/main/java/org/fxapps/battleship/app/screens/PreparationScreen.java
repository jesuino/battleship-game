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
import javafx.scene.layout.VBox;
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
        var centerPane = new VBox(10);
        var btnReset = new Button("RESET");
        var btnStart = new Button("START");

        btnReset.getStyleClass().add("danger");

        cbShips = new ComboBox<>();
        btnStart.disableProperty().bind(cbShips.disableProperty().not());

        tbIsVertical = new ToggleButton("Vertical");
        root = new BorderPane();

        initCmbShips();

        btnReset.setAlignment(Pos.CENTER_LEFT);

        var hbShipConf = new HBox(20,
                                  cbShips,
                                  tbIsVertical,
                                  btnReset);

        centerPane.getChildren().addAll(hbShipConf,
                                        buildBoardRepresentation(),
                                        btnStart);

        centerPane.setAlignment(Pos.CENTER);
        btnStart.getStyleClass().add("btn-start-game");
        btnStart.setPrefSize(canvas.getWidth(), 100);

        root.setCenter(centerPane);

        BorderPane.setAlignment(centerPane, Pos.CENTER);
        BorderPane.setMargin(centerPane, new Insets(50));

        cbShips.getItems().addListener((Observable obs) -> cbShips.setDisable(cbShips.getItems().isEmpty()));

        btnReset.setOnAction(e -> reset());
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
        canvas = new Canvas(boardWidth, boardHeight);
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
