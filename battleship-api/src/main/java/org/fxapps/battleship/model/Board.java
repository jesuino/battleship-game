package org.fxapps.battleship.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Board {

    Random random = new Random();

    public static final int DEFAULT_ROWS = 10;
    public static final int DEFAULT_COLS = 10;

    public static final String MSG_OUT_OF_RANGE = String.format("You can't set a position out of range. (MAX X is %d and MAX Y is %d).",
                                                                DEFAULT_COLS, DEFAULT_ROWS);
    public static final String MSG_SHIP_OUT_OF_RANGE = "The ship is out of the board boundaries.";
    public static final String MSG_SHIP_CONFLICT = "The ship conflicts with another ship.";

    private List<ShipPosition> shipsPositions = new ArrayList<>();
    private boolean[][] boardState;
    private int rows;
    private int cols;

    private Board() {}

    public static Board create() {
        return create(DEFAULT_ROWS, DEFAULT_COLS);
    }

    public static Board create(int rows, int cols) {
        Board board = new Board();
        board.rows = rows;
        board.cols = cols;
        board.boardState = new boolean[rows][cols];
        return board;
    }

    public static Board randomShips() {
        var board = create(DEFAULT_ROWS, DEFAULT_COLS);
        board.addRandomShipPositions();
        return board;
    }

    public static Board randomShips(int rows, int cols) {
        var board = create(rows, cols);
        board.addRandomShipPositions();
        return board;
    }

    public Optional<ShipPosition> placeShip(Ship ship, Location location, boolean isVertical) {
        ShipPosition shipPosition = ShipPosition.create(ship, location, isVertical);
        try {
            placeShip(shipPosition);
            return Optional.of(shipPosition);
        } catch (Exception e) {
            // TODO: process e
        }
        return Optional.empty();
    }

    public void placeShip(ShipPosition shipPosition) {
        checkPositions(shipPosition.getX(), shipPosition.getY());
        validateShip(shipPosition);
        shipsPositions.add(shipPosition);
        setShipPositionState(shipPosition, true);
    }

    /**
     * 
     * Removes all positions with a given ship
     * @param ship
     * @return
     * true if some ship was removed, false otherwise
     */
    public boolean removeShip(Ship ship) {
        final var shipsToRemove = shipsPositions.stream()
                                                .filter(sp -> sp.getShip() == ship)
                                                .collect(Collectors.toList());
        shipsPositions.removeAll(shipsToRemove);
        shipsToRemove.forEach(this::cancelShip);
        return !shipsToRemove.isEmpty();
    }

    public boolean stateAt(int x, int y) {
        checkPositions(x, y);
        return boardState[x][y];
    }

    void checkPositions(int x, int y) {
        if (isValidPosition(x, y)) {
            throw new IllegalArgumentException(MSG_OUT_OF_RANGE);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= getCols() || y >= getRows();
    }

    public List<ShipPosition> getShipsPositions() {
        return Collections.unmodifiableList(shipsPositions);
    }

    private void cancelShip(ShipPosition shipPosition) {
        setShipPositionState(shipPosition, false);
    }

    private void setShipPositionState(ShipPosition shipPosition, Boolean value) {
        var x = shipPosition.getX();
        var y = shipPosition.getY();
        var isVertical = shipPosition.isVertical();
        if (isVertical) {
            for (int j = y; j <= shipPosition.getEndY(); j++) {
                boardState[x][j] = value;
            }

        } else {
            for (int i = x; i <= shipPosition.getEndX(); i++) {
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
        if (endY >= getRows()) {
            throw new IllegalArgumentException(MSG_SHIP_OUT_OF_RANGE);
        }

        for (int j = y; j <= endY; j++) {
            if (boardState[x][j]) {
                throw new IllegalArgumentException(MSG_SHIP_CONFLICT);
            }
        }
    }

    private void validateHorizontalShip(int x, int y, int endX) {
        if (endX >= getCols()) {
            throw new IllegalArgumentException(MSG_SHIP_OUT_OF_RANGE);
        }

        for (int i = x; i <= endX; i++) {
            if (boardState[i][y]) {
                throw new IllegalArgumentException(MSG_SHIP_CONFLICT);
            }
        }
    }

    public void addRandomShipPositions() {
        var addedShips = this.getShipsPositions().stream().map(ShipPosition::getShip).collect(toList());
        var shipsToAdd = Stream.of(Ship.values()).filter(s -> !addedShips.contains(s)).collect(toList());
        while (!shipsToAdd.isEmpty()) {
            var ship = shipsToAdd.get(0);
            var isVertical = random.nextBoolean();
            int x, y;
            if (isVertical) {
                x = random.nextInt(getCols());
                y = random.nextInt(getRows() - ship.getSpaces());
            } else {
                x = random.nextInt(getCols() - ship.getSpaces());
                y = random.nextInt(getRows());
            }
            placeShip(ship, Location.of(x, y), isVertical).ifPresent(sp -> shipsToAdd.remove(sp.getShip()));
        }
    }

}
