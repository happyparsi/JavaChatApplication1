package chat.app.Client;

import java.io.BufferedReader;
import java.io.IOException;
import chat.app.Views.ChatUI;

public class ClientListener implements Runnable {
    private final BufferedReader in;
    private volatile boolean running = true;
    private ChatUI chatUI;

    public ClientListener(BufferedReader in) {
        this.in = in;
    }

    public void setChatUI(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    @Override
    public void run() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                if (message.startsWith("MESSAGE ")) {
                    // Format: MESSAGE <from> <content>
                    String[] parts = message.substring(8).split(" ", 2);
                    if (parts.length == 2 && chatUI != null) {
                        chatUI.receiveMessage(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
    }
}
