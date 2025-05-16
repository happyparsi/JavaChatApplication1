package chat.app.Client;

import chat.app.Models.User;
import chat.app.Database.DBManager;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientListener listener;
    private String username;
    private boolean isAuthenticated = false;
    
    public Client() throws IOException {
        socket = new Socket("localhost", 12345);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public boolean login(String username, String password) {
        try {
            out.println("LOGIN " + username + " " + password);
            String response = in.readLine();
            isAuthenticated = response.equals("LOGIN_SUCCESS");
            if (isAuthenticated) {
                this.username = username;
                startListener();
            }
            return isAuthenticated;
        } catch (IOException e) {
            return false;
        }
    }
    
    public boolean register(String username, String password) {
        try {
            out.println("REGISTER " + username + " " + password);
            String response = in.readLine();
            return response.equals("REGISTER_SUCCESS");
        } catch (IOException e) {
            return false;
        }
    }
    
    public void sendPrivateMessage(String recipient, String message) {
        if (isAuthenticated) {
            out.println("PRIVATE " + recipient + " " + message);
        }
    }
    
    public void sendGroupMessage(String groupName, String message) {
        if (isAuthenticated) {
            out.println("GROUP " + groupName + " " + message);
        }
    }
    
    public boolean createGroup(String groupName) {
        if (isAuthenticated) {
            out.println("CREATE_GROUP " + groupName);
            try {
                String response = in.readLine();
                return response.equals("GROUP_CREATED");
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
    
    public List<String> getUsers() {
        List<String> users = new ArrayList<>();
        if (isAuthenticated) {
            out.println("GET_USERS");
            try {
                String response = in.readLine();
                if (response.startsWith("USERS ")) {
                    String[] userArray = response.substring(6).split(",");
                    for (String user : userArray) {
                        if (!user.trim().isEmpty()) {
                            users.add(user.trim());
                        }
                    }
                }
            } catch (IOException e) {
                // Handle error
            }
        }
        return users;
    }
    
    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        if (isAuthenticated) {
            out.println("GET_GROUPS");
            try {
                String response = in.readLine();
                if (response.startsWith("GROUPS ")) {
                    String[] groupArray = response.substring(7).split(",");
                    for (String group : groupArray) {
                        if (!group.trim().isEmpty()) {
                            groups.add(group.trim());
                        }
                    }
                }
            } catch (IOException e) {
                // Handle error
            }
        }
        return groups;
    }
    
    public List<String> getPrivateMessages(String otherUser) {
        List<String> messages = new ArrayList<>();
        if (isAuthenticated) {
            out.println("GET_PRIVATE_MESSAGES " + otherUser);
            try {
                String line;
                while (!(line = in.readLine()).equals("END_MESSAGES")) {
                    if (!line.isEmpty()) {
                        messages.add(line);
                    }
                }
            } catch (IOException e) {
                // Handle error
            }
        }
        return messages;
    }
    
    public List<String> getGroupMessages(String groupName) {
        List<String> messages = new ArrayList<>();
        if (isAuthenticated) {
            out.println("GET_GROUP_MESSAGES " + groupName);
            try {
                String line;
                while (!(line = in.readLine()).equals("END_MESSAGES")) {
                    if (!line.isEmpty()) {
                        messages.add(line);
                    }
                }
            } catch (IOException e) {
                // Handle error
            }
        }
        return messages;
    }
    
    private void startListener() {
        listener = new ClientListener(in);
        new Thread(listener).start();
    }
    
    public void disconnect() {
        try {
            if (listener != null) {
                listener.stop();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Handle error
        }
    }
}