package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class Rook extends Piece {

    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/rook");
        } else {
            image = getImage("/pieces/rook1");
        }
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (preCol == targetCol || preRow == targetRow) {
                return isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Rock(" + (color == 0 ? "W" : "B") + "){}";
    }
}
