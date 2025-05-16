package chat.app.Server;

import chat.app.Models.Group;
import chat.app.Models.User;
import chat.app.Database.DBManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final List<User> serverUserList = new ArrayList<>();
    private static final List<Group> serverGroupList = new ArrayList<>();

    private static long serverStartTime = System.currentTimeMillis();

    public static void main(String[] args) {
        int port = 12345; // Example port

        // Initialize database connection once here before anything else
        DBManager.ConnectDB();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addServerUserList(User user) {
        if (!serverUserList.contains(user)) {
            serverUserList.add(user);
            System.out.println("User added: " + user.getName());
        }
    }

    public static synchronized void removeFromUserList(User user) {
        serverUserList.remove(user);
        System.out.println("User removed: " + user.getName());
    }

    public static synchronized User isUserExist(String username) {
        return serverUserList.stream()
                .filter(u -> u.getName() != null && u.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public static synchronized void addServerGroupList(Group group) {
        if (!serverGroupList.contains(group)) {
            serverGroupList.add(group);
            System.out.println("Group added: " + group.getGroupName());
        }
    }

    public static synchronized void removeFromGroupList(String groupName) {
        serverGroupList.removeIf(g -> g.getGroupName().equalsIgnoreCase(groupName));
        System.out.println("Group removed: " + groupName);
    }

    public static synchronized Group isGroupExist(String groupName) {
        return serverGroupList.stream()
                .filter(g -> g.getGroupName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElse(null);
    }

    public static String getServerTime() {
        // Return current server time as a string, e.g. formatted date/time
        return java.time.LocalDateTime.now().toString();
    }

    public static synchronized List<User> getServerUserList() {
        return new ArrayList<>(serverUserList);
    }

    public static long getServerStartTime() {
        return serverStartTime;
    }

    public static void setServerStartTime(long serverStartTime) {
        Server.serverStartTime = serverStartTime;
    }
}
