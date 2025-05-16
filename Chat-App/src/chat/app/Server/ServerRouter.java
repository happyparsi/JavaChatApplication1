package chat.app.Server;

import chat.app.Models.User;
import java.sql.SQLException;

public class ServerRouter {

    private CommandManager executer;

    public ServerRouter(User user) {
        this.executer = new CommandManager(user);
        System.out.println(user.getSocket().toString() + " connected.");
    }

    public boolean Route(String command) throws SQLException {
        if (command == null || command.isBlank()) {
            executer.command_unknown();
            return true;
        }

        String trimmedCommand = command.replaceAll(" ", "");

        if (trimmedCommand.equals("/quit")) {
            executer.command_quit();
            return false;
        }
        else if (trimmedCommand.equals("/singleUser")) {
            executer.command_singleUser();
            return true;
        }
        else if (command.startsWith("/group")) {
            executer.command_group(command);
            return true;
        }
        else if (trimmedCommand.equals("/createGroup")) {
            executer.command_createGroup();
            return true;
        }
        else if (command.startsWith("/allUser")) {
            executer.command_allUser(command);
            return true;
        }
        else if (trimmedCommand.equals("/assignName")) {
            executer.command_assignName();
            return true;
        }
        else {
            executer.command_unknown();
            return true;
        }
    }
}
