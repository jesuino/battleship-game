package org.fxapps.battleship.model;

public enum Ship {

    CARRIER("Carrier", 5),
    BATTLESHIP("Battleship", 4),
    CRUISER("Cruiser", 3),
    SUBMARINE("Submarine", 3),
    DESTROYER("Destroyer", 2);

    private int spaces;
    private String name;

    Ship(String name, int spaces) {
        this.name = name;
        this.spaces = spaces;
    }

    public int getSpaces() {
        return spaces;
    }

    public String getName() {
        return name;
    }
   
}