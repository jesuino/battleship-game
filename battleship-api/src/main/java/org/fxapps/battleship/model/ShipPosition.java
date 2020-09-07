package org.fxapps.battleship.model;

import java.util.ArrayList;
import java.util.List;

public class ShipPosition {

    private Ship ship;
    private int x;
    private int y;
    private int endX;
    private int endY;
    private boolean isVertical;
    private List<Location> locations;

    public static ShipPosition horizontal(Ship ship, int x, int y) {
        return create(ship, x, y, false);
    }

    public static ShipPosition vertical(Ship ship, int x, int y) {
        return create(ship, x, y, true);
    }

    public static ShipPosition create(Ship ship, Location location, boolean isVertical) {
        return create(ship, location.x(), location.y(), isVertical);
    }

    public static ShipPosition create(Ship ship, int x, int y, boolean isVertical) {
        ShipPosition shipPosition = new ShipPosition();
        shipPosition.ship = ship;
        shipPosition.x = x;
        shipPosition.y = y;
        shipPosition.isVertical = isVertical;
        if (isVertical) {
            shipPosition.endX = x;
            shipPosition.endY = y + ship.getSpaces() - 1;
        } else {
            shipPosition.endY = y;
            shipPosition.endX = x + ship.getSpaces() - 1;
        }
        shipPosition.locations = new ArrayList<>();
        for (int i = x; i <= shipPosition.endX; i++) {
            for (int j = y; j <= shipPosition.endY; j++) {
                shipPosition.locations.add(Location.of(i, j));
            }
        }
        return shipPosition;
    }

    public Ship getShip() {
        return ship;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public List<Location> locations() {
        return locations;
    }

    @Override
    public String toString() {
        return "ShipPosition [ship=" + ship + ", x=" + x + ", y=" + y +
               ", endX=" + endX + ", endY=" + endY + ", isVertical=" + isVertical + "]";
    }

}