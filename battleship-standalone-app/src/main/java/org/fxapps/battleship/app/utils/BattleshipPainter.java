package org.fxapps.battleship.app.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.fxapps.battleship.model.Board;
import org.fxapps.battleship.model.Guess;
import org.fxapps.battleship.model.Location;
import org.fxapps.battleship.model.ShipPosition;

public class BattleshipPainter {

    static Map<String, Image> imageCache = new HashMap<>();
    final static Random random = new Random();

    private BattleshipPainter() {}

    public static void paintBoard(Canvas canvas, Board board) {
        paintBoard(canvas, board.getShipsPositions(), board.getCols(), board.getCols());
    }

    public static void paintBoard(Canvas canvas, List<ShipPosition> shipPositions, int cols, int rows) {
        paintBoard(canvas, shipPositions, Collections.emptyList(), cols, rows);
    }

    public static void paintBoard(Canvas canvas, List<ShipPosition> shipPositions) {
        paintBoard(canvas, shipPositions, Collections.emptyList(), Board.DEFAULT_COLS, Board.DEFAULT_ROWS);
    }

    public static void paintBoard(Canvas canvas, List<ShipPosition> shipPositions, List<Guess> guesses) {
        paintBoard(canvas, shipPositions, guesses, Board.DEFAULT_COLS, Board.DEFAULT_ROWS);
    }

    public static void paintBoard(Canvas canvas, List<ShipPosition> shipsPositions, List<Guess> guesses, int boardCols, int boardRows) {
        final var tileWidth = canvas.getWidth() / boardCols;
        final var tileHeight = canvas.getHeight() / boardRows;
        var ctx = canvas.getGraphicsContext2D();
        paintEmptyBoard(ctx, boardCols, boardRows, tileWidth, tileHeight);
        paintShips(ctx, shipsPositions, tileWidth, tileHeight);
        paintsGuesses(ctx, guesses, tileWidth, tileHeight);
    }

    public static Location getLocationOnBoard(MouseEvent e) {
        var target = (Canvas) e.getSource();
        return getLocationOnBoard(e, target.getWidth(), target.getHeight());
    }

    public static Location getLocationOnBoard(MouseEvent e, double width, double height) {
        var x = e.getX();
        var y = e.getY();
        int posX = (int) (x / (width / Board.DEFAULT_COLS));
        int posY = (int) (y / (height / Board.DEFAULT_ROWS));
        return Location.of(posX, posY);
    }

    private static void paintsGuesses(GraphicsContext ctx, List<Guess> guesses, double tileWidth, double tileHeight) {
        var fireImage = imageCache.computeIfAbsent("/images/fire.png", Image::new);
        var splashImage = imageCache.computeIfAbsent("/images/splash.png", Image::new);
        for (Guess guess : guesses) {
            var image = splashImage;
            if (guess.isHit()) {
                image = fireImage;
            }
            ctx.drawImage(image,
                          guess.getLocation().x() * tileWidth,
                          guess.getLocation().y() * tileHeight,
                          tileWidth,
                          tileHeight);
        }
    }

    private static void paintEmptyBoard(GraphicsContext ctx, int boardCols, int boardRows, final double tileWidth, final double tileHeight) {
        ctx.setLineWidth(0.1);
        ctx.setStroke(Color.GAINSBORO);
        ctx.setFont(Font.font(tileHeight / 5));
        for (int i = 0; i < boardCols; i++) {
            for (int j = 0; j < boardRows; j++) {
                var x = i * tileWidth;
                var y = j * tileHeight;
                ctx.setFill(Color.ROYALBLUE);
                ctx.fillRect(x, y, tileWidth, tileHeight);
                ctx.strokeRect(x, y, tileWidth, tileHeight);
                if (i == 0) {
                    ctx.setFill(Color.WHITE);
                    ctx.fillText(j + 1 + "", 2, y + tileHeight - 2);
                }
                if (j == 0) {
                    ctx.setFill(Color.WHITE);
                    char charCode = (char) (65 + i);
                    ctx.fillText(String.valueOf(charCode), x + 2, 15);
                }
            }
        }
    }

    private static void paintShips(GraphicsContext ctx, List<ShipPosition> shipsPositions, final double tileWidth, final double tileHeight) {
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
