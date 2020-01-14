package org.fxapps.battleship.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.fxapps.battleship.model.Ship.BATTLESHIP;
import static org.fxapps.battleship.model.Ship.CARRIER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

    Board board;

    @BeforeEach
    public void init() {
        board = Board.create();
    }
    
    @Test
    public void initConditionsTest() {
        assertEquals(Board.COLS, board.getCols());
        assertEquals(Board.ROWS, board.getRows());
    }
    

    @Test
    public void outOfRangeShipPositionTest() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> board.placeShip(ShipPosition.vertical(Ship.BATTLESHIP, Board.ROWS, 0)));
        assertEquals(Board.MSG_OUT_OF_RANGE, exception.getMessage());
    }

    @Test
    public void conflictingVerticalShipTest() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            board.placeShip(ShipPosition.vertical(CARRIER, 0, 0));
            board.placeShip(ShipPosition.vertical(Ship.BATTLESHIP, 0, 2));
        });
        assertEquals(Board.MSG_SHIP_CONFLICT, exception.getMessage());
        assertEquals(1, board.getShipsPositions().size());
    }

    @Test
    public void conflictingHorizontalShipTest() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            board.placeShip(ShipPosition.vertical(CARRIER, 0, 0));
            board.placeShip(ShipPosition.horizontal(Ship.BATTLESHIP, 0, 2));
        });
        assertEquals(Board.MSG_SHIP_CONFLICT, exception.getMessage());
        assertEquals(1, board.getShipsPositions().size());
    }

    @Test
    public void outOfRangeShipSizeTest() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> board.placeShip(ShipPosition.vertical(CARRIER, 0, Board.ROWS - 2)));
        assertEquals(Board.MSG_SHIP_OUT_OF_RANGE, exception.getMessage());
    }

    @Test
    public void outOfRangeHorizontalShipSizeTest() {
        var exception = assertThrows(IllegalArgumentException.class,
                                     () -> board.placeShip(ShipPosition.horizontal(CARRIER, Board.COLS - 2, 0)));
        assertEquals(Board.MSG_SHIP_OUT_OF_RANGE, exception.getMessage());
    }

    @Test
    public void successVerticalShipPositionTest() {
        var board = Board.create();
        board.placeShip(ShipPosition.vertical(CARRIER, 0, 0));
        for (int i = 0; i < CARRIER.getSpaces(); i++) {
            assertTrue(board.stateAt(0, i));
        }
        assertEquals(1, board.getShipsPositions().size());
    }

    @Test
    public void successHorizontalShipPositionTest() {
        board.placeShip(ShipPosition.horizontal(CARRIER, 0, 0));
        for (int i = 0; i < CARRIER.getSpaces(); i++) {
            assertTrue(board.stateAt(i, 0));
        }
        assertEquals(1, board.getShipsPositions().size());
    }

    @Test
    public void shipPositionRemoval() {
        var pos = ShipPosition.create(BATTLESHIP, 0, 0, false);
        var pos2 = ShipPosition.create(BATTLESHIP, 0, 1, false);
        board.placeShip(pos);
        board.placeShip(pos2);
        board.placeShip(ShipPosition.create(CARRIER, 0, 2, false));
        for (int i = 0; i < pos.getShip().getSpaces(); i++) {
            assertTrue(board.stateAt(i, 0));
            assertTrue(board.stateAt(i, 1));
        }
        assertEquals(3, board.getShipsPositions().size());
        boolean shipRemoval = board.removeShip(Ship.BATTLESHIP);
        assertTrue(shipRemoval);
        for (int i = 0; i < pos.getShip().getSpaces(); i++) {
            assertFalse(board.stateAt(i, 0));
            assertFalse(board.stateAt(i, 1));
        }
        assertEquals(1, board.getShipsPositions().size());

    }

}
