package org.fxapps.battleship.model;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameManagerTest {

    private Player player1;
    private Player player2;
    private BoardGame boardGame;
    private GameManager gameManager;

    @BeforeEach
    public void init() {
        player1 = Player.create("antonio");
        player2 = Player.create("luana");
        boardGame = BoardGame.create(player1, player2);
        gameManager = GameManager.create(boardGame);
    }

    @Test
    public void initTest() {
        assertEquals(GameState.PREPARATION, gameManager.state());
        assertEquals(false, gameManager.isReady(player1));
        assertEquals(false, gameManager.isReady(player2));
    }

    @Test
    public void addDuplicateShipTest() {
        gameManager.addVerticalShip(player1, Ship.BATTLESHIP, 0, 0);
        var e = assertThrows(IllegalArgumentException.class,
                             () -> gameManager.addVerticalShip(player1, Ship.BATTLESHIP, 0, 1));
        var formattedDuplicateMsg = String.format(GameManager.MSG_DUPLICATE_SHIP, Ship.BATTLESHIP);
        assertEquals(formattedDuplicateMsg, e.getMessage());
    }

    @Test
    public void addTooMuchShipTest() {
        gameManager.addVerticalShip(player1, Ship.BATTLESHIP, 0, 0);
        var e = assertThrows(IllegalArgumentException.class,
                             () -> gameManager.addVerticalShip(player1, Ship.BATTLESHIP, 0, 1));
        var formattedDuplicateMsg = String.format(GameManager.MSG_DUPLICATE_SHIP, Ship.BATTLESHIP);
        assertEquals(formattedDuplicateMsg, e.getMessage());
    }

    @Test
    public void doesNotStartIfNotReadyTest() {
        var e = assertThrows(IllegalStateException.class, () -> gameManager.start());
        assertEquals(GameManager.MSG_GAME_NOT_READY, e.getMessage());
    }

    @Test
    public void readyToStartIfAllShipsWereAddedTest() {
        addAllShipsToAllPlayers();
        assertEquals(GameState.READY_TO_START, gameManager.state());
    }

    @Test
    public void statusChangeAfterRemovingShipTest() {
        addAllShipsToAllPlayers();
        assertEquals(GameState.READY_TO_START, gameManager.state());
        gameManager.removeShip(player1, Ship.BATTLESHIP);
        assertEquals(GameState.PREPARATION, gameManager.state());
    }

    @Test
    public void successStartingAfterPlayerReadyTest() {
        addAllShipsToAllPlayers();
        gameManager.ready(player1);
        gameManager.ready(player2);
        gameManager.start();
        assertEquals(GameState.STARTED, gameManager.state());
    }

    @Test
    public void cannotBeReadyIfPlayerDidntAddShipTest() {
        var e = assertThrows(IllegalStateException.class, () -> gameManager.ready(player2));
        assertEquals(GameManager.MSG_PLAYER_MISSING_SHIPS, e.getMessage());
    }
    
    @Test
    public void cannotRemoveShipTest() {
        addAllShipsToAllPlayers();
        gameManager.ready(player1);
        gameManager.ready(player2);
        gameManager.start();
        Exception e = assertThrows(IllegalStateException.class, () -> gameManager.removeShip(player1, Ship.BATTLESHIP));
        assertEquals(GameManager.MSG_NOT_THE_STATE_TO_REMOVE_SHIPS, e.getMessage());
    }
    
    @Test
    public void stateChangeAfterRemovingShipTest() {
        addAllShipsToAllPlayers();
        gameManager.ready(player1);
        gameManager.ready(player2);
        assertEquals(GameState.READY_TO_START, gameManager.state());
        gameManager.removeShip(player1, Ship.BATTLESHIP);
        assertEquals(GameState.PREPARATION, gameManager.state());
    }

    @Test
    public void cannotStartAfterRemovingShipTest() {
        addAllShipsToAllPlayers();
        gameManager.removeShip(player1, Ship.BATTLESHIP);
        assertEquals(GameState.PREPARATION, gameManager.state());
        var e = assertThrows(IllegalStateException.class, () -> gameManager.start());
        assertEquals(GameManager.MSG_GAME_NOT_READY, e.getMessage());
    }

    @Test
    public void cannotStartIfPlayerIsntReadyTest() {
        addAllShipsToAllPlayers();
        gameManager.ready(player2);
        var e = assertThrows(IllegalStateException.class, () -> gameManager.start());
        var expectedMessage = String.format(GameManager.MSG_PLAYER_NOT_STARTED, player1.getName());
        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    public void attackOnWrongStatusTest() {
        var e = assertThrows(IllegalStateException.class, () -> gameManager.guess(player1, 0, 0));
        assertEquals(GameManager.MSG_NOT_THE_STATUS_TO_ATTACK, e.getMessage());
    }

    @Test
    public void abortTest() {
        gameManager.abort();
        assertEquals(GameState.ABORTED, gameManager.state());
    }

    @Test
    public void successEndTest() {
        addAllShipsToAllPlayers();
        gameManager.ready(boardGame.player1());
        gameManager.ready(boardGame.player2());
        gameManager.start();
        Player winnerPlayer = gameManager.playerTurn().get();
        Player loserPlayer = gameManager.waitingPlayer().get();
        var attackingWhenGameEndedException = assertThrows(IllegalStateException.class, () -> {
            for (int i = 0; i < Ship.values().length; i++) {
                var ship = Ship.values()[i];
                for (int j = 0; j < ship.getSpaces(); j++) {
                    gameManager.guess(winnerPlayer, i, j);
                    gameManager.guess(loserPlayer, i, j);
                }
            }
        });
        assertEquals(GameManager.MSG_NOT_THE_STATUS_TO_ATTACK, attackingWhenGameEndedException.getMessage());
        assertEquals(GameState.FINISHED, gameManager.state());
    }

    @Test
    public void initialStatsTest() {
        GameStats stats = gameManager.stats();
        assertEquals(gameManager.getId(), stats.getId());
        assertEquals(gameManager.getBoardGame().getStartTime(), stats.getStarted());
        assertEquals(gameManager.state(), stats.getState());
        assertEquals(0, stats.getGuesses().get(player1).size());
        assertEquals(0, stats.getGuesses().get(player2).size());
        assertEquals(0, stats.getSunkenShips().get(player1));
        assertEquals(0, stats.getSunkenShips().get(player2));
        assertTrue(stats.getWinner().isEmpty());
    }

    @Test
    public void finalStatsTest() {
        successEndTest();
        Player winner = gameManager.winningPlayer().get();
        Player loser = winner == player1 ? player2 : player1;
        int totalGuesses = Stream.of(Ship.values())
                                 .mapToInt(Ship::getSpaces).sum();
        GameStats stats = gameManager.stats();
        assertEquals(gameManager.getId(), stats.getId());
        assertEquals(gameManager.getBoardGame().getStartTime(), stats.getStarted());
        assertEquals(totalGuesses, stats.getGuesses().get(winner).size());
        assertEquals(totalGuesses - 1, stats.getGuesses().get(loser).size());
        assertEquals(Ship.values().length, stats.getSunkenShips().get(winner));
        assertEquals(Ship.values().length - 1, stats.getSunkenShips().get(loser));
        assertEquals(winner, stats.getWinner().get());
    }

    private void addAllShipsToAllPlayers() {
        var x = 0;
        for (Ship ship : Ship.values()) {
            gameManager.addVerticalShip(player1, ship, x, 0);
            gameManager.addVerticalShip(player2, ship, x, 0);
            x++;
        }
    }

}
