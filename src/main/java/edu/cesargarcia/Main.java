package edu.cesargarcia;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Simple Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Add GamePanel to this window
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        gp.launchGame();
    }
}