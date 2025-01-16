package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.PAWN;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/pawn");
        } else {
            image = getImage("/pieces/pawn1");
        }
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {

            // Define the move value based on its color
            int moveValue = (color == GamePanel.WHITE) ? -1 : 1;

            // Check the hitting piece
            hittingP = getHittingPiece(targetCol, targetRow);

            // 1 square movement
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                return true;
            }

            // 2 square movement
            if (targetCol == preCol && targetRow == preRow + (moveValue * 2) && hittingP == null && !moved) {
                return true;
            }

            // Diagonal Movement && Capture (if enemy is on the next diagonal square)
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue
                    && hittingP != null && hittingP.color != this.color) {
                return true;
            }

            // En Passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
                for(Piece piece: GamePanel.simPieces) {
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Pawn(" + (color == 0 ? "W" : "B") + "){}";
    }
}
