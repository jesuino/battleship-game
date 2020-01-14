package org.fxapps.battleship.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardGameTest {

    private Player player1;
    private Player player2;
    private BoardGame boardGame;

    @BeforeEach
    public void init() {
        player1 = Player.create("antonio");
        player2 = Player.create("luana");
        boardGame = BoardGame.create(player1, player2);
        assertNotNull(boardGame.getStartTime());
    }

    @Test
    public void initialStateTest() {
        assertEquals(0, boardGame.hits(player1));
        assertEquals(0, boardGame.hits(player2));
        assertEquals(0, boardGame.guesses(player1).size());
        assertEquals(0, boardGame.guesses(player2).size());
        assertTrue(boardGame.getWinner().isEmpty());
        assertFalse(boardGame.playerTurn() == null);
    }

    @Test
    public void playerNotInTheGameTest() {
        var p = Player.create("other");
        var e = assertThrows(IllegalArgumentException.class,
                             () -> boardGame.getBoard(p));
        assertEquals(BoardGame.MSG_PLAYER_NOT_IN_GAME, e.getMessage());
    }

    @Test
    public void playerInTheGameTest() {
        assertNotNull(boardGame.getBoard(player1));
        assertNotNull(boardGame.getBoard(player2));
    }

    @Test
    public void wrongTurnTest() {
        var player = boardGame.playerTurn();
        boardGame.guess(player, 0, 0);
        var exception = assertThrows(IllegalArgumentException.class, () -> boardGame.guess(player, 0, 0));
        assertEquals(BoardGame.MSG_WRONG_PLAYER, exception.getMessage());
    }

    @Test
    public void changeTurnTest() {
        var player = boardGame.playerTurn();
        boardGame.guess(player, 0, 0);
        var otherPlayer = boardGame.playerTurn();
        assertFalse(player == otherPlayer);
    }

    @Test
    public void guessesTest() {
        var player = boardGame.playerTurn();
        boardGame.guess(player, 0, 0);
        var otherPlayer = boardGame.playerTurn();
        boardGame.guess(otherPlayer, 0, 0);
        boardGame.guess(player, 1, 0);
        assertEquals(1, boardGame.guesses(otherPlayer).size());
        assertEquals(2, boardGame.guesses(player).size());
    }

    @Test
    public void drawTest() {
        var player = boardGame.playerTurn();
        boardGame.guess(player, 0, 0);
        var otherPlayer = boardGame.playerTurn();
        boardGame.guess(otherPlayer, 0, 0);
        boardGame.guess(player, 1, 0);
        assertTrue(boardGame.getWinner().isEmpty());
    }

    @Test
    public void duplicateGuessTest() {
        Player player = boardGame.playerTurn();
        Player otherPlayer = boardGame.waitingPlayer();
        boardGame.guess(player, 0, 0);
        boardGame.guess(otherPlayer, 0, 0);
        Exception e = assertThrows(IllegalArgumentException.class, () -> boardGame.guess(player, 0, 0));
        assertEquals(BoardGame.MSG_DUPLICATED_GUESS, e.getMessage());
    }

    @Test
    public void winnerTest() {
        boardGame.addShip(boardGame.player1(), ShipPosition.vertical(Ship.CARRIER, 0, 0));
        boardGame.addShip(boardGame.player2(), ShipPosition.vertical(Ship.CARRIER, 0, 0));
        var expectWinner = boardGame.playerTurn();
        boardGame.guess(expectWinner, 0, 0);
        var loser = boardGame.playerTurn();
        boardGame.guess(loser, 0, 0);
        boardGame.guess(expectWinner, 0, 1);
        boardGame.guess(loser, 1, 0);
        assertEquals(expectWinner, boardGame.getWinner().get());
    }

    @Test
    public void getSunkShipsTest() {
        var sunkShip = ShipPosition.vertical(Ship.DESTROYER, 2, 0);
        var shipPositions = List.of(ShipPosition.vertical(Ship.SUBMARINE, 0, 0),
                                    ShipPosition.vertical(Ship.CARRIER, 1, 0),
                                    sunkShip);
        Collection<Guess> hitGuesses = List.of(Guess.hit(0, 0),
                                               Guess.hit(0, 1),
                                               Guess.hit(0, 2),
                                               Guess.hit(0, 3),
                                               Guess.hit(1, 0),
                                               Guess.hit(1, 1),
                                               Guess.hit(1, 2),
                                               Guess.hit(2, 1),
                                               Guess.hit(2, 2));
        var result = BoardGame.getSunkShips(shipPositions, hitGuesses, List.of(sunkShip));
        assertEquals(1, result.size());
        assertEquals(Ship.SUBMARINE, result.get(0).getShip());
    }

    @Test
    public void getSunkShipsWithoutPreviousSunkShipsTest() {
        var willS1nk1 = ShipPosition.vertical(Ship.SUBMARINE, 0, 0);
        var willSink2 = ShipPosition.vertical(Ship.DESTROYER, 2, 0);
        var shipPositions = List.of(willS1nk1,
                                    ShipPosition.vertical(Ship.CARRIER, 1, 0),
                                    willSink2);
        Collection<Guess> hitGuesses = List.of(Guess.hit(0, 0),
                                               Guess.hit(0, 1),
                                               Guess.hit(0, 2),
                                               Guess.hit(0, 3),
                                               Guess.hit(1, 0),
                                               Guess.hit(2, 0),
                                               Guess.hit(2, 1));
        var result = BoardGame.getSunkShips(shipPositions, hitGuesses);
        assertTrue(result.contains(willS1nk1));
        assertTrue(result.contains(willSink2));
    }

    @Test
    public void verifySinkTest() {
        var shipPos = ShipPosition.vertical(Ship.SUBMARINE, 0, 0);
        var hitGuesses = List.of(Guess.hit(0, 0),
                                 Guess.hit(0, 1),
                                 Guess.hit(0, 2),
                                 Guess.hit(0, 3));
        var missedGuessesForAShip = List.of(Guess.hit(1, 0),
                                            Guess.hit(1, 1),
                                            Guess.hit(1, 2));
        assertTrue(BoardGame.verifySinkShip(hitGuesses, shipPos));
        assertFalse(BoardGame.verifySinkShip(missedGuessesForAShip, shipPos));
    }

    @Test
    public void sinkShipTest() {
        Player sinker = boardGame.playerTurn();
        Player other = boardGame.waitingPlayer();
        for (int i = 0; i < Ship.values().length; i++) {
            var ship = Ship.values()[i];
            boardGame.addShip(other, ShipPosition.vertical(ship, i, 0));
        }
        for (int i = 0; i < Ship.values().length; i++) {
            var ship = Ship.values()[i];
            for (int j = 0; j < ship.getSpaces(); j++) {
                boardGame.guess(sinker, i, j);
                boardGame.guess(other, i, j);
            }
        }
        var totalGuessesToSinkAllShips = Stream.of(Ship.values()).mapToInt(Ship::getSpaces).sum();
        assertEquals(totalGuessesToSinkAllShips, boardGame.guesses(sinker).size());
        assertEquals(Ship.values().length, boardGame.shipsSunkenBy(sinker));
        assertEquals(0, boardGame.shipsSunkenBy(other));
    }

    @Test
    public void scoreTest() {
        boardGame.addShip(boardGame.player1(), ShipPosition.vertical(Ship.CARRIER, 0, 0));
        boardGame.addShip(boardGame.player2(), ShipPosition.vertical(Ship.CARRIER, 0, 0));
        var highScorePlayer = boardGame.playerTurn();
        boardGame.guess(highScorePlayer, 0, 0);
        var lowScorePlayer = boardGame.playerTurn();
        boardGame.guess(lowScorePlayer, 0, 0);
        boardGame.guess(highScorePlayer, 0, 1);
        boardGame.guess(lowScorePlayer, 1, 0);
        boardGame.guess(highScorePlayer, 0, 2);
        boardGame.guess(lowScorePlayer, 2, 0);
        boardGame.guess(highScorePlayer, 0, 3);
        boardGame.guess(lowScorePlayer, 3, 0);
        assertEquals(4, boardGame.hits(highScorePlayer));
        assertEquals(1, boardGame.hits(lowScorePlayer));
    }

}
