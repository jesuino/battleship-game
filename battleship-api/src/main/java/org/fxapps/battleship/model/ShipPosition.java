package org.fxapps.battleship.model;

public class ShipPosition {

    private Ship ship;
    private int x;
    private int y;
    private int endX;
    private int endY;
    private boolean isVertical;

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

    @Override
    public String toString() {
        return "ShipPosition [ship=" + ship + ", x=" + x + ", y=" + y +
               ", endX=" + endX + ", endY=" + endY + ", isVertical=" + isVertical + "]";
    }

}
