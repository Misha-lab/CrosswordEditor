package com.company;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Crossword extends JTable {
    private static ArrayList<ArrayList<String>> dictionary = new ArrayList<>();
    private static ArrayList<String> allWords = new ArrayList<>();
    private final ArrayList<ColorChanger> colorChangers = new ArrayList<>();

    private JLayeredPane layeredPane;

    private static int dictionaryCounter = 0;
    private static int colorCounter = 0;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int ANYWAY = 2;

    public Crossword(int size, JLayeredPane layeredPane) {
        super(size,size);
        this.layeredPane = layeredPane;
        setRowHeight(40);
        setPreferredSize(new Dimension(size * getRowHeight(),size * getRowHeight()));
        setTableHeader(null);
        setGridColor(Color.black);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionBackground(Color.red);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        for(int i = 0; i < getColumnCount(); i++)
            getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                setValueAt(' ',i,j);
            }
        }
    }
    public boolean isCellEditable(int row, int column) {
        return false;
    }


    public void createTemplate(JLayeredPane layeredPane) {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                deleteBlackColorAt(i,j);
            }
        }
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (Math.random() > 0.7) {
                    setValueAt('*', i, j);
                    setBlackColorAt(i, j);
                }
                else {
                    setValueAt(' ', i, j);
                    deleteBlackColorAt(i, j);
                }
            }
        }
    }

    public void fillCrossword(String dictionaryFilename) {
        int wordSize = 0;
        int runnerHor = 0, runnerVert;
        int fails = 0;

        String word = "";
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                runnerHor = i;
                runnerVert = j;
                String request = "";
                if(canBeginWordAt(i, j, Crossword.HORIZONTAL)) {
                    while (runnerVert < getColumnCount()) {
                        if (!canSetLetter(i, runnerVert, Crossword.HORIZONTAL)) {
                            break;
                        }
                        else {
                            if(isLetterAt(i,runnerVert)) {
                                request = request + (runnerVert - j) + " " + (char)getValueAt(i,runnerVert) + " ";
                            }
                            wordSize++;
                            runnerVert++;
                        }
                    }
                    if (wordSize > 2) {
                        //System.out.println(request);
                        word = wordSearch(i,j, wordSize,request, Crossword.HORIZONTAL, dictionaryFilename);
                        runnerVert = j;
                        for (int k = 0; k < word.length(); k++) {
                            if(isLetterAt(i,runnerVert)) {
                                if((char)getValueAt(i,runnerVert) != word.charAt(k)) {
                                    fails++;
                                }
                            }
                            setValueAt(word.charAt(k), i, runnerVert);
                            runnerVert++;
                        }
                    }
                }

                wordSize = 0;
                request = "";

                if(canBeginWordAt(i, j, Crossword.VERTICAL)) {
                    while (runnerHor < getRowCount()) {
                        if (!canSetLetter(runnerHor, j, Crossword.VERTICAL))
                            break;
                        else {
                            if(isLetterAt(runnerHor,j)) {
                                request = request + (runnerHor - i) + " " + (char)getValueAt(runnerHor,j) + " ";
                            }
                            wordSize++;
                            runnerHor++;
                        }
                    }

                    if (wordSize > 2) {
                        //System.out.println(request);
                        word = wordSearch(i,j, wordSize, request, Crossword.VERTICAL, dictionaryFilename);
                        runnerHor = i;
                        for (int k = 0; k < word.length(); k++) {
                            if(isLetterAt(runnerHor,j)) {
                                if((char)getValueAt(runnerHor,j) != word.charAt(k)) {
                                    fails++;
                                }
                            }
                            setValueAt(word.charAt(k), runnerHor, j);
                            runnerHor++;
                        }
                    }
                }
                wordSize = 0;
                request = "";
            }
        }
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if((char)getValueAt(i,j) == ' ') {
                    setValueAt('*', i,j);
                    setBlackColorAt(i,j);
                }
            }
        }
        System.out.println(fails);
        dictionaryCounter = 0;
    }

    public String wordSearch(int rowOfBegin, int columnOfBegin, int length, String request, int direction, String dictionaryFilename) {
        if(dictionaryCounter == 0) {
            dictionaryCounter++;
            dictionary.clear();
            allWords.clear();
            for (int i = 0; i < 30; i++) {
                dictionary.add(new ArrayList<>());
            }
            try {
                File file = new File(dictionaryFilename);
                Scanner scan = new Scanner(file);
                while (scan.hasNext()) {
                    String temp = scan.next();
                    dictionary.get(temp.length()).add(temp);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(length > 20) {
            length = (int)(Math.random()*20)+4;
        }
        String word = "";
        boolean isFound = false;
        boolean isGoodLength = false;
        int limit = 5;
        if(request.equals("")) {
            while(length > 1 && length < dictionary.size() && !isFound && limit > 0) {
                int rand = (int) (Math.random() * dictionary.get(length).size());
                    if (rand < dictionary.get(length).size()) {
                        if (!allWords.contains(dictionary.get(length).get(rand))) {
                            word = dictionary.get(length).get(rand);
                            allWords.add(word);
                            isFound = true;
                        } else {
                            limit--;
                        }
                    }
                length--;
            }
        }
        else {
            String[] temp = request.split(" ");
            while(!isFound && length > 2) {
                if(direction == Crossword.HORIZONTAL) {
                    if(canEndWordAt(rowOfBegin,columnOfBegin + length - 1, Crossword.HORIZONTAL)) {
                        isGoodLength = true;
                    }
                }
                else if(direction == Crossword.VERTICAL) {
                    if(canEndWordAt(rowOfBegin + length - 1, columnOfBegin, Crossword.VERTICAL)) {
                        isGoodLength = true;
                    }
                }
                if(isGoodLength) {
                    if(length < dictionary.size()) {
                        ArrayList<String> goodWords = new ArrayList<>();
                        for (int i = 0; i < dictionary.get(length).size(); i++) {
                            String wordForTest = dictionary.get(length).get(i);
                            for (int j = 0; j < temp.length; j += 2) {
                                if (temp[j + 1].length() == 1) {
                                    if (Integer.parseInt(temp[j]) < length) {
                                        if (wordForTest.charAt(Integer.parseInt(temp[j])) != temp[j + 1].charAt(0)) {
                                            break;
                                        }
                                    }
                                }
                                if (j == temp.length - 2) {
                                    goodWords.add(wordForTest);
                                }
                            }
                        }
                        if(goodWords.size() > 0) {
                            int rand = (int) (Math.random() * goodWords.size());
                            limit = goodWords.size();
                            while(!isFound && limit > 0) {
                                if (rand < goodWords.size()) {
                                    if(!allWords.contains(goodWords.get(rand))) {
                                        word = goodWords.get(rand);
                                        allWords.add(word);
                                        isFound = true;
                                    }
                                    else {
                                        limit--;
                                        rand = (int) (Math.random() * goodWords.size());
                                    }
                                }
                            }
                        }
                    }
                }
                isGoodLength = false;
                length--;
            }
        }
        return word;
    }

    public boolean canSetLetter(int row, int column, int direction) {
        boolean flag = true;
        if((char)getValueAt(row, column) == '*') {
            flag = false;
        }

        if(direction == Crossword.HORIZONTAL) {
            if(isInWord(row - 1, column, Crossword.HORIZONTAL) || isInWord( row + 1, column, Crossword.HORIZONTAL)) {
                flag = false;
            }

            if(!isLetterAt(row,column)) {
                if(isLetterAt(row - 1, column)) {
                    flag = false;
                }
            }
        }

        if(direction == Crossword.VERTICAL) {
            if(isInWord(row, column - 1, Crossword.VERTICAL) || isInWord(row, column + 1, Crossword.VERTICAL)) {
                flag = false;
            }
            if(!isLetterAt(row,column)) {
                if(isLetterAt(row, column - 1)) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    public boolean canBeginWordAt(int row, int column, int direction) {
        boolean flag = true;
        if((char)getValueAt(row,column) == '*')
            return false;
        if(direction == Crossword.HORIZONTAL) {
            if(!isInWord(row, column, Crossword.HORIZONTAL)) {
                if (isLetterAt(row - 1, column + 1) && isLetterAt(row + 1, column + 1)) {
                    flag = false;
                }
            }
            if(isLetterAt(row, column - 1)) {
                flag = false;
            }
        }
        else if(direction == Crossword.VERTICAL) {
            if(!isInWord(row, column, Crossword.VERTICAL)) {
                if (isLetterAt(row + 1, column - 1) && isLetterAt(row + 1, column + 1)) {
                    flag = false;
                }
            }
            if(isLetterAt(row - 1, column))
                flag = false;
        }

        return flag;
    }

    public boolean canEndWordAt(int row, int column, int direction) {
        boolean flag = true;
        if((char)getValueAt(row,column) == '*')
            return false;

        if(direction == Crossword.HORIZONTAL) {
            if(isLetterAt(row, column + 1)) {
                flag = false;
            }
        }
        else if(direction == Crossword.VERTICAL) {
            if(isLetterAt(row + 1, column))
                flag = false;
        }
        return flag;
    }

    public boolean isInWord(int row, int column, int direction) {
        boolean flag = false;
        if(!isLetterAt(row, column))
            return false;
        else {
            if (direction == Crossword.HORIZONTAL || direction == Crossword.ANYWAY) {
                if (isLetterAt(row, column - 1)) {
                    flag = true;
                }
                if(isLetterAt(row, column + 1)) {
                    flag = true;
                }
            } else if (direction == Crossword.VERTICAL || direction == Crossword.ANYWAY) {
                if (isLetterAt(row - 1, column)) {
                    flag = true;
                }
                if(isLetterAt(row + 1, column)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public boolean isLetterAt(int row, int column) {
        if(row >= 0 && row < getRowCount() && column >= 0 && column < getColumnCount()) {
            char ch = (char)getValueAt(row, column);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 'а' && ch <= 'я') || (ch >= 'А' && ch <= 'Я') || ch == 'Ё' || ch == 'ё' || ch == '-') {
                return true;
            }
        }
        return false;
    }

    public void loadFromTextFile(String filename) {
        int curRow = 1;
        try {
            File file = new File(filename);
            if (!file.exists())
                return;
            Scanner scan = new Scanner(file);
            if(scan.hasNextLine()) {
                String first = scan.nextLine();
                DefaultTableModel model = (DefaultTableModel)getModel();
                model.setRowCount(first.length());
                model.setColumnCount(first.length());
                setModel(model);
                setBounds(new Rectangle(first.length() * getRowHeight(),first.length() * getRowHeight()));
                layeredPane.setPreferredSize(new Dimension(first.length() * getRowHeight(),first.length() * getRowHeight()));
                for (int i = 0; i < getRowCount(); i++) {
                    for (int j = 0; j < getColumnCount(); j++) {
                            setValueAt(' ',i,j);
                            deleteBlackColorAt(i,j);
                    }
                }
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment( JLabel.CENTER );
                for(int i = 0; i < getColumnCount(); i++){
                    getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
                }
                for (int i = 0; i < getColumnCount(); i++) {
                    setValueAt(first.charAt(i), 0, i);
                    if(first.charAt(i) == '*') {
                        setBlackColorAt(0, i);
                    }
                }
            }
            while (scan.hasNextLine()) {
                String temp = scan.nextLine();
                for (int i = 0; i < getColumnCount(); i++) {
                    setValueAt(temp.charAt(i), curRow,i);
                    if(temp.charAt(i) == '*') {
                        setBlackColorAt(curRow, i);
                    }
                }
                curRow++;
            }
            scan.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToTextFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists())
                file.createNewFile();
            FileWriter w = new FileWriter(file);
            w.write(toString());
            w.flush();
            w.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if((char)getValueAt(i,j) == ' ')
                    sb.append('*');
                else
                    sb.append((char) getValueAt(i, j));
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public void setBlackColorAt(int row, int column) {
        ColorChanger changer = new ColorChanger();
        changer.getPanel().setBackground(Color.BLACK);
        changer.setX(row);
        changer.setY(column);
        changer.getPanel().setBounds(column * getRowHeight() + 1, row * getRowHeight() + 1, getRowHeight() - 3, getRowHeight() - 3);
        changer.getPanel().setOpaque(true);
        colorChangers.add(changer);
        layeredPane.add(colorChangers.get(colorChangers.size() - 1).getPanel(), new Integer(++colorCounter));
    }

    public void deleteBlackColorAt(int row, int column) {
        for (int i = 0; i < colorChangers.size(); i++) {
            if(colorChangers.get(i).canGetPanel(row, column)) {
                layeredPane.remove(colorChangers.get(i).getPanel());
            }
        }
    }

    public void changeSizeOfAllRects(int newSize) {
        for (int i = 0; i < colorChangers.size(); i++)
            colorChangers.get(i).changeSizeOfPanel(newSize);
    }
}