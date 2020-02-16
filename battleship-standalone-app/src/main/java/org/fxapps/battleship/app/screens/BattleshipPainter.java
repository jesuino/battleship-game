package org.fxapps.battleship.app.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.fxapps.battleship.model.Board;

public class BattleshipPainter {

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
                if (board.stateAt(i, j)) {
                    ctx.setFill(Color.LIGHTSTEELBLUE);
                } else {
                    ctx.setFill(Color.ROYALBLUE);
                }
                ctx.fillRect(x, y, tileWidth, tileHeight);
                ctx.strokeRect(x, y, tileWidth, tileHeight);
            }
        }
    }

}
