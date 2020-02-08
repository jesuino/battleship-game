package org.fxapps.battleship.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

/**
 * An board based game with two players. This class controls the current player turn and the score.
 * 
 * @author wsiqueir
 *
 */
public class BoardGame {

    static final String MSG_WRONG_PLAYER = "Not this player turn.";

    static final String MSG_PLAYER_NOT_IN_GAME = "Player is not in the game";

    static final String MSG_DUPLICATED_GUESS = "Guess already made for player.";

    private LocalDateTime startTime;

    private Player currentPlayer;

    private Player player1;
    private Player player2;

    private Map<Player, Board> playerBoard = new HashMap<>();
    private Map<Player, List<Guess>> guesses = new HashMap<>();

    private Map<Player, List<ShipPosition>> sunkenShipsByPlayer = new HashMap<>();

    public static BoardGame create(Player player1, Player player2) {
        var game = new BoardGame();
        game.startTime = LocalDateTime.now();
        game.playerBoard.put(player1, Board.create());
        game.playerBoard.put(player2, Board.create());
        game.player1 = player1;
        game.player2 = player2;
        game.guesses.putAll(Map.of(player1, new ArrayList<>(), player2, new ArrayList<>()));
        game.sunkenShipsByPlayer.putAll(Map.of(player1, new ArrayList<>(), player2, new ArrayList<>()));
        game.currentPlayer = new Random().nextBoolean() ? player1 : player2;
        return game;
    }

    boolean guess(Player player, int x, int y) {
        if (player != currentPlayer) {
            throw new IllegalArgumentException(MSG_WRONG_PLAYER);
        }
        checkDuplicateGuess(player, x, y);
        var otherPlayer = waitingPlayer();
        var target = playerBoard.get(otherPlayer);
        var hit = target.stateAt(x, y);
        guesses.get(currentPlayer).add(Guess.create(x, y, hit));
        updateSinkShipsBy(player);
        currentPlayer = currentPlayer == player1 ? player2 : player1;
        return hit;
    }

    /**
     * Calculate the winner
     * 
     * @return
     *  An optional containing the winner or empty if it is a draw
     */
    Optional<Player> getWinner() {
        Player winningPlayer = null;
        var scorePlayer1 = hits(player1);
        var scorePlayer2 = hits(player2);
        if (scorePlayer1 > scorePlayer2) {
            winningPlayer = player1;
        } else if (scorePlayer2 > scorePlayer1) {
            winningPlayer = player2;
        }
        return Optional.ofNullable(winningPlayer);
    }

    void addShip(Player player, ShipPosition shipPosition) {
        playerBoard.get(player).placeShip(shipPosition);
    }

    LocalDateTime getStartTime() {
        return startTime;
    }

    Player playerTurn() {
        return currentPlayer;
    }

    Player waitingPlayer() {
        return currentPlayer == player1 ? player2 : player1;
    }

    Board getBoard(Player player) {
        playerBoard.computeIfAbsent(player, k -> {
            throw new IllegalArgumentException(MSG_PLAYER_NOT_IN_GAME);
        });
        return playerBoard.get(player);
    }

    int hits(Player player) {
        return (int) guesses.get(player).stream().filter(Guess::isHit).count();
    }

    List<Guess> guesses(Player player) {
        return Collections.unmodifiableList(guesses.get(player));
    }

    Player player1() {
        return player1;
    }

    Player player2() {
        return player2;
    }

    int shipsSunkenBy(Player player) {
        return sunkenShipsByPlayer.get(player).size();
    }

    private void updateSinkShipsBy(Player player) {
        var otherPlayer = waitingPlayer();
        var otherPlayerShips = playerBoard.get(otherPlayer).getShipsPositions();
        var sunkenShips = sunkenShipsByPlayer.get(player);
        var playerHitGuesses = guesses.get(player).stream().filter(Guess::isHit).collect(toList());
        var sunkShips = getSunkShips(otherPlayerShips, playerHitGuesses, sunkenShips);
        sunkShips.forEach(sunkenShipsByPlayer.get(player)::add);
    }

    private void checkDuplicateGuess(Player player, int x, int y) {
        Location location = Location.of(x, y);
        guesses.get(player)
               .stream().filter(g -> g.getLocation() == location)
               .findFirst().ifPresent(g -> {
                   throw new IllegalArgumentException(MSG_DUPLICATED_GUESS);
               });
    }

    static List<ShipPosition> getSunkShips(Collection<ShipPosition> positions,
                                           Collection<Guess> guesses) {
        return getSunkShips(positions, guesses, Collections.emptyList());
    }

    static List<ShipPosition> getSunkShips(Collection<ShipPosition> positions,
                                           Collection<Guess> guesses,
                                           Collection<ShipPosition> sunkenShips) {
        return positions.stream()
                        .filter(shipPos -> !sunkenShips.contains(shipPos))
                        .filter(shipPos -> verifySinkShip(guesses, shipPos))
                        .collect(toList());
    }

    static boolean verifySinkShip(Collection<Guess> guesses, ShipPosition shipPos) {
        for (int i = shipPos.getX(); i <= shipPos.getEndX(); i++) {
            for (int j = shipPos.getY(); j <= shipPos.getEndY(); j++) {
                if (! containsPosition(guesses, i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean containsPosition(Collection<Guess> guesses, final int x, final int y) {
        return guesses.stream()
                .map(Guess::getLocation)
                .anyMatch(location -> location == Location.of(x, y));

    }

}
