package org.fxapps.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationTest {

    @Test
    public void immutabilityTest() {
        assertEquals(Location.of(0, 0), Location.of(0, 0));

    }

}
