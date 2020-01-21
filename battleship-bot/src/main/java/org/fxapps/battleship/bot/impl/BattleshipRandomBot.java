package org.fxapps.battleship.bot.impl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.fxapps.battleship.bot.BattleshipBot;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Guess;
import org.fxapps.battleship.model.Player;

public class BattleshipRandomBot extends Player implements BattleshipBot {

    static final String MSG_NO_MORE_GUESS = "No more Guess!";
    Random random = new Random();
    Set<Guess> history = new HashSet<>();

    public BattleshipRandomBot() {
        super("Random Bot");
    }

    @Override
    public Guess newGuess(Board botBoard) {
        final var rows = botBoard.getRows();
        final var cols = botBoard.getCols();
        final var maxGuess = rows * cols;
        var x = random.nextInt(cols);
        var y = random.nextInt(rows);
        var maxAttempts = maxGuess;
        if (maxGuess == history.size()) {
            throw new IllegalStateException(MSG_NO_MORE_GUESS);
        }
        boolean changedY = false;
        while (containsGuess(x, y) && maxAttempts > 0) {
            if (changedY) {
                x = x < cols ? x + 1 : 0;
                changedY = false;
            } else {
                y = y < rows ? y + 1 : 0;
                changedY = true;
            }
            maxAttempts--;
        }
        Guess miss = Guess.miss(x, y);
        history.add(miss);
        return miss;
    }

    private boolean containsGuess(int x, int y) {
        return history.stream()
                      .anyMatch(guess -> guess.getX() == x &&
                                         guess.getY() == y);
    }

}
