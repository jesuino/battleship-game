package org.fxapps.battleship.app.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.ShipPosition;

public class BattleshipPainter {

    static Map<String, Image> imageCache = new HashMap<>();
    final static Random random = new Random();

    private BattleshipPainter() {}

    public static void paintBoard(Board board, Canvas canvas) {
        final var tileWidth = canvas.getWidth() / board.getCols();
        final var tileHeight = canvas.getHeight() / board.getRows();
        var ctx = canvas.getGraphicsContext2D();
        ctx.setLineWidth(0.2);
        ctx.setStroke(Color.GAINSBORO);
        for (int i = 0; i < board.getCols(); i++) {
            for (int j = 0; j < board.getRows(); j++) {
                var x = i * tileWidth;
                var y = j * tileHeight;
                ctx.setFill(Color.ROYALBLUE);
                ctx.fillRect(x, y, tileWidth, tileHeight);
                ctx.strokeRect(x, y, tileWidth, tileHeight);
            }
        }
        var shipsPositions = board.getShipsPositions();

        for (ShipPosition shipPosition : shipsPositions) {
            var ship = shipPosition.getShip();
            var shipImageName = "/images/" + ship.getName().toLowerCase() + ".png";
            var shipX = (shipPosition.getX() * tileWidth) + tileWidth / 3.0;
            var shipY = (shipPosition.getY() * tileHeight) + tileHeight / 3.0;
            final var shipWidth = shipPosition.isVertical() ? tileWidth : tileWidth * ship.getSpaces();
            final var shipHeight = shipPosition.isVertical() ? tileHeight * ship.getSpaces() : tileHeight;
            String keyName = ship.getName() + (shipPosition.isVertical() ? "_vertical" : "");
            var shipImage = imageCache.computeIfAbsent(keyName, v -> {
                if (shipPosition.isVertical()) {
                    return new Image(shipImageName);
                } else {
                    return rotateImage(shipImageName);
                }
            });
            ctx.drawImage(shipImage, shipX, shipY, shipWidth - tileWidth / 2.0, shipHeight - tileHeight / 2.0);
        }
    }

    private static Image rotateImage(String imageName) {
        ImageView iv = new ImageView(new Image(imageName));
        iv.setRotate(270);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return iv.snapshot(params, null);
    }

}
