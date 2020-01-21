package org.fxapps.battleship.bot;

import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Guess;

public interface BattleshipBot {

    Guess newGuess(Board botBoard);

}
