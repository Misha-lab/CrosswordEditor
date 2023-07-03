package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonView extends JPanel {
    private JLayeredPane layeredPane;
    private Crossword crossword;

    private final JButton button1 = new JButton("Очистить кроссворд");
    private final JButton button2 = new JButton("Сгенерировать новую сетку");
    private final JButton button3 = new JButton("Сохранить текущий кроссворд");
    private final JButton button4 = new JButton("Загрузить кроссворд из файла");
    private final JButton button5 = new JButton("Заполнить кроссворд словами из словаря");

    public ButtonView(Crossword crossword, JLayeredPane layeredPane) {
        this.crossword = crossword;
        this.layeredPane = layeredPane;
        setLayout(new GridLayout(5,1));
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(button5);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel pan = new JPanel();
                pan.setLayout(new FlowLayout());
                JLabel sizeInput = new JLabel("Введите размер поля, в клеточках (min: 12, max:200)");

                SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel();
                spinnerNumberModel.setMinimum(12);
                spinnerNumberModel.setMaximum(200);
                spinnerNumberModel.setValue(crossword.getRowCount());
                JSpinner spin = new JSpinner(spinnerNumberModel);

                JCheckBox prevTemplate = new JCheckBox("Оставить прежний шаблон");
                JButton start = new JButton("Создать новое поле");

                pan.add(start);
                pan.add(sizeInput);
                pan.add(spin);
                pan.add(prevTemplate);

                JDialog dialog = new JDialog();
                dialog.setTitle("Настройки поля");
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.add(pan);
                dialog.setBounds(920,450, 400,150);

                start.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DefaultTableModel model = (DefaultTableModel) crossword.getModel();
                        int oldRowCount = crossword.getRowCount();
                        int oldColumnCount = crossword.getColumnCount();
                        model.setRowCount((int) spin.getValue());
                        model.setColumnCount((int) spin.getValue());
                        crossword.setModel(model);
                        crossword.setBounds(new Rectangle(crossword.getRowCount() * crossword.getRowHeight(), crossword.getColumnCount() * crossword.getRowHeight()));
                        layeredPane.setPreferredSize(new Dimension(crossword.getRowCount() * crossword.getRowHeight(), crossword.getColumnCount() * crossword.getRowHeight()));

                        if (crossword.getRowCount() > oldRowCount && crossword.getColumnCount() > oldColumnCount) {
                            for (int i = oldRowCount; i < crossword.getRowCount(); i++) {
                                for (int j = 0; j < crossword.getColumnCount(); j++) {
                                    crossword.setValueAt(' ', i, j);
                                    crossword.setValueAt(' ', j, i);
                                }
                            }
                        } else if (crossword.getRowCount() < oldRowCount && crossword.getColumnCount() < oldColumnCount) {
                            for (int i = crossword.getRowCount(); i < oldRowCount; i++) {
                                for (int j = 0; j < oldColumnCount; j++) {
                                    crossword.deleteBlackColorAt(i, j);
                                    crossword.deleteBlackColorAt(j, i);
                                }
                            }
                        }
                        for (int i = 0; i < crossword.getRowCount(); i++) {
                            for (int j = 0; j < crossword.getColumnCount(); j++) {
                                if (!(prevTemplate.isSelected() && ((char)crossword.getValueAt(i, j) == '*'))) {
                                    crossword.setValueAt(' ', i, j);
                                    crossword.deleteBlackColorAt(i, j);
                                }
                            }
                        }
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int i = 0; i < crossword.getColumnCount(); i++) {
                            crossword.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                        }
                        dialog.setVisible(false);
                    }
                });

                dialog.setVisible(true);
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crossword.createTemplate(layeredPane);
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel pan = new JPanel(new FlowLayout());
                JLabel label = new JLabel("Введите название файла (БЕЗ .txt):");
                JTextField filename = new JTextField();
                filename.setPreferredSize(new Dimension(150,30));
                pan.add(label);
                pan.add(filename);
                JDialog dialog = new JDialog();
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setTitle("Сохранение кроссворда в текстовый файл");
                JButton confirm = new JButton("Подтвердить");
                confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        crossword.saveToTextFile("saved crosswords/" + filename.getText() + ".txt");
                        dialog.setVisible(false);
                    }
                });
                pan.add(confirm);
                dialog.add(pan);

                dialog.setBounds(920,450, 400,150);
                dialog.setVisible(true);
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel pan = new JPanel(new FlowLayout());
                JLabel label = new JLabel("Введите название файла (БЕЗ .txt):");
                JTextField filename = new JTextField();
                filename.setPreferredSize(new Dimension(150,30));
                pan.add(label);
                pan.add(filename);
                JDialog dialog = new JDialog();
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setTitle("Загрузка кроссворда из текстового файла");
                JButton confirm = new JButton("Подтвердить");
                confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        crossword.loadFromTextFile( "saved crosswords/" + filename.getText() + ".txt");
                        dialog.setVisible(false);
                    }
                });
                pan.add(confirm);
                dialog.add(pan);

                dialog.setBounds(920,450, 400,150);
                dialog.setVisible(true);
            }
        });

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel pan = new JPanel(new FlowLayout());
                String[] languages = {"Русский", "Английский"};
                JComboBox choice = new JComboBox(languages);
                JDialog dialog = new JDialog();
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setTitle("Выбор языка");
                pan.add(choice);
                dialog.setBounds(920,450, 300,100);
                JButton confirm = new JButton("Подтвердить");
                pan.add(confirm);
                dialog.add(pan);
                confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String language;
                        language = (String)choice.getSelectedItem();
                        if(language.equals("Английский")) {
                            language = "languages/word_eng.txt";
                        }
                        else {
                            language = "languages/word_rus.txt";
                        }
                        crossword.fillCrossword(language);
                        dialog.setVisible(false);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
