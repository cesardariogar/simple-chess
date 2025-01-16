package edu.cesargarcia.pieces;

import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import java.awt.image.BufferedImage;

public class King extends Piece {

    public King(int color, int col, int row) {
        super(color, col, row);
        type = PieceType.KING;

        if (color == GamePanel.WHITE) {
            image = getImage("/pieces/king");
        } else {
            image = getImage("/pieces/king1");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            boolean isValidKingMovement =
                    (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1) ||
                    (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1);

            if (isValidSquare(targetCol, targetRow) && isValidKingMovement) {
                return true;
            }

            // CASTLING
            if(!moved) {
                hittingP = getHittingPiece(targetCol, targetRow);

                // Right castling
                if(targetCol == preCol + 2  && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    for (Piece piece: GamePanel.simPieces) {
                        if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }

                // Left castling
                if(targetCol == preCol - 2  && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    boolean pieceIsRook = false;
                    boolean pieceInTheMiddleFound = false;

                    for (Piece piece: GamePanel.simPieces) {
                        // Check if B1 or B8 has a hittingPiece
                        if(piece.col == preCol - 3 && piece.row == targetRow) {
                            pieceInTheMiddleFound = true;
                        }

                        // Check if A1 or A8 has a Rock
                        if(piece.col == preCol - 4 && piece.row == targetRow) {
                            pieceIsRook = true;
                        }

                        if(!pieceInTheMiddleFound && pieceIsRook && !piece.moved) {
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public BufferedImage getImage(String imagePath) {
        return super.getImage(imagePath);
    }

    @Override
    public String toString() {
        return "King(" + (color == 0 ? "W" : "B") + "){}";
    }
}
