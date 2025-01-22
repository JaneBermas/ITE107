import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Calculator extends JFrame implements ActionListener {
    
    //===================Text Displays======================//
    private JTextField textDisplay;
    private JTextArea historyDisplay;
    
    //===================Functionality Variables======================//
    private double input1, input2, resultingValue;
    private String operator;
    private boolean done;
    
    //===================Menu and Action Items======================//
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem computeItem, viewHistoryItem, exitItem;
    
    public Calculator() {
        //===================GUI Main Frame======================//
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30)); // Dark background color
        
        //===================Menu Bar======================//
        menuBar = new JMenuBar();
        menu = new JMenu("Options");
        
        // Compute Menu Item
        computeItem = new JMenuItem("Compute");
        computeItem.addActionListener(this);
        
        // View History Menu Item
        viewHistoryItem = new JMenuItem("View History");
        viewHistoryItem.addActionListener(this);
        
        // Exit Menu Item
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        
        menu.add(computeItem);
        menu.add(viewHistoryItem);
        menu.addSeparator();
        menu.add(exitItem);
        
        menuBar.add(menu);
        setJMenuBar(menuBar);
        
        //===================GUI Upper Text Display======================//
        textDisplay = new JTextField();
        textDisplay.setEditable(false);
        textDisplay.setFont(new Font("Arial", Font.BOLD, 30));
        textDisplay.setHorizontalAlignment(JTextField.RIGHT);
        textDisplay.setBackground(new Color(50, 50, 50)); // Darker background
        textDisplay.setForeground(Color.white);
        textDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(textDisplay, BorderLayout.NORTH);
        
        //===================GUI Buttons ======================//
        JPanel buttonGroup = new JPanel();
        buttonGroup.setBackground(new Color(30, 30, 30));
        buttonGroup.setLayout(new GridLayout(5, 4, 10, 10)); // Spacing between buttons
        buttonGroup.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
        
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C"
        };
        
        for (String button : buttons) {
            JButton b = new JButton(button);
            b.setFont(new Font("Arial", Font.BOLD, 24));
            b.setBackground(new Color(70, 70, 70)); // Dark gray background
            b.setForeground(Color.white);
            b.setFocusPainted(false); // Remove focus border
            b.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 5));
            b.setPreferredSize(new Dimension(80, 80));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
            b.addActionListener(this);
            buttonGroup.add(b);
        }
        
        add(buttonGroup, BorderLayout.CENTER);
        
        //===================GUI Left History Text Area======================//
        JPanel historyPanel = new JPanel();
        historyPanel.setBackground(new Color(30, 30, 30));
        historyPanel.setLayout(new BorderLayout());
        
        historyDisplay = new JTextArea(10, 30);
        historyDisplay.setEditable(false);
        historyDisplay.setFont(new Font("Courier New", Font.PLAIN, 14));
        historyDisplay.setBackground(new Color(50, 50, 50)); // Darker background for the history
        historyDisplay.setForeground(Color.white);
        historyDisplay.setText("History:\n");
        
        JScrollPane scrollPane = new JScrollPane(historyDisplay);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(historyPanel, BorderLayout.WEST);
        
        //===================GUI Frame Visibility======================//
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String calcuInput = e.getActionCommand();
        
        // Handle Compute action
        if (calcuInput.equals("Compute")) {
            performCalculation();
        }
        // Handle View History action
        else if (calcuInput.equals("View History")) {
            viewHistory();
        }
        // Handle Exit action
        else if (calcuInput.equals("Exit")) {
            System.exit(0);
        }
        // Handle button actions
        else {
            handleButtonInput(calcuInput);
        }
    }
    
    public void performCalculation() {
        // Display the calculation UI to enter numbers and operators
        // Same as before: buttons, input, and calculation
        setVisible(true);
    }

    public void viewHistory() {
    // Show the history in a popup dialog
    try {
        String existingHistory = readHistory();
        if (existingHistory.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No previous history found.", "History", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, existingHistory, "History", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading history.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public void handleButtonInput(String calcuInput) {
        if (textDisplay.getText().length() <= 0 && calcuInput.equals("0")) {
            return;
        }

        if (calcuInput.equals("C")) {
            input1 = 0;
            input2 = 0;
            textDisplay.setText("");
        } else if (Character.isDigit(calcuInput.charAt(0)) || calcuInput.equals(".")) {
            if (done) {
                textDisplay.setText("");
                done = false;
            }

            if (calcuInput.equals(".") && textDisplay.getText().contains(".")) {
                return;
            }

            textDisplay.setText(textDisplay.getText() + calcuInput);
        } else if (calcuInput.equals("=")) {
            input2 = Double.parseDouble(textDisplay.getText());
            calculate();
            textDisplay.setText(String.valueOf(resultingValue));

            done = true;

            try {
                String history = historyRecorder(
                        String.valueOf(input1) + " " +
                        operator + " " +
                        String.valueOf(input2) + " = " +
                        String.valueOf(resultingValue)
                );
                historyDisplay.setText("History:\n" + history);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            operator = calcuInput;
            input1 = Double.parseDouble(textDisplay.getText());
            textDisplay.setText("");
            done = false;
        }
    }

    public void calculate() {
        switch (operator) {
            case "+":
                resultingValue = input1 + input2;
                break;
            case "-":
                resultingValue = input1 - input2;
                break;
            case "*":
                resultingValue = input1 * input2;
                break;
            case "/":
                if (input2 == 0) {
                    textDisplay.setText("Error");
                    return;
                } else {
                    resultingValue = input1 / input2;
                }
                break;
        }
    }

    public String historyRecorder(String record) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("calculator_history.txt"));
        
        String line;
        String history = "";
        
        while ((line = reader.readLine()) != null) {
            history += line + System.lineSeparator();
        }
        
        history += record;

        BufferedWriter writer = new BufferedWriter(new FileWriter("calculator_history.txt"));
        writer.write(history);

        reader.close();
        writer.close();
        
        return history;
    }

    public String readHistory() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("calculator_history.txt"));
        
        String line;
        StringBuilder history = new StringBuilder();
        
        while ((line = reader.readLine()) != null) {
            history.append(line).append(System.lineSeparator());
        }

        reader.close();
        return history.toString();
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("calculator_history.txt"));
        writer.write("");  // Clear the history file on start
        writer.close();
        
        new Calculator();
    }
}
