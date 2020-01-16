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

    public static ShipPosition create(Ship ship, int x, int y, boolean isVertical) {
        ShipPosition shipPosition = new ShipPosition();
        shipPosition.ship = ship;
        shipPosition.x = x;
        shipPosition.y = y;
        shipPosition.isVertical = isVertical;
        if (isVertical) {
            shipPosition.endX = x;
            shipPosition.endY = y + ship.getSpaces();
        } else {
            shipPosition.endY = y;
            shipPosition.endX = x + ship.getSpaces();
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

}