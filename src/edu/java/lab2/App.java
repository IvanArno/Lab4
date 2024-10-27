package edu.java.lab2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.*;

/**
 * Program for cinema
 * 
 * Author: Ivan_Arno
 * Version: 1.0
 * Since: 2024
 */

public class App {
    private JFrame frame;
    private JToolBar buttonsPanel;
    private JButton save;
    private JButton open;
    private JButton add;
    private JButton edit;
    private JButton delete;
    private JButton info;
    private JButton filter;
    private DefaultTableModel model;
    private JTable films;
    private JComboBox<String> name;
    private JTextField filmName;
    private JPanel filterPanel;
    
    public void show() {
        frame = new JFrame("Список фильмов");
        // Создание панели инструментов
        buttonsPanel = new JToolBar();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        
        // Создание кнопок на панели
        save = new JButton(new ImageIcon("./Icons/save2.png"));
        open = new JButton(new ImageIcon("./Icons/Open.png"));
        add = new JButton(new ImageIcon("./Icons/add.png"));
        edit = new JButton(new ImageIcon("./Icons/edit.png"));
        delete = new JButton(new ImageIcon("./Icons/trash.png"));
        info = new JButton(new ImageIcon("./Icons/info.png"));

        // Размещение кнопок на панели
        buttonsPanel.add(save);
        buttonsPanel.add(open);
        buttonsPanel.add(add);
        buttonsPanel.add(edit);
        buttonsPanel.add(delete);
        buttonsPanel.add(info);
        
        // Размещение панели в окне
        frame.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
        
        // Создание таблицы данных
        String[] columns = { "Фильм", "Жанр", "Сеанс", "Проданные билеты" };
        Object[][] data = {
            {"Дюна", "Научная фантастика", "18:00", "120"},
            {"Темные времена", "Драма", "20:30", "90"}
        };
        model = new DefaultTableModel(data, columns);
        films = new JTable(model);
        
        // Добавление таблицы в окно
        frame.add(BorderLayout.CENTER, new JScrollPane(films));

        // Создание панели поиска
        name = new JComboBox<>(new String[] { "Фильм", "Жанр", "Сеанс" });
        filmName = new JTextField("Название фильма");
        filter = new JButton("Поиск");
        filterPanel = new JPanel();
        filterPanel.add(name);
        filterPanel.add(filmName);
        filterPanel.add(filter);
        frame.add(BorderLayout.SOUTH, filterPanel);
        
        // Отображение окна
        frame.setVisible(true);

        // Обработка событий
        ButtonListener buttonListener = new ButtonListener();
        info.setActionCommand("Информация");
        add.setActionCommand("Добавить");
        delete.setActionCommand("Удалить");
        edit.setActionCommand("Редактировать");
        
        info.addActionListener(buttonListener);
        add.addActionListener(buttonListener);
        delete.addActionListener(buttonListener);
        edit.addActionListener(buttonListener);
        
        filter.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        	try { checkName(filmName);
        	}
        	catch(NullPointerException ex) {
        	JOptionPane.showMessageDialog(frame, ex.toString());
        	}
        	catch(MyException myEx) {
        	JOptionPane.showMessageDialog(null, myEx.getMessage());
        	}}});
        
        filmName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (filmName.getText().equals("Название фильма")) {
                    filmName.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (filmName.getText().isEmpty()) {
                    filmName.setText("Название фильма");
                }
            }
        });
        

    }
    
    // Класс для обработки событий кнопок
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
            case "Редактировать": {
                int selectedRow = films.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите строку для редактирования.");
                    break;
                }

                // Получение текущих значений из выбранной строки
                String currentFilmName = (String) model.getValueAt(selectedRow, 0);
                String currentGenre = (String) model.getValueAt(selectedRow, 1);
                String currentSession = (String) model.getValueAt(selectedRow, 2);
                String currentTickets = (String) model.getValueAt(selectedRow, 3);

                // Создание текстовых полей с текущими значениями
                JTextField filmNameInput = new JTextField(currentFilmName);
                JTextField genreInput = new JTextField(currentGenre);
                JTextField sessionInput = new JTextField(currentSession);
                JTextField ticketsInput = new JTextField(currentTickets);

                Object[] message = {
                    "Название фильма:", filmNameInput,
                    "Жанр:", genreInput,
                    "Сеанс:", sessionInput,
                    "Проданные билеты:", ticketsInput
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Редактировать фильм", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String name = filmNameInput.getText();
                        String genre = genreInput.getText();
                        String session = sessionInput.getText();
                        String tickets = ticketsInput.getText();

                        // Проверка на допустимость значений
                        int ticketsCount;
                        try {
                            ticketsCount = Integer.parseInt(tickets);
                            if (ticketsCount < 0) {
                                throw new InputException("Количество проданных билетов должно быть положительным числом.");
                            }
                        } catch (NumberFormatException n) {
                            throw new InputException("Количество проданных билетов должно быть целым числом.");
                        }
                        
                        if (!session.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                            throw new InputException("Время сеанса должно быть в формате HH:mm.");
                        }

                        // Обновление данных выбранной строки
                        model.setValueAt(name, selectedRow, 0);
                        model.setValueAt(genre, selectedRow, 1);
                        model.setValueAt(session, selectedRow, 2);
                        model.setValueAt(ticketsCount, selectedRow, 3);
                    } catch (InputException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
                break;
            }
                case "Информация":
                    JOptionPane.showMessageDialog(frame, "Тест кнопки");
                    break;
                case "Добавить": {
                    int selectedRow = films.getSelectedRow();
                    if (selectedRow != -1) { // Проверяем, выбрана ли строка
                        model.insertRow(selectedRow + 1, new Object[]{});
                    } else {
                        model.addRow(new Object[]{});
                    }
                    break;
                }
                case "Удалить": {
                    int selectedRow = films.getSelectedRow();
                    try {
	                    if (selectedRow == -1) throw new RowException(); // Проверяем, выбрана ли строка
	                    else model.removeRow(selectedRow);
	                    } catch (RowException ex) {
	                        JOptionPane.showMessageDialog(frame, ex.getMessage());
	                    }
                    break;
                }
            }
        }
    }
    
    private class RowException extends Exception {
    		public RowException() {
		    	super ("Не выбрана строка для удаления. Пожалуйста, выберите строку.");
		    	}
    		}
    	
    private class MyException extends Exception {
	    	public MyException() {
		    	super ("Вы не ввели название фильма для поиска для поиска");
		    	}
	    	}
    	private void checkName (JTextField bName) throws MyException,NullPointerException
    	{
    	String sName = bName.getText();
    	if (sName.contains("Название фильма")) throw new MyException();
    	if (sName.length() == 0) throw new NullPointerException();
    	}
    	
    private class InputException extends Exception {
    	   public InputException(String message) {
    	        super(message);
    	    }
    	}




    public static void main(String[] args) {
        new App().show();
    }
}
