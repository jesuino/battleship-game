package org.fxapps.battleship.model;

public class Guess {

    private Location location;
    private boolean hit;

    private Guess() {

    }

    public static Guess miss(int x, int y) {
        return create(x, y, false);
    }

    public static Guess hit(int x, int y) {
        return create(x, y, true);
    }

    public static Guess create(int x, int y, boolean hit) {
        Guess guess = new Guess();
        guess.location = Location.of(x, y);
        guess.hit = hit;
        return guess;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isHit() {
        return hit;
    }

}
