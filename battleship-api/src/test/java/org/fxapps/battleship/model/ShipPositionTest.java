package org.fxapps.battleship.model;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShipPositionTest {

    @Test
    public void boundariesTest() {
        Ship ship = Ship.DESTROYER;
        ShipPosition sp = ShipPosition.vertical(ship, 0, 0);
        assertEquals(0, sp.getX());
        assertEquals(0, sp.getY());
        assertEquals(0, sp.getEndX());
        assertEquals(sp.getEndY(), ship.getSpaces() - 1);

        sp = ShipPosition.horizontal(ship, 0, 0);
        assertEquals(0, sp.getX());
        assertEquals(0, sp.getY());
        assertEquals(ship.getSpaces() - 1, sp.getEndX());
        assertEquals(0, sp.getEndY());
    }

    @Test
    public void locationsTest() {
        Ship ship = Ship.DESTROYER;
        var expectedVerticalLocations = IntStream.range(0, ship.getSpaces())
                                                 .mapToObj(i -> Location.of(0, i))
                                                 .collect(Collectors.toList());
        var expectedHorizontalLocations = IntStream.range(0, ship.getSpaces())
                                                   .mapToObj(i -> Location.of(i, 0))
                                                   .collect(Collectors.toList());
        var verticalLocations = ShipPosition.vertical(ship, 0, 0).locations();
        var horizontalLocations = ShipPosition.horizontal(ship, 0, 0).locations();

        assertEquals(expectedHorizontalLocations, horizontalLocations);
        assertEquals(expectedVerticalLocations, verticalLocations);
    }

}
