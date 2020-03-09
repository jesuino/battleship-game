package org.fxapps.battleship.app.model;

public enum Difficult {

    HARDEST(70),
    HARD(50),
    MEDIUM(25),
    EASY(10),
    EASIEST(1);

    int hitProbability;

    Difficult(int hitProbability) {
        this.hitProbability = hitProbability;
    }

    public int getHitProbability() {
        return hitProbability;
    }
}
