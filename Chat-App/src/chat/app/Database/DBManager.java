package chat.app.Database;

import chat.app.Models.Group;
import chat.app.Models.User;
import chat.app.Server.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static Connection con;
    
    public static void ConnectDB() {
        //Connect database
        try{  
                Class.forName("com.mysql.jdbc.Driver");  
                con = DriverManager.getConnection(  
                "jdbc:mysql://127.0.0.1:3306/chatappdb","root","rawal117");  
                 System.out.println("Database connection success");
                } catch(ClassNotFoundException | SQLException e) { System.out.println("Database connection error: " + e.getMessage());}  
         } 

    public static boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM userlist WHERE username = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        
        if (result.next()) {
            String storedPassword = result.getString("password");
            return storedPassword.equals(password); // In production, use proper password hashing
        }
        return false;
    }
    
    public static boolean registerUser(String username, String password) throws SQLException {
        if (isUserExist(username)) {
            return false;
        }
        
        String sql = "INSERT INTO userlist (username, password) VALUES (?, ?)";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password); // In production, use proper password hashing
        statement.executeUpdate();
        return true;
    }

    public static List<String> getUsers() throws SQLException {
        String sql = "SELECT username FROM userlist";
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery(sql);
        
        List<String> users = new ArrayList<>();
        while (result.next()) {
            users.add(result.getString("username"));
        }
        return users;
    }

    public static List<String> getGroups() throws SQLException {
        String sql = "SELECT group_name FROM `groups`";
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery(sql);
        
        List<String> groups = new ArrayList<>();
        while (result.next()) {
            groups.add(result.getString("group_name"));
        }
        return groups;
    }
    
    public static boolean createGroup(String groupName, String createdBy) throws SQLException {
        String sql = "INSERT INTO `groups` (group_name, created_by) VALUES (?, ?)";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, groupName);
        statement.setString(2, createdBy);
        statement.executeUpdate();
        
        // Add creator as first member
        sql = "INSERT INTO group_members (group_id, username) " +
              "SELECT id, ? FROM `groups` WHERE group_name = ?";
        statement = con.prepareStatement(sql);
        statement.setString(1, createdBy);
        statement.setString(2, groupName);
        statement.executeUpdate();
        
        return true;
    }
    
    public static void addUserToGroup(String username, String groupName) throws SQLException {
        String sql = "INSERT INTO group_members (group_id, username) " +
                    "SELECT id, ? FROM `groups` WHERE group_name = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, groupName);
        statement.executeUpdate();
    }

    public static List<String> getPrivateMessages(String sender, String receiver) throws SQLException {
        String sql = "SELECT sender, message, time FROM messagelist " +
                    "WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) " +
                    "AND is_group_message = FALSE " +
                    "ORDER BY created_at";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, sender);
        statement.setString(2, receiver);
        statement.setString(3, receiver);
        statement.setString(4, sender);
        ResultSet result = statement.executeQuery();
        
        List<String> messages = new ArrayList<>();
        while (result.next()) {
            String msgSender = result.getString("sender");
            String message = result.getString("message");
            String time = result.getString("time");
            messages.add(String.format("[%s] %s: %s", time, msgSender, message));
        }
        return messages;
    }
    
    public static List<String> getGroupMessages(String groupName) throws SQLException {
        String sql = "SELECT m.sender, m.message, m.time " +
                    "FROM messagelist m " +
                    "JOIN `groups` g ON m.group_id = g.id " +
                    "WHERE g.group_name = ? AND m.is_group_message = TRUE " +
                    "ORDER BY m.created_at";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, groupName);
        ResultSet result = statement.executeQuery();
        
        List<String> messages = new ArrayList<>();
        while (result.next()) {
            String sender = result.getString("sender");
            String message = result.getString("message");
            String time = result.getString("time");
            messages.add(String.format("[%s] %s: %s", time, sender, message));
        }
        return messages;
    }
    
    public static void savePrivateMessage(String sender, String receiver, String message, String time) 
            throws SQLException {
        String sql = "INSERT INTO messagelist (sender, receiver, message, time, is_group_message) " +
                    "VALUES (?, ?, ?, ?, FALSE)";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, sender);
        statement.setString(2, receiver);
        statement.setString(3, message);
        statement.setString(4, time);
        statement.executeUpdate();
    }
    
    public static void saveGroupMessage(String sender, String groupName, String message, String time) 
            throws SQLException {
        String sql = "INSERT INTO messagelist (sender, receiver, message, time, is_group_message, group_id) " +
                    "SELECT ?, ?, ?, ?, TRUE, id FROM `groups` WHERE group_name = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, sender);
        statement.setString(2, groupName);
        statement.setString(3, message);
        statement.setString(4, time);
        statement.setString(5, groupName);
        statement.executeUpdate();
    }
    
    public static boolean isUserExist(String username) throws SQLException {
        String sql = "SELECT username FROM userlist WHERE username = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        return result.next();
    }
    
    public static boolean isGroupExist(String groupName) throws SQLException {
        String sql = "SELECT group_name FROM `groups` WHERE group_name = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, groupName);
        ResultSet result = statement.executeQuery();
        return result.next();
    }
    
    public static boolean isUserInGroup(String username, String groupName) throws SQLException {
        String sql = "SELECT 1 FROM group_members gm " +
                    "JOIN `groups` g ON gm.group_id = g.id " +
                    "WHERE g.group_name = ? AND gm.username = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, groupName);
        statement.setString(2, username);
        ResultSet result = statement.executeQuery();
        return result.next();
    }
}

