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

        if (maxGuess == history.size()) {
            throw new IllegalStateException(MSG_NO_MORE_GUESS);
        }

        for (int i = 0; i < cols && history.contains(location); i++) {
            var x = location.getX();
            x = x < cols - 1 ? x + 1 : 0;
            location = location.withX(x);
            for (int j = 0; j < rows && history.contains(location); j++) {
                var y = location.getY();
                y = y < rows - 1 ? y + 1 : 0;
                location = location.withY(y);
            }
        }
        history.add(location);
        return location;
    }

}
