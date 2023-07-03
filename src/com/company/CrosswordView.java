package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CrosswordView extends JPanel {
    private Crossword crossword;
    private JLayeredPane layeredPane;
    public int sizeOfCellCount = 0;

    public CrosswordView(Crossword crossword, JLayeredPane layeredPane) {
        this.crossword = crossword;
        this.layeredPane = layeredPane;
        add(crossword);
        crossword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char ch = e.getKeyChar();
                if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 'а' && ch <= 'я') || (ch >= 'А' && ch <= 'Я') || ch == ' ' || ch == 'Ё' || ch == 'ё' || ch == '-') {
                    if(crossword.getSelectedRow() != -1 && crossword.getSelectedColumn() != -1) {
                        if ((char) crossword.getValueAt(crossword.getSelectedRow(), crossword.getSelectedColumn()) != '*') {
                            crossword.setValueAt(ch, crossword.getSelectedRow(), crossword.getSelectedColumn());
                        }
                    }
                }
            }
        });

        crossword.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int buttonValue = e.getButton();

                if (buttonValue == MouseEvent.BUTTON3) {
                    int x = crossword.rowAtPoint(e.getPoint()), y = crossword.columnAtPoint(e.getPoint());
                    if((char)crossword.getValueAt(x, y) != '*') {
                        crossword.setValueAt('*', x, y);
                        crossword.setBlackColorAt(x,y);
                    }
                    else {
                        crossword.setValueAt(' ', x, y);
                        crossword.deleteBlackColorAt(x,y);
                    }
                }
                else if(buttonValue == MouseEvent.BUTTON2) {
                    int newSize = 0;
                    switch (sizeOfCellCount % 3) {
                        case 0:
                            newSize = 30;
                            break;
                        case 1:
                            newSize = 50;
                            break;
                        case 2:
                            newSize = 40;
                            break;
                    }
                    sizeOfCellCount++;
                    crossword.setRowHeight(newSize);
                    crossword.setBounds(new Rectangle(crossword.getRowCount() * crossword.getRowHeight(),crossword.getColumnCount() * crossword.getRowHeight()));
                    Font font = new Font("Times New Roman", Font.BOLD, newSize - 10);
                    crossword.setFont(font);
                    layeredPane.setPreferredSize(new Dimension(crossword.getRowCount() * crossword.getRowHeight(),crossword.getColumnCount() * crossword.getRowHeight()));
                    crossword.changeSizeOfAllRects(newSize);
                }
            }
        });
    }

    public Crossword getCrossword() {
        return crossword;
    }

}
