package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/queen");
        } else {
            image = getImage("/pieces/queen1");
        }
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            // Straight
            if (preCol == targetCol || preRow == targetRow) {
                return isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow);
            }

            // Diagonal
            if ((Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow))) {
                return isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow);
            }

        }
        return false;
    }

    @Override
    public String toString() {
        return "Queen(" + (color == 0 ? "W" : "B") + "){}";
    }
}
