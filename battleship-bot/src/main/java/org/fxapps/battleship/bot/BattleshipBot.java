package org.fxapps.battleship.bot;

import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Location;

public interface BattleshipBot {

    default Location newLocation() {
        return newLocation(Board.DEFAULT_ROWS, Board.DEFAULT_COLS);
    }

    Location newLocation(int rows, int cols);

}
