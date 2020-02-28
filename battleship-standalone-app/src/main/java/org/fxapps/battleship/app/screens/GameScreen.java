package org.fxapps.battleship.app.screens;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
    SimpleBooleanProperty isPlayerTurn = new SimpleBooleanProperty(false);
    SimpleObjectProperty<Location> targetLocation = new SimpleObjectProperty<>();
    

    private GameManager manager;

    private BattleshipRandomBot player2;

    private Player player;

    private VBox root;

    private Text txtStats;

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
        return "";
    }

    @Override
    public void onShow() {
        cleanUp();
        prepareBattle();
        paintBoards();
        updateStats();
    }

    public void setShipsPositions(List<ShipPosition> shipsPositions) {
        this.shipsPositions = shipsPositions;
    }

    private void buildUI() {
        var btnFire = new Button("Fire");
        player2Canvas = new Canvas(550, 550);
        playerCanvas = new Canvas(150, 150);
        txtStats = new Text("");

        btnFire.disableProperty().bind(targetLocation.isNull().and(isPlayerTurn.not()));
        btnFire.setMinSize(player2Canvas.getWidth(), 50);
        btnFire.getStyleClass().add("danger");
        btnFire.setOnMouseClicked(e -> {
            var location = targetLocation.get();
            manager.guess(player, location.x(), location.y(), (hit, sink) -> {
                System.out.println(hit + " - " + sink);
                botGuess();
                paintBoards();
            });
        });

        HBox.setMargin(playerCanvas, new Insets(5));

        player2Canvas.setOnMouseClicked(this::updateTarget);

        root = new VBox(5, player2Canvas, btnFire, new Separator(Orientation.HORIZONTAL), playerCanvas);
        root.setAlignment(Pos.CENTER);
    }

    private void cleanUp() {
        targetLocation.set(null);
    }

    private void prepareBattle() {
        var botShips = Board.randomShips().getShipsPositions();
        player = Player.create("user");
        player2 = new BattleshipRandomBot();
        manager = GameManager.create(BoardGame.create(player, player2));
        manager.addShips(player2, botShips);
        manager.addShips(player, shipsPositions);
        manager.ready(player);
        manager.ready(player2);
        manager.start();
        manager.playerTurn().ifPresent(p -> isPlayerTurn.set(p == player));
        if (!isPlayerTurn.get()) {
            botGuess();
        }
    }

    private void botGuess() {
        Location location = player2.newLocation();
        manager.guess(player2, location.x(), location.y());
        isPlayerTurn.set(true);
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

    private void updateTarget(MouseEvent e) {
        var tileWidth = player2Canvas.getWidth() / Board.DEFAULT_COLS;
        var tileHeight = player2Canvas.getHeight() / Board.DEFAULT_ROWS;
        var location = BattleshipPainter.getLocationOnBoard(e);
        var ctx = player2Canvas.getGraphicsContext2D();
        paintPlayer2Board();
        ctx.drawImage(IMG_TARGET,
                      location.x() * tileWidth,
                      location.y() * tileHeight,
                      tileWidth,
                      tileHeight);
        targetLocation.set(location);
    }

    private void updateStats() {
        var stats = manager.stats();
        String statsText = "Game Started at " + stats.getStarted();
        txtStats.setText(statsText);
    }

}
