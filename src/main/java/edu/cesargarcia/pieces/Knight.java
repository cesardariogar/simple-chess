package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.KNIGHT;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/knight");
        } else {
            image = getImage("/pieces/knight1");
        }
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Movement rules : Knight ratio 2:1 or 1:2
        boolean isValidKnightMovement = (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2);

        return isWithinBoard(targetCol, targetRow)
                && isValidSquare(targetCol, targetRow)
                && isValidKnightMovement;
    }

    @Override
    public String toString() {
        return "Knight(" + (color == 0 ? "W" : "B") + "){}";
    }
}
