package com.company;

import javax.swing.*;
import java.awt.*;

public class ColorChanger {
    private JPanel panel = new JPanel();
    private int x;
    private int y;

    public boolean canGetPanel(int x, int y) {
        if(this.x == x && this.y == y)
            return true;
        return false;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void changeSizeOfPanel(int newSize) {
        panel.setBackground(Color.BLACK);
        panel.setBounds(y * newSize + 1, x * newSize + 1, newSize - 3, newSize - 3);
        panel.setOpaque(true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Point: " + x + ", " + y);
        return sb.toString();
    }
}
