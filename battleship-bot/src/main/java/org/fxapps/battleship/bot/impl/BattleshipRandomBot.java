package org.fxapps.battleship.bot.impl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.fxapps.battleship.bot.BattleshipBot;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.Player;

public class BattleshipRandomBot extends Player implements BattleshipBot {

    static final String MSG_NO_MORE_GUESS = "No more Guess!";
    Random random = new Random();
    Set<Location> history = new HashSet<>();

    public BattleshipRandomBot() {
        super("Random Bot");
    }

    @Override
    public Location newLocation(Board botBoard) {
        final var rows = botBoard.getRows();
        final var cols = botBoard.getCols();
        final var maxGuess = rows * cols;
        var location = Location.of(random.nextInt(cols),
                                   random.nextInt(rows));
        var maxAttempts = maxGuess;
        if (maxGuess == history.size()) {
            throw new IllegalStateException(MSG_NO_MORE_GUESS);
        }
        boolean changedY = false;
        while (containsLocation(location) && maxAttempts > 0) {
            if (changedY) {
                var x = location.getX() < cols ? location.getX() + 1 : 0;
                location = Location.of(x, location.getY());
                changedY = false;
            } else {
                var y = location.getY() < rows ? location.getY() + 1 : 0;
                location = Location.of(location.getX(), y);
                changedY = true;
            }
            maxAttempts--;
        }
        history.add(location);
        return location;
    }

    private boolean containsLocation(Location location) {
        return history.contains(location);
    }

}
