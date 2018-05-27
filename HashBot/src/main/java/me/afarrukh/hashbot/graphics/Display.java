package me.afarrukh.hashbot.graphics;

import javax.swing.*;
import java.awt.*;

public class Display {
    private JFrame frame;
    private Canvas canvas; //This is the main object on which we will draw our game images

    private String title;
    private int width, height;

    public Display(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        createDisplay();
    }

    private void createDisplay() {
        frame = new JFrame(title);

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));

        canvas.setFocusable(false);

        frame.add(canvas);
        frame.pack();
    }

    public Canvas getCanvas() { //Standard getter method to return our canvas
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}