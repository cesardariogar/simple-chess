package edu.cesargarcia.pieces;

import edu.cesargarcia.Board;
import edu.cesargarcia.GamePanel;
import edu.cesargarcia.pieces.enums.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.isNull;

public abstract class Piece {

    public BufferedImage image;
    public int x, y;
    public int row, col;
    public int preRow, preCol;
    public int color;
    public Piece hittingP;
    public boolean moved;
    public boolean twoStepped;
    public PieceType type;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        this.x = getX(col);
        this.y = getY(row);
        this.preCol = col;
        this.preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public void updatePosition() {
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    public void checkEnPassant() {
        if(type == PieceType.PAWN) {
            if(Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }
        }
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        return (targetCol >= 0 && targetCol <= 7) && (targetRow >= 0 && targetRow <= 7);
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        return (preCol == targetCol && preRow == targetRow);
    }

    public Piece getHittingPiece(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingPiece(targetCol, targetRow);

        if (isNull(hittingP)) {
            return true;
        } else if (hittingP.color != this.color) {
            return true;
        } else {
            hittingP = null;
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        // Left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == c && p.row == targetRow) {
                    hittingP = p;
                    return true;
                }
            }
        }
        // Right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == c && p.row == targetRow) {
                    hittingP = p;
                    return true;
                }
            }
        }
        // Up
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == targetCol && p.row == r) {
                    hittingP = p;
                    return true;
                }
            }
        }
        // Down
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == targetCol && p.row == r) {
                    hittingP = p;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {

        // UP
        if (preRow > targetRow) {
            // Up Left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece p : GamePanel.simPieces) {
                    if (p.col == c && p.row == preRow - diff) {
                        hittingP = p;
                        return true;
                    }
                }
            }
            // Up Right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece p : GamePanel.simPieces) {
                    if (p.col == c && p.row == preRow - diff) {
                        hittingP = p;
                        return true;
                    }
                }
            }
        }

        // DOWN
        if (preRow < targetRow) {
            // Down Left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece p : GamePanel.simPieces) {
                    if (p.col == c && p.row == preRow + diff) {
                        hittingP = p;
                        return true;
                    }
                }
            }
            // Down Right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece p : GamePanel.simPieces) {
                    if (p.col == c && p.row == preRow + diff) {
                        hittingP = p;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
