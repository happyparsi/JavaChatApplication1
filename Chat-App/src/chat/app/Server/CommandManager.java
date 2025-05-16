package chat.app.Server;

import chat.app.Database.DBManager;
import chat.app.Models.User;
import java.sql.SQLException;

public class CommandManager {

    private final User user;

    public CommandManager(User user) {
        this.user = user;
    }

    public void command_group(String command) throws SQLException {
        if (user.getGroup().getGroupUsers().isEmpty()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "Create a group first for sending messages to the group.");
            return;
        }

        String[] parts = command.split(" ", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "Message cannot be empty. Try again.");
            return;
        }

        String msg = parts[1];
        MessageManager.sendGroupMessage(msg, user);

        System.out.println("(Group " + user.getGroup().getGroupName() + ") " + user.getName() + ": " + msg);
        DBManager.saveGroupMessage(user.getName(), user.getGroup().getGroupName(), msg, Server.getServerTime());
    }

    public void command_createGroup() throws SQLException {
        if (!user.getGroup().getGroupUsers().isEmpty()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "You are already a member of a group.");
            return;
        }

        MessageManager.messageSenderAsServer(user.getWriter(), "Type group name:");

        while (true) {
            String groupName = user.getReader().nextLine().trim();

            if (groupName.isBlank()) {
                MessageManager.messageSenderAsServer(user.getWriter(), "Group name cannot be empty. Try again.");
                continue;
            }

            if (Server.isGroupExist(groupName) != null) {
                MessageManager.messageSenderAsServer(user.getWriter(), "Group name is already in use. Try again.");
                continue;
            }

            user.getGroup().createGroup(groupName);

            MessageManager.messageSenderAsServer(user.getWriter(), "Add members to the group. Type usernames. To finish, type /done.");

            while (true) {
                String member = user.getReader().nextLine().trim();

                if (member.equalsIgnoreCase("/done")) {
                    if (!user.getGroup().getGroupUsers().isEmpty()) {
                        user.getGroup().addUserToGroup(user); // Add self to group
                        Server.addServerGroupList(user.getGroup());

                        MessageManager.messageSenderAsServer(user.getWriter(), "Group created successfully.");
                        break;
                    } else {
                        MessageManager.messageSenderAsServer(user.getWriter(), "You must add at least one member before finishing.");
                    }
                } else if (member.isBlank()) {
                    MessageManager.messageSenderAsServer(user.getWriter(), "User name cannot be empty. Try again.");
                } else {
                    User memberUser = Server.isUserExist(member);
                    if (memberUser != null) {
                        if (!memberUser.equals(user)) {
                            user.getGroup().addUserToGroup(memberUser);
                            MessageManager.messageSenderAsServer(user.getWriter(), member + " added to group.");
                        } else {
                            MessageManager.messageSenderAsServer(user.getWriter(), "You are already a member of the group.");
                        }
                    } else {
                        MessageManager.messageSenderAsServer(user.getWriter(), "User does not exist. Try again.");
                    }
                }
            }
            break;
        }
    }

    public void command_allUser(String command) throws SQLException {
        String[] parts = command.split(" ", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "Message cannot be empty. Try again.");
            return;
        }

        String msg = parts[1];
        MessageManager.sendBroadcastMessage(msg, user);

        for (User recipient : Server.getServerUserList()) {
            if (!recipient.equals(user)) {
                DBManager.savePrivateMessage(user.getName(), recipient.getName(), msg, Server.getServerTime());
            }
        }
    }

    public void command_singleUser() throws SQLException {
        MessageManager.messageSenderAsServer(user.getWriter(), "Type receiver's username:");

        while (true) {
            String receiverName = user.getReader().nextLine().trim();

            if (receiverName.isBlank()) {
                MessageManager.messageSenderAsServer(user.getWriter(), "Username cannot be empty. Try again.");
                continue;
            }

            User receiver = Server.isUserExist(receiverName);
            if (receiver == null) {
                MessageManager.messageSenderAsServer(user.getWriter(), "User does not exist. Try again.");
            } else {
                MessageManager.messageSenderAsServer(user.getWriter(), "Type message to send:");
                while (true) {
                    String message = user.getReader().nextLine();
                    if (message.isBlank()) {
                        MessageManager.messageSenderAsServer(user.getWriter(), "Message cannot be empty. Try again.");
                        continue;
                    }
                    MessageManager.sendPrivateMessage(message, receiver, user);
                    DBManager.savePrivateMessage(user.getName(), receiver.getName(), message, Server.getServerTime());
                    break;
                }
                break;
            }
        }
    }

    public void command_assignName() throws SQLException {
        MessageManager.messageSenderAsServer(user.getWriter(), "Please assign your username:");

        while (true) {
            String name = user.getReader().nextLine().trim();

            if (name.isBlank()) {
                MessageManager.messageSenderAsServer(user.getWriter(), "Name cannot be empty. Try again.");
                continue;
            }

            if (Server.isUserExist(name) != null) {
                MessageManager.messageSenderAsServer(user.getWriter(), "Username already taken. Try another.");
                continue;
            }

            user.setName(name);
            Server.addServerUserList(user);
            DBManager.registerUser(name, ""); // No password for now (can be improved)

            MessageManager.messageSenderAsServer(user.getWriter(), "Username assigned: " + name);
            System.out.println("[SERVER] User assigned name: " + name);
            break;
        }
    }

    public void command_register(String command) throws SQLException {
        // Format: /register username password
        String[] parts = command.split(" ", 3);
        if (parts.length < 3) {
            MessageManager.messageSenderAsServer(user.getWriter(), "REGISTER_FAILED Missing parameters. Use: /register username password");
            return;
        }

        String username = parts[1].trim();
        String password = parts[2].trim();

        if (username.isEmpty() || password.isEmpty()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "REGISTER_FAILED Username or password cannot be empty.");
            return;
        }

        if (Server.isUserExist(username) != null) {
            MessageManager.messageSenderAsServer(user.getWriter(), "REGISTER_FAILED Username already exists.");
            return;
        }

        if (DBManager.registerUser(username, password)) {
            MessageManager.messageSenderAsServer(user.getWriter(), "REGISTER_SUCCESS");
            System.out.println("[SERVER] User registered: " + username);
        } else {
            MessageManager.messageSenderAsServer(user.getWriter(), "REGISTER_FAILED Internal error.");
        }
    }

    public void command_login(String command) throws SQLException {
        // Format: /login username password
        String[] parts = command.split(" ", 3);
        if (parts.length < 3) {
            MessageManager.messageSenderAsServer(user.getWriter(), "LOGIN_FAILED Missing parameters. Use: /login username password");
            return;
        }

        String username = parts[1].trim();
        String password = parts[2].trim();

        if (username.isEmpty() || password.isEmpty()) {
            MessageManager.messageSenderAsServer(user.getWriter(), "LOGIN_FAILED Username or password cannot be empty.");
            return;
        }

        if (DBManager.authenticateUser(username, password)) {
            user.setName(username);
            Server.addServerUserList(user);
            MessageManager.messageSenderAsServer(user.getWriter(), "LOGIN_SUCCESS");
            System.out.println("[SERVER] User logged in: " + username);
        } else {
            MessageManager.messageSenderAsServer(user.getWriter(), "LOGIN_FAILED Invalid username or password.");
        }
    }

    public void command_quit() {
        try {
            MessageManager.messageSenderAsServer(user.getWriter(), "Goodbye!");
            Server.removeFromUserList(user);
            user.getSocket().close();
            System.out.println("[SERVER] User disconnected: " + user.getName());
        } catch (Exception e) {
            System.err.println("[SERVER] Error while quitting: " + e.getMessage());
        }
    }

    public void command_unknown() {
        MessageManager.messageSenderAsServer(user.getWriter(), "Unknown command. Please try again.");
    }


}