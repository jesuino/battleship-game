package org.fxapps.battleship.model;

public class Guess {

    private int x;
    private int y;
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
        guess.x = x;
        guess.y = y;
        guess.hit = hit;
        return guess;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHit() {
        return hit;
    }

}
