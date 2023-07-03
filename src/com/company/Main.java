package com.company;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {

    public Main(String title) {
        //super(title);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width/2 - 500,dimension.height/2 - 360,1160,720);
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        Container c = getContentPane();
        setLayout(new FlowLayout());
        JLayeredPane layeredPane = new JLayeredPane();
        setIconImage(new ImageIcon("icon.png").getImage());

        int size = 15;
        CrosswordView cw = new CrosswordView(new Crossword(size, layeredPane), layeredPane);
        cw.getCrossword().setBounds(new Rectangle(size * cw.getCrossword().getRowHeight(),size * cw.getCrossword().getRowHeight()));
        layeredPane.setPreferredSize(new Dimension(size * cw.getCrossword().getRowHeight(),size * cw.getCrossword().getRowHeight()));
        layeredPane.add(cw.getCrossword(), 0);

        JScrollPane pane = new JScrollPane(layeredPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(603,603));
        c.add(pane);

        Font font = new Font("Times New Roman", Font.BOLD, 30 );
        cw.getCrossword().setFont(font);

        ButtonView bw = new ButtonView(cw.getCrossword(),layeredPane);
        c.add(bw);
        c.setVisible(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main("Crossword Editor");
    }
}