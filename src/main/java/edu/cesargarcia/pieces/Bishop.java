package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.BISHOP;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/bishop");
        } else {
            image = getImage("/pieces/bishop1");
        }
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Movement rules : bishop
        if (isWithinBoard(targetCol, targetRow)
                && !isSameSquare(targetCol, targetRow)) {

            // Diagonal
            if ((Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow))) {
                return isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow);
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Bishop(" + (color == 0 ? "W" : "B") + "){}";
    }
}
