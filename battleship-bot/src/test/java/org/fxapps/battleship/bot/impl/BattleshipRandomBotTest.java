package org.fxapps.battleship.bot.impl;

import org.fxapps.battleship.model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BattleshipRandomBotTest {

    private BattleshipRandomBot bot;
    private Board board;

    @BeforeEach
    public void init() {
        bot = new BattleshipRandomBot();
        board = Board.create(20, 20);
    }

    @Test
    void newGuessTest() {
        bot.newLocation(board);
        assertEquals(1, bot.history.size());
    }

    @Test
    void noMoreGuessTest() {
        final int totalGuess = board.getCols() * board.getRows();
        int guessCount = totalGuess;
        while (guessCount > 0) {
            bot.newLocation(board);
            guessCount--;
        }
        assertEquals(totalGuess, bot.history.size());
        Exception e = assertThrows(IllegalStateException.class, () -> bot.newLocation(board));
        assertEquals(BattleshipRandomBot.MSG_NO_MORE_GUESS, e.getMessage());
    }

}
