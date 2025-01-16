package edu.cesargarcia;

import java.awt.*;

public class Board {

    public final static int MAX_COL = 8;
    public final static int MAX_ROW = 8;
    private final static Color BLACK_SQUARE_COLOR = new Color(210, 165, 125);
    private final static Color WHITE_SQUARE_COLOR = new Color(175, 115, 70);
    // Board size = 800 x 800
    public static final int SQUARE_SIZE = 75;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;


    public void draw(Graphics2D g2) {
        boolean blackSquare = false;

        for (int row = 0; row < MAX_ROW; row++) {
            blackSquare = !blackSquare;

            for (int col = 0; col < MAX_COL; col++) {

                g2.setColor(blackSquare ? BLACK_SQUARE_COLOR : WHITE_SQUARE_COLOR);
                blackSquare = !blackSquare;

                g2.fillRect(col * SQUARE_SIZE,
                        row * SQUARE_SIZE,
                        SQUARE_SIZE,
                        SQUARE_SIZE
                );
            }
        }
    }

}
