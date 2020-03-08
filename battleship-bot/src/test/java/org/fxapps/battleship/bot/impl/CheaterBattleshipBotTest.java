package org.fxapps.battleship.bot.impl;

import java.util.stream.Collectors;

import org.fxapps.battleship.model.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheaterBattleshipBotTest {

    private CheaterBattleshipBot bot;

    @Test
    void guessAllLocationTest() {
        var positions = Board.randomShips().getShipsPositions();
        bot = new CheaterBattleshipBot(100, positions);
        var allLocations = positions.stream().flatMap(sp -> sp.locations().stream()).collect(Collectors.toList());
        allLocations.forEach(l -> bot.newLocation());
        assertEquals(allLocations.size(), bot.history.size());
        assertTrue(bot.history.containsAll(allLocations));
    }

}
