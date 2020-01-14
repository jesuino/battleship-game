package org.fxapps.battleship.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * Game Information
 * 
 * @author wsiqueir
 *
 */
public class GameStats {

    private String id;
    private GameState state;
    private LocalDateTime started;
    private Map<Player, List<Guess>> guesses;
    private Map<Player, Integer> sunkenShips;
    private Optional<Player> winner;

    private GameStats() {

    }

    public static GameStats of(String id,
                               GameState state,
                               LocalDateTime started,
                               Map<Player, List<Guess>> guesses,
                               Map<Player, Integer> sunkenShips,
                               Optional<Player> winner) {
        GameStats stats = new GameStats();
        stats.id = id;
        stats.state = state;
        stats.started = started;
        stats.guesses = guesses;
        stats.sunkenShips = sunkenShips;
        stats.winner = winner;
        return stats;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public Map<Player, List<Guess>> getGuesses() {
        return guesses;
    }

    public Map<Player, Integer> getSunkenShips() {
        return sunkenShips;
    }

    public Optional<Player> getWinner() {
        return winner;
    }

    public GameState getState() {
        return state;
    }

}
