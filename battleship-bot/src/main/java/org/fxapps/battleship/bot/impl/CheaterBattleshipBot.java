package org.fxapps.battleship.bot.impl;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.ShipPosition;

/**
 * 
 * A bot that can cheat on ships positions and randomly hits ships
 *  
 * @author wsiqueir
 *
 */
public class CheaterBattleshipBot extends BattleshipRandomBot {

    Random random = new Random();
    private int chancesOfShipHit;
    private List<Location> validLocation;

    public CheaterBattleshipBot(int chancesOfShipHit, List<ShipPosition> otherPlayerShipsPositions) {
        if (chancesOfShipHit > 100)
            chancesOfShipHit = 100;
        if (chancesOfShipHit < 0)
            chancesOfShipHit = 0;
        this.chancesOfShipHit = chancesOfShipHit;
        this.validLocation = otherPlayerShipsPositions.stream()
                                                      .flatMap(sp -> sp.locations().stream())
                                                      .collect(Collectors.toList());
    }

    @Override
    public Location newLocation(int rows, int cols) {
        if (chancesOfShipHit >= random.nextInt(101)) {
            return cheatGuess();
        }
        return super.newLocation(rows, cols);
    }

    private Location cheatGuess() {
        int pos = random.nextInt(validLocation.size());
        Location location = validLocation.remove(pos);
        history.add(location);
        return location;
    }

}
