package org.fxapps.battleship.app.model;

import java.util.List;

import org.fxapps.battleship.model.ShipPosition;

public class GamePreparationData {

    private List<ShipPosition> shipsPositions;

    private Difficult difficult;

    private GamePreparationData(List<ShipPosition> shipsPositions,
                                Difficult difficult) {
        this.shipsPositions = shipsPositions;
        this.difficult = difficult;
    }

    public List<ShipPosition> getShipsPositions() {
        return shipsPositions;
    }

    public Difficult getDifficult() {
        return difficult;
    }

    public static GamePreparationData of(List<ShipPosition> shipsPositions, Difficult difficult) {
        return new GamePreparationData(shipsPositions, difficult);
    }

}