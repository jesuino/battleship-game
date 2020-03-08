package org.fxapps.battleship.app.screens;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fxapps.battleship.app.utils.BattleshipPainter;
import org.fxapps.battleship.bot.impl.BattleshipRandomBot;
import org.fxapps.battleship.bot.impl.CheaterBattleshipBot;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.BoardGame;
import org.fxapps.battleship.model.GameManager;
import org.fxapps.battleship.model.GameState;
import org.fxapps.battleship.model.GameStats;
import org.fxapps.battleship.model.Guess;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Player;
import org.fxapps.battleship.model.ShipPosition;

public class GameScreen implements Screen {

    private static final Image IMG_TARGET = new Image("/images/target.png");

    private List<ShipPosition> shipsPositions;

    Canvas player2Canvas;
    Canvas playerCanvas;
    SimpleBooleanProperty isPlayerTurnProperty = new SimpleBooleanProperty(false);
    SimpleObjectProperty<Location> targetLocationProperty = new SimpleObjectProperty<>();
    SimpleObjectProperty<GameState> gameStateProperty = new SimpleObjectProperty<>();

    private GameManager manager;

    private BattleshipRandomBot player2;

    private Player player;

    private Label lblFireInfo;

    private Timeline fireLabelAnimation;

    private List<ShipPosition> botShips;

    private StackPane root;

    private Runnable homeScreenCallback;

    public GameScreen() {
        buildUI();
    }

    public GameScreen(Runnable homeScreenCallback) {
        this.homeScreenCallback = homeScreenCallback != null ? homeScreenCallback : () -> {
        };
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
    }

    public void setShipsPositions(List<ShipPosition> shipsPositions) {
        this.shipsPositions = shipsPositions;
    }

    private void buildUI() {
        var vbGameOverOverlay = new VBox(20);
        Label lblEndTitle = new Label("");
        var btnFire = new Button("Fire");
        player2Canvas = new Canvas(550, 550);
        playerCanvas = new Canvas(150, 150);
        lblFireInfo = new Label();
        buildTransitions();

        lblFireInfo.getStyleClass().add("lbl-fireInfo");
        lblFireInfo.setOpacity(0.0);

        btnFire.disableProperty().bind(targetLocationProperty.isNull().or(isPlayerTurnProperty.not()));
        btnFire.setMinSize(player2Canvas.getWidth(), 50);
        btnFire.getStyleClass().addAll("danger", "btn-fire");
        btnFire.setOnMouseClicked(e -> playerGuess());

        HBox.setMargin(playerCanvas, new Insets(5));

        var btnNewGame = new Button("New Game");
        var lblHits = new Label("Total Hits: 20");
        var lblMisses = new Label("Total Miss: 30");
        var lblTime = new Label("Time: 30 minutes");
        vbGameOverOverlay.getStyleClass().add("vb-end");
        btnNewGame.setOnAction(e -> homeScreenCallback.run());
        vbGameOverOverlay.getChildren().addAll(lblEndTitle, lblTime, lblHits, lblMisses, btnNewGame);
        vbGameOverOverlay.setAlignment(Pos.CENTER);
        vbGameOverOverlay.setMaxSize(300, 300);
        VBox.setMargin(lblEndTitle, new Insets(10, 0, 30, 0));

        player2Canvas.setOnMouseClicked(this::updateTarget);

        var vbGame = new VBox(5,
                              new StackPane(player2Canvas, lblFireInfo),
                              btnFire,
                              new Separator(Orientation.HORIZONTAL),
                              playerCanvas);
        vbGame.setAlignment(Pos.CENTER);

        root = new StackPane(vbGame, vbGameOverOverlay);

        vbGame.disableProperty().bind(fireLabelAnimation.statusProperty()
                                                        .isEqualTo(Status.RUNNING)
                                                        .or(gameStateProperty.isNotEqualTo(GameState.STARTED)));
        vbGameOverOverlay.visibleProperty().bind(gameStateProperty.isEqualTo(GameState.FINISHED));
        gameStateProperty.addListener(l -> {
            if (gameStateProperty.get() == GameState.FINISHED) {
                var stats = manager.stats();
                var totalSeconds = stats.getStarted().until(LocalDateTime.now(), ChronoUnit.SECONDS);
                var totalMinutes = stats.getStarted().until(LocalDateTime.now(), ChronoUnit.MINUTES);
                var guesses = stats.getGuesses().get(player);
                boolean isWin = stats.getWinner().get() == player;
                lblEndTitle.setText(isWin ? "You Win!" : "You lose!");
                lblEndTitle.getStyleClass().clear();
                lblEndTitle.getStyleClass().add(isWin ? "lbl-end-winner" : "lbl-end-loser");
                lblTime.setText("Time: " + totalMinutes + "m " + totalSeconds + "s");
                lblHits.setText("Total Guesses: " + guesses.size());
                lblMisses.setText("Wrong Guesses: " + guesses.stream().filter(g -> !g.isHit()).count());
            }
            paintBoards();
        });
    }

    private void buildTransitions() {
        fireLabelAnimation = new Timeline(new KeyFrame(Duration.millis(400),
                                                       new KeyValue(lblFireInfo.opacityProperty(), 1.0),
                                                       new KeyValue(lblFireInfo.scaleXProperty(), 5),
                                                       new KeyValue(lblFireInfo.scaleYProperty(), 5)),
                                          new KeyFrame(Duration.millis(500)),
                                          new KeyFrame(Duration.millis(600),
                                                       new KeyValue(lblFireInfo.opacityProperty(), 0)));
        fireLabelAnimation.setOnFinished(evt -> {
            lblFireInfo.setScaleX(1);
            lblFireInfo.setScaleY(1);
            paintBoards();
            botGuess();
            targetLocationProperty.set(null);
        });

    }

    private void cleanUp() {
        targetLocationProperty.set(null);
    }

    private void prepareBattle() {
        botShips = Board.randomShips().getShipsPositions();
        player = Player.create("user");
        player2 = new CheaterBattleshipBot(100, shipsPositions);
        manager = GameManager.create(BoardGame.create(player, player2));
        manager.addShips(player2, botShips);
        manager.addShips(player, shipsPositions);
        manager.ready(player);
        manager.ready(player2);
        manager.start();
        manager.playerTurn().ifPresent(p -> isPlayerTurnProperty.set(p == player));
        if (!isPlayerTurnProperty.get()) {
            botGuess();
        }
        gameStateProperty.set(manager.state());
    }

    private void paintBoards() {
        paintPlayerBoard();
        paintPlayer2Board();
    }

    private void updateTarget(MouseEvent e) {
        var playerGuesses = manager.stats().getGuesses().get(player);
        var tileWidth = player2Canvas.getWidth() / Board.DEFAULT_COLS;
        var tileHeight = player2Canvas.getHeight() / Board.DEFAULT_ROWS;
        var location = BattleshipPainter.getLocationOnBoard(e);
        var ctx = player2Canvas.getGraphicsContext2D();

        boolean guessAlreadyMade = playerGuesses.stream().anyMatch(guess -> guess.getLocation().equals(location));
        if (!guessAlreadyMade) {
            paintPlayer2Board();
            ctx.drawImage(IMG_TARGET,
                          location.x() * tileWidth,
                          location.y() * tileHeight,
                          tileWidth,
                          tileHeight);
            targetLocationProperty.set(location);
        }
    }

    private void paintPlayerBoard() {
        var player2Guesses = manager.stats().getGuesses().get(player2);
        BattleshipPainter.paintBoard(playerCanvas, shipsPositions, player2Guesses);
    }

    private void paintPlayer2Board() {
        List<ShipPosition> possibleBotShips = manager.state() == GameState.FINISHED ? botShips : Collections.emptyList();
        var playerGuesses = manager.stats().getGuesses().get(player);
        BattleshipPainter.paintBoard(player2Canvas, possibleBotShips, playerGuesses);
    }

    private void playerGuess() {
        var location = targetLocationProperty.get();
        manager.guess(player, location.x(), location.y(), (hit, sink) -> {
            if (Boolean.TRUE.equals(hit)) {
                lblFireInfo.setText("Hit" + (Boolean.TRUE.equals(sink) ? "\nand Sunk" : ""));
            } else {
                lblFireInfo.setText("Miss");
            }
            fireLabelAnimation.stop();
            fireLabelAnimation.play();
            gameStateProperty.set(manager.state());
        });
    }

    private void botGuess() {
        if (manager.state() != GameState.FINISHED) {
            Location location = player2.newLocation();
            manager.guess(player2, location.x(), location.y());
            isPlayerTurnProperty.set(true);
            gameStateProperty.set(manager.state());
        }
        paintBoards();
    }

}
