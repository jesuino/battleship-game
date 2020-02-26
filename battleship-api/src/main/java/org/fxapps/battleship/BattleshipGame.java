package org.fxapps.battleship;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.fxapps.battleship.model.BoardGame;
import org.fxapps.battleship.model.GameManager;
import org.fxapps.battleship.model.GameStats;
import org.fxapps.battleship.model.Player;
import org.fxapps.battleship.model.Ship;
import org.fxapps.battleship.model.ShipPosition;

public interface BattleshipGame {

    /**
     * Access this game's id
     * @return
     * The game ID
     */
    String getId();

    /**
     * Add a ship on vertical position for the given player only if the game is on preparation state.
     * 
     * @param player
     * @param ship
     * @param x
     * @param y
     */
    void addVerticalShip(Player player, Ship ship, int x, int y);

    /**
     * Add a ship on horizontal position for the given player only if the game is on preparation state.
     * 
     * @param player
     * @param ship
     * @param x
     * @param y
     */
    void addHorizontalShip(Player player, Ship ship, int x, int y);

    /**
     * 
     * Add for the player
     * @param player
     * @param shipsPositions
     */
    void addShips(Player player, List<ShipPosition> shipsPositions);

    /**
     * Remove positions with the given ship for the given player and throws exception if the game is not in the correct state. <br>
     * After ship is removed the game state changes to PREPARATION and player becomes unready.
     * 
     * @param player
     * @param ship
     */
    void removeShip(Player player, Ship ship);

    /**
     * Makes a player ready. Throws exception if player didn't add all ships.
     * @param player
     * The player that will be ready.
     */
    void ready(Player player);

    /**
     * Makes a player unready and the state comes back to PREPARATION. Throws exception if the game is not ready to start or in preparation state.
     * @param player
     */
    void unready(Player player);

    /**
     * 
     * Access the game stats.
     * @return
     */
    GameStats stats();

    /**
     * Checks if a player is ready.
     * @param player
     * @return
     */
    boolean isReady(Player player);

    /**
     * Guess a position on player's adversary board. Throws exception if the game state is not started.
     * @param player
     * Player guessing
     * @param x
     * x position
     * @param y
     * y position
     * @param result
     * a callback that accepts two boolean parameters: first is true if the guess was a hit and second if it caused a sink. 
     */
    void guess(Player player, int x, int y, BiConsumer<Boolean, Boolean> result);

    /**
     * Guess a position on player's adversary board. Throws exception if the game state is not started.
     * @param player
     * Player guessing
     * @param x
     * x position
     * @param y
     * y position
     */
    void guess(Player player, int x, int y);

    /**
     * The player that is winning the game 
     * @return
     * An optional containing the player or empty if the game is not started.
     */
    Optional<Player> winningPlayer();

    /**
     * Start the game if all conditions are satisfied. Otherwise throws exception.
     */
    void start();

    /**
     * Abort the game at any time.
     */
    void abort();

    /**
     * Returns the player that should guess now or empty if the game is finished or not initialized.
     * 
     * @return
     */
    Optional<Player> playerTurn();

    /**
     * Player that is not in current turn.
     * @return
     */
    Optional<Player> waitingPlayer();

    public static BattleshipGame create(Player player1, Player player2) {
        BoardGame boardGame = BoardGame.create(player1, player2);
        return GameManager.create(boardGame);
    }

}
