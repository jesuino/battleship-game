package org.fxapps.battleship.app.screens;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fxapps.battleship.app.utils.BattleshipPainter;
import org.fxapps.battleship.bot.impl.BattleshipRandomBot;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.BoardGame;
import org.fxapps.battleship.model.GameManager;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Player;
import org.fxapps.battleship.model.ShipPosition;

public class GameScreen implements Screen {

    private static final Image IMG_TARGET = new Image("/images/target.png");

    private List<ShipPosition> shipsPositions;

    Canvas player2Canvas;
    Canvas playerCanvas;
    SimpleObjectProperty<Location> targetLocation = new SimpleObjectProperty<>();

    private GameManager manager;

    private Player player2;

    private Player player;

    private VBox root;

    public GameScreen() {
        buildUI();
    }

    @Override
    public String id() {
        return "game";
    }

    @Override
    public Node content() {
        return root;
    }

    @Override
    public String name() {
        return "Game";
    }

    @Override
    public void onShow() {
        prepareBattle();
        paintBoards();
    }

    private void buildUI() {
        player2Canvas = new Canvas(500, 500);
        playerCanvas = new Canvas(200, 200);
        var hbBottom = new HBox(10, playerCanvas, new Text(""));
        Button btnFire = new Button("Fire");
        var hbMiddle = new HBox(10, new Label("A5"), btnFire);
        hbMiddle.setAlignment(Pos.CENTER);
        root = new VBox(5, player2Canvas, hbMiddle, new Separator(Orientation.HORIZONTAL), hbBottom);
        root.setAlignment(Pos.CENTER);
        
        btnFire.getStyleClass().add("danger");
        btnFire.setPrefSize(200, 150);
        btnFire.disableProperty().bind(targetLocation.isNull());
        
        HBox.setMargin(playerCanvas, new Insets(5));

        player2Canvas.setOnMouseClicked(e -> {
            var tileWidth = player2Canvas.getWidth() / Board.DEFAULT_COLS;
            var tileHeight = player2Canvas.getHeight() / Board.DEFAULT_ROWS;
            var location = BattleshipPainter.getLocation(e);
            var ctx = player2Canvas.getGraphicsContext2D();
            paintPlayer2Board();
            ctx.drawImage(IMG_TARGET, location.x() * tileWidth, location.y() * tileHeight,
                          tileWidth,
                          tileHeight);
            targetLocation.set(location);
        });
    }

    private void prepareBattle() {
        player = Player.create("user");
        player2 = BattleshipRandomBot.create("bot");
        var boardGame = BoardGame.create(player, player2);
        manager = GameManager.create(boardGame);
        var botShips = Board.randomShips().getShipsPositions();
        manager.addShips(player2, botShips);
        manager.addShips(player, shipsPositions);
        manager.ready(player);
        manager.ready(player2);
        manager.start();
    }

    private void paintBoards() {
        paintPlayerBoard();
        paintPlayer2Board();
    }

    private void paintPlayerBoard() {
        var player2Guesses = manager.stats().getGuesses().get(player2);
        BattleshipPainter.paintBoard(playerCanvas, shipsPositions, player2Guesses);
    }

    private void paintPlayer2Board() {
        var playerGuesses = manager.stats().getGuesses().get(player);
        BattleshipPainter.paintBoard(player2Canvas, Collections.emptyList(), playerGuesses);
    }

    public void setShipsPositions(List<ShipPosition> shipsPositions) {
        this.shipsPositions = shipsPositions;
    }

}
