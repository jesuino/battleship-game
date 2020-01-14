package org.fxapps.battleship.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

    public static final int ROWS = 10;
    public static final int COLS = 10;

    public static final String MSG_OUT_OF_RANGE = String.format("You can't set a position out of range. (MAX X is %d and MAX Y is %d).",
                                                                COLS, ROWS);
    public static final String MSG_SHIP_OUT_OF_RANGE = "The ship is out of the board boundaries.";
    public static final String MSG_SHIP_CONFLICT = "The ship conflicts with another ship.";

    private List<ShipPosition> shipsPositions = new ArrayList<>();
    private boolean[][] boardState;
    private int rows;
    private int cols;

    public static Board create() {
        return create(ROWS, COLS);
    }

    public static Board create(int rows, int cols) {
        Board board = new Board();
        board.rows = rows;
        board.cols = cols;
        board.boardState = new boolean[rows][cols];
        return board;
    }

    void placeShip(ShipPosition shipPosition) {
        checkPositions(shipPosition.getX(), shipPosition.getY());
        validateShip(shipPosition);
        shipsPositions.add(shipPosition);
        setShipPositionState(shipPosition, true);
    }

    public boolean removeShip(Ship ship) {
        final var shipsToRemove = shipsPositions.stream()
                                                .filter(sp -> sp.getShip() == ship)
                                                .peek(sp -> setShipPositionState(sp, false))
                                                .collect(Collectors.toList());
        shipsPositions.removeAll(shipsToRemove);
        return shipsToRemove.size() > 0;
    }

    boolean stateAt(int x, int y) {
        checkPositions(x, y);
        return boardState[x][y];
    }

    void checkPositions(int x, int y) {
        if (x >= COLS || y >= ROWS) {
            throw new IllegalArgumentException(MSG_OUT_OF_RANGE);
        }
    }

    int getRows() {
        return rows;
    }

    int getCols() {
        return cols;
    }

    Collection<ShipPosition> getShipsPositions() {
        return Collections.unmodifiableCollection(shipsPositions);
    }

    private void setShipPositionState(ShipPosition shipPosition, Boolean value) {
        var x = shipPosition.getX();
        var y = shipPosition.getY();
        var isVertical = shipPosition.isVertical();
        if (isVertical) {
            for (int j = y; j < shipPosition.getEndY(); j++) {
                boardState[x][j] = value;
            }

        } else {
            for (int i = x; i < shipPosition.getEndX(); i++) {
                boardState[i][y] = value;
            }
        }
    }

    private void validateShip(ShipPosition shipPosition) {
        int x = shipPosition.getX();
        int y = shipPosition.getY();
        if (shipPosition.isVertical()) {
            validateVerticalShip(x, y, shipPosition.getEndY());
        } else {
            validateHorizontalShip(x, y, shipPosition.getEndX());
        }

    }

    private void validateVerticalShip(int x, int y, int endY) {
        if (endY >= ROWS) {
            throw new IllegalArgumentException(MSG_SHIP_OUT_OF_RANGE);
        }

        for (int j = y; j < endY; j++) {
            if (boardState[x][j]) {
                throw new IllegalArgumentException(MSG_SHIP_CONFLICT);
            }
        }
    }

    private void validateHorizontalShip(int x, int y, int endX) {
        if (endX >= COLS) {
            throw new IllegalArgumentException(MSG_SHIP_OUT_OF_RANGE);
        }

        for (int i = x; i < endX; i++) {
            if (boardState[i][y]) {
                throw new IllegalArgumentException(MSG_SHIP_CONFLICT);
            }
        }
    }

}
