import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PhonebookSystem extends JFrame implements ActionListener {
    
    private JTextField eventField, nameField, phoneField, groupField;
    private JTextArea displayArea;
    private String filename = "phonebook.txt", groupFile = "groups.txt", selectedContact = "";
    private Set<String> groups;
    
    public PhonebookSystem() {
        setTitle("Phonebook System - Dark Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());
        
        Color bgColor = new Color(45, 45, 45);
        Color fgColor = Color.WHITE;
        Color buttonColor = new Color(70, 70, 70);
        
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Consolas", Font.PLAIN, 12);
        Font buttonFont = new Font("Verdana", Font.BOLD, 12);
        
        eventField = new JTextField();
        eventField.setEditable(false);
        eventField.setBackground(bgColor);
        eventField.setForeground(fgColor);
        eventField.setFont(fieldFont);
        
        nameField = new JTextField();
        nameField.setBackground(bgColor);
        nameField.setForeground(fgColor);
        nameField.setCaretColor(fgColor);
        nameField.setFont(fieldFont);
        
        phoneField = new JTextField();
        phoneField.setBackground(bgColor);
        phoneField.setForeground(fgColor);
        phoneField.setCaretColor(fgColor);
        phoneField.setFont(fieldFont);
        
        groupField = new JTextField();
        groupField.setBackground(bgColor);
        groupField.setForeground(fgColor);
        groupField.setCaretColor(fgColor);
        groupField.setFont(fieldFont);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(bgColor);
        displayArea.setForeground(fgColor);
        displayArea.setCaretColor(fgColor);
        displayArea.setFont(fieldFont);
        
        JPanel topPanels = new JPanel(new BorderLayout());
        topPanels.setBackground(bgColor);
        add(topPanels, BorderLayout.NORTH);
        
        // Event text field at the top for showing actions
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgColor);
        headerPanel.add(eventField);
        topPanels.add(headerPanel, BorderLayout.NORTH);
        
        // Input fields for name, phone, and group
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.setBackground(bgColor);
        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(fgColor);
        nameLabel.setFont(labelFont);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(fgColor);
        phoneLabel.setFont(labelFont);
        inputPanel.add(phoneLabel);
        inputPanel.add(phoneField);
        
        JLabel groupLabel = new JLabel("Group:");
        groupLabel.setForeground(fgColor);
        groupLabel.setFont(labelFont);
        inputPanel.add(groupLabel);
        inputPanel.add(groupField);
        
        topPanels.add(inputPanel, BorderLayout.SOUTH);
        
        // Display the contacts
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.getViewport().setBackground(bgColor);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons for actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        String[] buttons = {"Add Contact", "Search", "Delete Contact", "Select Contact", "Update", "Create Group", "Delete Group", "Add to Group", "Remove from Group"};
        for (String button : buttons) {
            JButton b = new JButton(button);
            b.setBackground(buttonColor);
            b.setForeground(fgColor);
            b.setFont(buttonFont);
            b.setFocusPainted(false);
            b.addActionListener(this);
            buttonPanel.add(b);
        }
        add(buttonPanel, BorderLayout.SOUTH);
        
        groups = new HashSet<>();
        loadContacts();
        loadGroups();
        
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        // Clear event field
        eventField.setText("");
        
        // Data validation
        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || groupField.getText().isEmpty()) {
            eventField.setText("Input field(s) are empty");
            return;
        }
        
        if (phoneField.getText().length() != 11 || !phoneField.getText().startsWith("09")) {
            eventField.setText("Phone number must be 11 digits long starting from 09");
            return;
        }
        
        // Handle commands
        if (command.equals("Add Contact")) {
            addContact(nameField.getText(), phoneField.getText());
        }
        else if (command.equals("Search")) {
            searchContact(nameField.getText(), phoneField.getText());
        }
        else if (command.equals("Delete Contact")) {
            deleteContact(nameField.getText(), phoneField.getText());
        }
        else if (command.equals("Select Contact")) {
            selectContact(nameField.getText(), phoneField.getText());
        }
        else if (command.equals("Update")) {
            updateContact(nameField.getText(), phoneField.getText());
        }
        else if (command.equals("Create Group")) {
            createGroup(groupField.getText());
        }
        else if (command.equals("Delete Group")) {
            deleteGroup(groupField.getText());
        }
        else if (command.equals("Add to Group")) {
            addToGroup(nameField.getText(), phoneField.getText(), groupField.getText());
        }
        else if (command.equals("Remove from Group")) {
            removeFromGroup(nameField.getText(), phoneField.getText(), groupField.getText());
        }
    }
    
    public void loadContacts() {
        String line;
        String contacts = "";
        
        try {
            File file = new File(filename);
            if (!file.exists()) file.createNewFile();
            
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                contacts += line + System.lineSeparator();
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        displayArea.setText(contacts);
    }

    public void loadGroups() {
        try {
            File file = new File(groupFile);
            if (!file.exists()) file.createNewFile();
            
            BufferedReader reader = new BufferedReader(new FileReader(groupFile));
            String line;
            while ((line = reader.readLine()) != null) {
                groups.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addContact(String name, String phone) {
        String contacts = loadFile(filename);
        
        if (contacts.contains(name + ":" + phone)) {
            eventField.setText("Contact already exists");
            return;
        }
        
        contacts += name + ":" + phone + System.lineSeparator();
        writeToFile(filename, contacts);
        eventField.setText("Contact added");
        loadContacts();
    }

    public void searchContact(String name, String phone) {
        String contacts = loadFile(filename);
        
        if (contacts.contains(name + ":" + phone)) {
            eventField.setText("Contact found");
        } else {
            eventField.setText("Contact does not exist");
        }
    }

    public void deleteContact(String name, String phone) {
        String contacts = loadFile(filename);
        if (contacts.contains(name + ":" + phone)) {
            contacts = contacts.replace(name + ":" + phone + System.lineSeparator(), "");
            writeToFile(filename, contacts);
            eventField.setText("Contact deleted");
        } else {
            eventField.setText("Contact does not exist");
        }
        loadContacts();
    }

    public void selectContact(String name, String phone) {
        eventField.setText("Selected contact: " + name + ":" + phone);
    }

    public void updateContact(String name, String phone) {
        String contacts = loadFile(filename);
        contacts = contacts.replace(name + ":" + phone, name + ":" + phone);
        writeToFile(filename, contacts);
        eventField.setText("Contact updated");
        loadContacts();
    }

    public void createGroup(String groupName) {
        if (!groups.contains(groupName)) {
            groups.add(groupName);
            writeGroupsToFile();
            eventField.setText("Group created: " + groupName);
        } else {
            eventField.setText("Group already exists");
        }
    }

    public void deleteGroup(String groupName) {
        if (groups.contains(groupName)) {
            groups.remove(groupName);
            writeGroupsToFile();
            eventField.setText("Group deleted: " + groupName);
        } else {
            eventField.setText("Group does not exist");
        }
    }

    public void addToGroup(String name, String phone, String groupName) {
        // Add the contact to the group file
        String contact = name + ":" + phone + " -> Group: " + groupName;
        String contacts = loadFile(filename);
        if (!contacts.contains(contact)) {
            contacts += contact + System.lineSeparator();
            writeToFile(filename, contacts);
            eventField.setText("Contact added to group: " + groupName);
        } else {
            eventField.setText("Contact is already in this group");
        }
    }

    public void removeFromGroup(String name, String phone, String groupName) {
        String contacts = loadFile(filename);
        String contact = name + ":" + phone + " -> Group: " + groupName;
        
        if (contacts.contains(contact)) {
            contacts = contacts.replace(contact + System.lineSeparator(), "");
            writeToFile(filename, contacts);
            eventField.setText("Contact removed from group: " + groupName);
        } else {
            eventField.setText("Contact is not in this group");
        }
    }

    // Helper methods to load and write to file
    public String loadFile(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void writeToFile(String filename, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGroupsToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(groupFile));
            for (String group : groups) {
                writer.write(group + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PhonebookSystem();
    }
}
