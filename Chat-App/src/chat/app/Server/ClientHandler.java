package chat.app.Server;

import chat.app.Models.User;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final User user;
    private final ServerRouter router;

    public ClientHandler(Socket socket) throws IOException {
        this.user = new User(socket);
        this.router = new ServerRouter(user);
    }

    public ServerRouter getRouter() {
        return router;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        System.out.println(MessageManager.serverResponseFormatter("Connected: " + user.getSocket()));

        try {
            // Assign a name to the client
            router.Route("/assignName");

            System.out.println("[" + user.getSocket() + "] Awaiting client input...");

            // Handle incoming client messages
            while (user.getReader().hasNextLine()) {
                String command = user.getReader().nextLine().trim();
                System.out.println("[" + user.getSocket() + "] Received: " + command);

                boolean keepRunning = router.Route(command);
                if (!keepRunning) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error while handling client: " + user.getSocket());
            e.printStackTrace();

        } finally {
            try {
                user.getSocket().close();
            } catch (IOException closeEx) {
                System.err.println("Failed to close socket: " + closeEx.getMessage());
            }
            System.out.println(MessageManager.serverResponseFormatter("Closed: " + user.getSocket()));
            // Remove user from server lists on disconnect
            Server.removeFromUserList(user);
            if (user.getGroup().getGroupUsers().size() > 0) {
                user.getGroup().removeUserFromGroup(user);
                if (user.getGroup().getGroupUsers().isEmpty()) {
                    Server.removeFromGroupList(user.getGroup().getGroupName());
                }
            }
        }
    }
}
