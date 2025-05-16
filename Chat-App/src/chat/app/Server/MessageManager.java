package chat.app.Server;

import chat.app.Models.User;
import java.io.PrintWriter;

public class MessageManager {

    /**
     * Formats messages sent by the server with a timestamp and "[Server]" tag.
     */
    public static String serverResponseFormatter(String message) {
        return " [" + Server.getServerTime() + " Server]  " + message;
    }

    /**
     * Formats messages sent by clients with a timestamp and sender's name.
     */
    public static String clientMessageFormatter(String message, String senderName) {
        return " [" + Server.getServerTime() + " " + senderName + "]  " + message;
    }

    /**
     * Sends a server message to the given PrintWriter.
     */
    public static void messageSenderAsServer(PrintWriter writer, String message) {
        writer.println(serverResponseFormatter(message));
        writer.flush();
    }

    /**
     * Sends a client message to the given PrintWriter.
     */
    public static void messageSenderAsClient(PrintWriter writer, String message) {
        writer.println(message);
        writer.flush();
    }

    /**
     * Sends a message to all users in the same group as the sender.
     */
    public static void sendGroupMessage(String message, User sender) {
        String finalMessage = clientMessageFormatter(message, "(Group " + sender.getGroup().getGroupName() + ") " + sender.getName());

        for (User person : sender.getGroup().getGroupUsers()) {
            PrintWriter pw = person.getWriter();
            pw.println(finalMessage);
            pw.flush();
        }
    }

    /**
     * Broadcasts a message to all connected users.
     */
    public static void sendBroadcastMessage(String message, User sender) {
        String finalMessage = clientMessageFormatter(message, sender.getName());

        for (User person : Server.getServerUserList()) {
            PrintWriter pw = person.getWriter();
            pw.println(finalMessage);
            pw.flush();
        }
    }

    /**
     * Sends a private message from sender to receiver.
     */
    public static void sendPrivateMessage(String message, User receiver, User sender) {
        String finalMessage = clientMessageFormatter(message, sender.getName());
        PrintWriter pw = receiver.getWriter();
        pw.println(finalMessage);
        pw.flush();
    }

    /**
     * Sends a loaded (historical) message to a user with given timestamp and sender name.
     */
    public static void sendLoadedMessages(String message, User receiver, String sender, String messageTime) {
        String finalMessage = " [" + messageTime + " " + sender + "]  " + message;
        PrintWriter pw = receiver.getWriter();
        pw.println(finalMessage);
        pw.flush();
    }
}
