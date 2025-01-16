package edu.cesargarcia;

import edu.cesargarcia.movement.Mouse;
import edu.cesargarcia.pieces.*;
import edu.cesargarcia.pieces.enums.PieceType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GamePanel extends JPanel implements Runnable {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    final int FPS = 60;

    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // Pieces
    public static List<Piece> pieces = new ArrayList<>();
    public static List<Piece> simPieces = new ArrayList<>();
    private final List<Piece> promoPieces = new ArrayList<>();

    Piece activeP, checkingP;
    public static Piece castlingP;
    boolean canMove;
    boolean validSquare;
    boolean promotionAvailable;
    boolean stalemate;
    boolean gameOver;

    // Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        //// White team ////
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //// Black team  ////
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    private void copyPieces(List<Piece> source, List<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    @Override
    public void run() {
        // Game loop
        double drawInterval = 1_000_000_000 / (double) FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (nonNull(gameThread)) {
            long now = System.nanoTime();
            delta += (now - lastTime) / drawInterval;
            lastTime = now;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (promotionAvailable) {
            promoting();
        } else if (!gameOver) {
            ///// MOUSE BUTTON PRESSED /////
            // -- check if you can pick up a piece
            if (mouse.pressed) {
                if (isNull(activeP)) {
                    activeP = mouseIsEatingAValidPiece(simPieces);
                } else {
                    // already holding a piece, simulate the move
                    simulate();
                }
            }

            ///// MOUSE BUTTON RELEASED /////
            if (!mouse.pressed) {
                if (nonNull(activeP)) {
                    if (validSquare) {
                        updateMovementOperations();
                    } else {
                        revertMovementOperations();
                    }
                }
            }
        }
    }

    private void simulate() {
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        // Reset this movement at the start of every loop
        // otherwise the moved rook remains on the same square
        resetCastlingMovement();

        // if a piece is being held, update its position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // Check if piece can be moved
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            // if hitting a piece, remove it from the list
            if (nonNull(activeP.hittingP)) {
                simPieces.remove(activeP.hittingP);
            }
            checkCastlingMovement();
            validSquare = kingLegalMovement(activeP) && !opponentCanCaptureKing();
        }
    }

    private boolean kingLegalMovement(Piece king) {
        // Check King
        if (king.type == PieceType.KING) {
            for (Piece currentPiece : simPieces) {
                boolean enemyIsNotKing = currentPiece != king;
                boolean isEnemy = king.color != currentPiece.color;
                boolean squareIsCoveredByEnemy = currentPiece.canMove(king.col, king.row);

                if (enemyIsNotKing && isEnemy && squareIsCoveredByEnemy) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck() {
        Piece king = getKing(true);
        if (king != null && activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        } else {
            checkingP = null;
        }
        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;
        for (Piece piece : simPieces) {
            boolean colorMatchFound = (opponent == (piece.color != currentColor));
            if (PieceType.KING == piece.type && colorMatchFound) {
                king = piece;
            }
        }
        return king;
    }

    private boolean isStaleMate() {
        int enemyCounter = 0;
        int selfCounter = 0;

        // Count the number of pieces
        for (Piece piece : simPieces) {
            if (piece.color == currentColor) {
                selfCounter++;
            } else {
                enemyCounter++;
            }
        }

        Piece enemyKing = getKing(true);
        Piece selfKing = getKing(false);

        // If only one piece enemy king is left & cant move
        if (enemyCounter == 1 && enemyKing != null && kingCanMove(enemyKing)) {
            return true;
            // or only two kings left
        } else return selfCounter == 1 && selfKing != null && enemyCounter == 1 && enemyKing != null;
    }

    private void updateMovementOperations() {
        copyPieces(simPieces, pieces);
        activeP.checkEnPassant();
        activeP.updatePosition();

        if (castlingP != null) {
            castlingP.updatePosition();
        }

        if (isKingInCheck() && isCheckmate()) {
            gameOver = true;
        } else if (isStaleMate() && !isKingInCheck()) {
            stalemate = true;
        } else {
            if (canPromote()) {
                promotionAvailable = true;
            } else {
                changePlayer();
            }
            activeP = null;
        }
    }

    private boolean isCheckmate() {
        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        } else {
            // You still have a chance
            // check if you can block the atack with your piece

            // check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if (colDiff == 0) {
                // The checking piece is atacking vertically
                if (checkingP.row < king.row) {
                    // The checking piece is above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // The checking piece is below the king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                // The checking piece is attacking horizontally
                if (checkingP.col < king.col) {
                    // The checking piece is to the left
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    // The checking piece is to the right
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // The checking piece is attacking diagonally
                if (checkingP.row < king.row) {
                    // The checking piece is above the king
                    if (checkingP.col < king.col) {
                        // The checking piece is in the upper left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return true;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // The checking piece is in the upper right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // The checking piece is above the king
                    if (checkingP.col < king.col) {
                        // The checking piece is in the lower left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return true;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // The checking piece is in the lower right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else {
                // The checking piece is Knight
            }

        }

        return true;
    }

    private boolean kingCanMove(Piece king) {

        // simulate if there is any square where the king can move to
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
        // Update the king's position for a second
        king.col += colPlus;
        king.row += rowPlus;
        boolean isValidMove = false;

        if (king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP);
            }
            if (kingLegalMovement(king)) {
                isValidMove = true;
            }
        }
        // Reset king's position and restore piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private void revertMovementOperations() {
        copyPieces(pieces, simPieces);
        activeP.resetPosition();
        activeP = null;
    }

    private Piece mouseIsEatingAValidPiece(List<Piece> simPieces) {
        for (Piece piece : simPieces) {
            if (piece.color == currentColor &&
                    piece.col == mouse.x / Board.SQUARE_SIZE &&
                    piece.row == mouse.y / Board.SQUARE_SIZE) {
                return piece;
            }
        }
        return null;
    }

    private void resetCastlingMovement() {
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
    }

    private void checkCastlingMovement() {
        if (castlingP != null) {
            // Targeted Left Castling
            if (castlingP.col == 0) {
                castlingP.col += 3;
                // Targeted Right Castling
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }
            // Update X
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private boolean canPromote() {
        if (activeP.type == PieceType.PAWN) {
            if ((activeP.color == WHITE && activeP.row == 0) || (activeP.color == BLACK && activeP.row == 7)) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case KNIGHT -> simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                        case BISHOP -> simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                        case ROOK -> simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                        case QUEEN -> simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                    }
                    simPieces.remove(activeP);
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotionAvailable = false;
                    changePlayer();
                }
            }
        }
    }

    public void changePlayer() {
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;

        // Reset piece properties of the colour in turn
        for (Piece piece : GamePanel.simPieces) {
            if (piece.color == currentColor) {
                piece.twoStepped = false;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Board
        board.draw(g2);

        // Pieces
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        if (nonNull(activeP)) {
            if (canMove) {
                if (!kingLegalMovement(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.gray);
                } else {
                    g2.setColor(Color.white);
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            // Draw the active piece in the end, so it won't be hidden by the board or the colored square
            activeP.draw(g2);
        }

        // STATUS MESSAGE
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        g2.setColor(Color.white);

        if (promotionAvailable) {
            g2.drawString("Promote to:", 640, 125);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (gameOver) {
                String winner = (currentColor == WHITE) ? "White Wins" : "Black Wins";
                g2.setFont(new Font("Arial", Font.PLAIN, 70));
                g2.setColor(Color.green);
                g2.drawString(winner, 200, 300);
            } else if (stalemate) {
                g2.setFont(new Font("Arial", Font.PLAIN, 70));
                g2.setColor(Color.lightGray);
                g2.drawString("Stalemate", 200, 300);
            } else if (currentColor == WHITE) {
                g2.drawString("White's Turn", 640, 450);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 640, 500);
                    g2.drawString("is in check!", 640, 550);
                }
            } else {
                g2.drawString("Black's Turn", 640, 150);
                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 640, 200);
                    g2.drawString("is in check!", 640, 250);
                }
            }
        }
    }
}
