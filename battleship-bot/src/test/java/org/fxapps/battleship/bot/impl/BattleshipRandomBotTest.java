package org.fxapps.battleship.bot.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BattleshipRandomBotTest {

    private BattleshipRandomBot bot;

    @BeforeEach
    public void init() {
        bot = new BattleshipRandomBot();
    }

    @Test
    void newGuessTest() {
        bot.newLocation();
        assertEquals(1, bot.history.size());
    }

    @Test
    void noMoreGuessTest() {
        int cols = 20, rows = 20;
        final int totalGuess = cols * rows;
        int guessCount = totalGuess;
        while (guessCount != 0) {
            bot.newLocation(rows, cols);
            guessCount--;
        }
        assertEquals(totalGuess, bot.history.size());
        Exception e = assertThrows(IllegalStateException.class, () -> bot.newLocation(rows, cols));
        assertEquals(BattleshipRandomBot.MSG_NO_MORE_GUESS, e.getMessage());
    }

}
