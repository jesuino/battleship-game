package org.fxapps.battleship.model;

public class Player {

    protected String name;

    public Player(String name) {
        this.name = name;
    }

    public static Player create(String name) {
        return new Player(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}