package org.fxapps.battleship.bot;

import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Guess;
import org.fxapps.battleship.model.Location;

public interface BattleshipBot {

    Location newLocation(Board botBoard);

}
