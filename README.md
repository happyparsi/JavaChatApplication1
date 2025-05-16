# Java Chat Application

A real-time chat application built with Java that supports both private messaging and group chats. The application uses a client-server architecture and MySQL database for persistent storage.

## Features

- User authentication (login/registration)
- Private messaging between users
- Group chat functionality
- Real-time message delivery
- Persistent message storage
- Modern graphical user interface

## Project Structure

```
Chat-App/
├── src/
│   └── chat/
│       └── app/
│           ├── Server/     # Server-side components
│           ├── Client/     # Client-side implementation
│           ├── Database/   # Database management
│           ├── Models/     # Data models
│           └── Views/      # UI components
├── bin/                    # Compiled classes
└── lib/                    # External dependencies
```

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL JDBC Driver
- Network connectivity for client-server communication

## Database Setup

1. Install MySQL Server
2. Run the SQL scripts from `Database.txt` to:
   - Create the database (`chatappdb`)
   - Set up required tables
   - Create necessary indexes
   - Insert sample data (optional)

### Database Schema

- `userlist`: Stores user accounts and credentials
- `groups`: Manages chat groups
- `group_members`: Tracks group membership
- `messagelist`: Stores all messages (private and group)

## Building the Application

1. Create the `lib` directory and add required dependencies:
   ```bash
   mkdir lib
   # Add MySQL JDBC driver to lib directory
   ```

2. Compile the application:
   ```bash
   javac -d bin -cp "lib/*" src/chat/app/**/*.java
   ```

## Running the Application

1. Start the server:
   ```bash
   java -cp "bin;lib/*" chat.app.Server.Server
   ```

2. Launch the client:
   ```bash
   java -cp "bin;lib/*" chat.app.Client.Client
   ```

## Architecture

### Server Components
- `Server`: Main server class handling client connections
- `ClientHandler`: Manages individual client sessions
- `CommandManager`: Processes client commands
- `MessageManager`: Handles message routing
- `ServerRouter`: Routes messages between clients

### Client Components
- Client GUI for user interaction
- Message handling and display
- Real-time updates
- Connection management

### Database Components
- Connection pooling
- CRUD operations
- Transaction management
- Data persistence

## Security Features

- Password protection for user accounts
- Secure client-server communication
- Protection against SQL injection
- Input validation

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Troubleshooting

### Common Issues

1. Database Connection Error:
   - Verify MySQL server is running
   - Check database credentials
   - Ensure MySQL JDBC driver is in classpath

2. Compilation Errors:
   - Verify JDK installation
   - Check CLASSPATH settings
   - Ensure all dependencies are present

3. Runtime Errors:
   - Check server status
   - Verify network connectivity
   - Review log files for errors

## Support

For support, please:
1. Check the troubleshooting guide
2. Review existing issues
3. Create a new issue with detailed information about your problem

## Authors

- [Your Name] - Initial work and maintenance
