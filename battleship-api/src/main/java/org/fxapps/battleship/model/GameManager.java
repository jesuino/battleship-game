package org.fxapps.battleship.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fxapps.battleship.BattleshipGame;

public class GameManager implements BattleshipGame {

    static final String MSG_WRONG_NUMBER_OF_SHIPS = String.format("Maximum ships is %d.", Ship.values().length);
    static final String MSG_DUPLICATE_SHIP = "Duplicated ship %s.";
    static final String MSG_NOT_THE_STATUS_TO_ADD_SHIPS = "Not in the state to add ships.";
    static final String MSG_NOT_THE_STATUS_TO_ATTACK = "Not in the state to attack.";
    static final String MSG_PLAYER_NOT_STARTED = "Player %s isn't ready yet.";
    static final String MSG_GAME_NOT_READY = "Game is not ready to start.";
    static final String MSG_PLAYER_MISSING_SHIPS = "Player didn't add all the ships to the board.";
    static final String MSG_NOT_THE_STATE_TO_UNREADY = "Can't set unready in this game state.";
    static final String MSG_NOT_THE_STATE_TO_REMOVE_SHIPS = "Not the state to remove ships.";

    private Map<Player, Boolean> readyState = new HashMap<>();
    private GameState state;
    private BoardGame boardGame;
    private String id;

    public static GameManager create(BoardGame boardGame) {
        var gameManager = new GameManager();
        gameManager.id = UUID.randomUUID().toString();
        gameManager.boardGame = boardGame;
        gameManager.state = GameState.PREPARATION;
        gameManager.readyState.put(boardGame.player1(), false);
        gameManager.readyState.put(boardGame.player2(), false);
        return gameManager;
    }

    @Override
    public void addVerticalShip(Player player, Ship ship, int x, int y) {
        addShip(player, ship, x, y, true);
    }

    @Override
    public void addHorizontalShip(Player player, Ship ship, int x, int y) {
        addShip(player, ship, x, y, false);
    }

    @Override
    public void removeShip(Player player, Ship ship) {
        if (state != GameState.READY_TO_START &&
            state != GameState.FINISHED) {
            throw new IllegalStateException(MSG_NOT_THE_STATE_TO_REMOVE_SHIPS);
        }
        var shipRemoved = boardGame.getBoard(player).removeShip(ship);
        if (shipRemoved) {
            state = GameState.PREPARATION;
            readyState.put(player, false);
        }
    }

    @Override
    public void ready(Player player) {
        var addedShips = boardGame.getBoard(player).getShipsPositions().size();
        if (Ship.values().length != addedShips) {
            throw new IllegalStateException(MSG_PLAYER_MISSING_SHIPS);
        }
        readyState.put(player, true);
    }

    @Override
    public void unready(Player player) {
        if (state != GameState.READY_TO_START &&
            state != GameState.PREPARATION) {
            throw new IllegalStateException(MSG_NOT_THE_STATE_TO_UNREADY);
        }
        readyState.put(player, false);
    }

    @Override
    public GameStats stats() {
        Player player1 = boardGame.player1();
        Player player2 = boardGame.player2();
        return GameStats.of(id,
                            state,
                            boardGame.getStartTime(),
                            Map.of(player1, boardGame.guesses(player1),
                                   player2, boardGame.guesses(player2)),
                            Map.of(player1, boardGame.shipsSunkenBy(player1),
                                   player2, boardGame.shipsSunkenBy(player2)),
                            boardGame.getWinner());
    }

    @Override
    public boolean isReady(Player player) {
        return readyState.get(player);
    }

    public GameState state() {
        return state;
    }

    @Override
    public void guess(Player player, int x, int y) {
        guess(player, x, y, (a, b) -> {
        });
    }

    @Override
    public void guess(Player player, int x, int y, BiConsumer<Boolean, Boolean> result) {
        if (state != GameState.STARTED) {
            throw new IllegalStateException(MSG_NOT_THE_STATUS_TO_ATTACK);
        }
        var nSunkenShips = boardGame.shipsSunkenBy(player);
        var hit = boardGame.guess(player, x, y);
        verifyGameIsOver();
        result.accept(hit, boardGame.shipsSunkenBy(player) > nSunkenShips);
    }

    @Override
    public Optional<Player> winningPlayer() {
        return boardGame.getWinner();
    }

    @Override
    public void start() {
        if (state != GameState.READY_TO_START) {
            throw new IllegalStateException(MSG_GAME_NOT_READY);
        }
        checkIfAllPlayersAreReady();
        state = GameState.STARTED;
    }

    @Override
    public void abort() {
        state = GameState.ABORTED;
    }

    @Override
    public Optional<Player> playerTurn() {
        if (state == GameState.STARTED) {
            return Optional.ofNullable(boardGame.playerTurn());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Player> waitingPlayer() {
        if (playerTurn().isPresent()) {
            return Optional.ofNullable(boardGame.waitingPlayer());
        }

        return Optional.empty();
    }

    @Override
    public String getId() {
        return id;
    }

    BoardGame getBoardGame() {
        return boardGame;
    }

    private void addShip(Player player, Ship ship, int x, int y, boolean isVertical) {
        if (state != GameState.PREPARATION) {
            throw new IllegalArgumentException(MSG_NOT_THE_STATUS_TO_ADD_SHIPS);
        }
        var shipPosition = ShipPosition.create(ship, x, y, isVertical);
        var currentShipsPositionsStream = boardGame.getBoard(player).getShipsPositions().stream();
        var shipsToValidate = Stream.concat(currentShipsPositionsStream, Stream.of(shipPosition))
                                    .map(ShipPosition::getShip)
                                    .collect(Collectors.toList());
        validateShips(shipsToValidate);
        boardGame.getBoard(player).placeShip(shipPosition);
        verifyIfPreparationIsDone();
    }

    private void verifyIfPreparationIsDone() {
        var totalShips = Ship.values().length;
        var board1Ships = boardGame.getBoard(boardGame.player1()).getShipsPositions().size();
        var board2Ships = boardGame.getBoard(boardGame.player2()).getShipsPositions().size();
        if (board1Ships == totalShips && board2Ships == totalShips) {
            state = GameState.READY_TO_START;
        }
    }

    private void validateShips(List<Ship> ships) {
        var possibleDuplicate = findDuplicateShips(ships);
        if (possibleDuplicate.isPresent()) {
            var duplicatedShipMsg = String.format(MSG_DUPLICATE_SHIP, possibleDuplicate.get());
            throw new IllegalArgumentException(duplicatedShipMsg);
        }
    }

    private Optional<String> findDuplicateShips(List<Ship> ships) {
        return ships.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getValue() > 1)
                    .map(e -> e.getKey().name())
                    .findFirst();
    }

    private void verifyGameIsOver() {
        int totalShips = Ship.values().length;
        if (boardGame.shipsSunkenBy(boardGame.player1()) == totalShips ||
            boardGame.shipsSunkenBy(boardGame.player2()) == totalShips) {
            state = GameState.FINISHED;
        }
    }

    private void checkIfAllPlayersAreReady() {
        readyState.entrySet().stream()
                  .filter(e -> !e.getValue())
                  .map(e -> String.format(MSG_PLAYER_NOT_STARTED, e.getKey().getName()))
                  .findFirst().map(IllegalStateException::new).ifPresent(e -> {
                      throw e;
                  });
    }

}
