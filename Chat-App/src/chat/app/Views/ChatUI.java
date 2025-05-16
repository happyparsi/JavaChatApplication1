package chat.app.Views;

import chat.app.Client.Client;
import chat.app.Models.Group;
import chat.app.Models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.ArrayList;

public class ChatUI extends JFrame {
    private final Client client;
    private final String currentUsername;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageField;
    private JList<String> userList;
    private JList<String> groupList;
    private DefaultListModel<String> userListModel;
    private DefaultListModel<String> groupListModel;
    private String selectedRecipient;
    private boolean isGroup;
    
    public ChatUI(Client client, String username) {
        this.client = client;
        this.currentUsername = username;
        
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        createUI();
        refreshLists();
    }
    
    private void createUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Create sidebar
        createSidebar();
        
        // Create chat area
        createChatArea();
        
        // Add components to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(new Color(255, 255, 255));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        
        // User profile section
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setBackground(new Color(66, 133, 244));
        JLabel usernameLabel = new JLabel(currentUsername);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        profilePanel.add(usernameLabel);
        
        // Lists section
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Users list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        styleList(userList);
        JScrollPane userScrollPane = new JScrollPane(userList);
        
        // Groups list
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        styleList(groupList);
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        
        tabbedPane.addTab("Users", new ImageIcon(), userScrollPane);
        tabbedPane.addTab("Groups", new ImageIcon(), groupScrollPane);
        
        // Add selection listeners
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userList.getSelectedValue() != null) {
                selectedRecipient = userList.getSelectedValue();
                isGroup = false;
                updateChatTitle();
                loadChatHistory();
            }
        });
        
        groupList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && groupList.getSelectedValue() != null) {
                selectedRecipient = groupList.getSelectedValue();
                isGroup = true;
                updateChatTitle();
                loadChatHistory();
            }
        });
        
        // Create group button
        JButton createGroupButton = new JButton("Create Group");
        styleButton(createGroupButton);
        createGroupButton.addActionListener(e -> showCreateGroupDialog());
        
        // Add components to sidebar
        sidebarPanel.add(profilePanel, BorderLayout.NORTH);
        sidebarPanel.add(tabbedPane, BorderLayout.CENTER);
        sidebarPanel.add(createGroupButton, BorderLayout.SOUTH);
    }
    
    private void createChatArea() {
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(255, 255, 255));
        
        // Chat title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        JLabel titleLabel = new JLabel("Select a chat to start messaging");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);
        
        // Message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        
        // Message input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton sendButton = new JButton("Send");
        styleButton(sendButton);
        sendButton.setPreferredSize(new Dimension(100, 36));
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        
        // Add components to chat panel
        chatPanel.add(titlePanel, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void styleList(JList<String> list) {
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        list.setFixedCellHeight(50);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(51, 103, 214));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(66, 133, 244));
            }
        });
    }
    
    private void showCreateGroupDialog() {
        JDialog dialog = new JDialog(this, "Create Group", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField groupNameField = new JTextField(20);
        JButton createButton = new JButton("Create");
        styleButton(createButton);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Group Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(groupNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(createButton, gbc);
        
        createButton.addActionListener(e -> {
            String groupName = groupNameField.getText().trim();
            if (!groupName.isEmpty()) {
                createGroup(groupName);
                dialog.dispose();
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void updateChatTitle() {
        String title = selectedRecipient + (isGroup ? " (Group)" : "");
        ((JLabel) ((JPanel) chatPanel.getComponent(0)).getComponent(0)).setText(title);
    }
    
    private void refreshLists() {
        // Get users and groups from server
        List<String> users = client.getUsers();
        List<String> groups = client.getGroups();
        
        userListModel.clear();
        groupListModel.clear();
        
        for (String user : users) {
            if (!user.equals(currentUsername)) {
                userListModel.addElement(user);
            }
        }
        
        for (String group : groups) {
            groupListModel.addElement(group);
        }
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && selectedRecipient != null) {
            try {
                if (isGroup) {
                    client.sendGroupMessage(selectedRecipient, message);
                } else {
                    client.sendPrivateMessage(selectedRecipient, message);
                }
                messageField.setText("");
                loadChatHistory(); // Refresh chat
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error sending message: " + e.getMessage());
            }
        }
    }
    
    private void loadChatHistory() {
        try {
            List<String> messages;
            if (isGroup) {
                messages = client.getGroupMessages(selectedRecipient);
            } else {
                messages = client.getPrivateMessages(selectedRecipient);
            }
            
            messageArea.setText("");
            for (String message : messages) {
                messageArea.append(message + "\n");
            }
            
            // Scroll to bottom
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading messages: " + e.getMessage());
        }
    }
    
    private void createGroup(String groupName) {
        try {
            boolean success = client.createGroup(groupName);
            if (success) {
                refreshLists();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create group");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating group: " + e.getMessage());
        }
    }
    
    public void receiveMessage(String from, String message) {
        if (from.equals(selectedRecipient)) {
            loadChatHistory();
        }
    }
}
