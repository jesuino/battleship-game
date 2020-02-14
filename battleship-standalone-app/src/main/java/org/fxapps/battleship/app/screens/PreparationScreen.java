package org.fxapps.battleship.app.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Ship;

public class PreparationScreen implements Screen {

    BorderPane root;
    Board board = Board.create();
    private static final int boardWidth = 500;
    private static final int boardHeight = 500;
    private GraphicsContext ctx;
    private ComboBox<Ship> cbShips;
    private ToggleButton tbIsVertical;

    public PreparationScreen(EventHandler<ActionEvent> evt) {
        init(evt);
    }

    public void init(EventHandler<ActionEvent> evt) {
        var lblTitle = new Label("Prepare your Board");
        var centerPane = new VBox(30);
        var btnReset = new Button("RESET");

        cbShips = new ComboBox<>();
        
        tbIsVertical = new ToggleButton("Vertical");
        root = new BorderPane();

        initCmbShips();

        HBox hbShipConf = new HBox(10, new Label("Ship"), cbShips, tbIsVertical, btnReset);
        centerPane.getChildren().addAll(hbShipConf,
                                        buildBoardRepresentation());

        Button btn = new Button("ABORT");
        btn.setOnAction(evt);

        root.setTop(lblTitle);
        root.setCenter(centerPane);
        root.setBottom(btn);

        BorderPane.setAlignment(lblTitle, Pos.CENTER);
        BorderPane.setAlignment(centerPane, Pos.CENTER);
        BorderPane.setAlignment(btn, Pos.CENTER);

        BorderPane.setMargin(centerPane, new Insets(30));

        cbShips.itemsProperty().addListener((obs, old, n) -> cbShips.setDisable(n.isEmpty()));

        btnReset.setOnAction(e -> {
            board = Board.create();
            cbShips.getItems().clear();
            initCmbShips();
            paintBoard();
        });
    }

    private void initCmbShips() {
        cbShips.getItems().addAll(Ship.values());
        cbShips.getSelectionModel().select(0);
    }

    private Canvas buildBoardRepresentation() {
        var canvas = new Canvas(boardWidth, boardHeight);
        ctx = canvas.getGraphicsContext2D();
        paintBoard();
        canvas.setOnMouseClicked(e -> {
            Ship ship = cbShips.getSelectionModel().getSelectedItem();
            if (ship != null) {
                board.removeShip(ship);
                Location location = getLocation(e);
                System.out.println(location);
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
        final var tileWidth = boardWidth / board.getCols();
        final var tileHeight = boardHeight / board.getRows();
        ctx.setStroke(Color.LIGHTBLUE);
        for (int i = 0; i < board.getCols(); i++) {
            for (int j = 0; j < board.getRows(); j++) {
                var x = i * tileWidth;
                var y = j * tileHeight;
                if (board.stateAt(i, j)) {
                    ctx.setFill(Color.GRAY);
                } else {
                    ctx.setFill(Color.DARKBLUE);
                }
                ctx.fillRect(x, y, tileWidth, tileHeight);
                ctx.strokeRect(x, y, tileWidth, tileHeight);
            }
        }
    }

    @Override
    public String id() {
        return "PREPARATION";
    }

    @Override
    public Node content() {
        return root;
    }

}
