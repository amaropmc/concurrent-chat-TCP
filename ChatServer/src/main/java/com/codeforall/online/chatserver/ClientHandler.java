package com.codeforall.online.chatserver;

import com.codeforall.online.chatserver.util.Commands;
import com.codeforall.online.chatserver.util.CommunicationHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles individual client connections and processes messages.
 */
public class ClientHandler implements Runnable {

    private String name;
    private final Socket client;
    private final CommunicationHandler communicationHandler;
    private final ChatServer chatServer;
    private final Logger logger;

    public ClientHandler(ChatServer chatServer, Socket client, Logger logger) {

        this.client = client;
        this.chatServer = chatServer;
        this.communicationHandler = new CommunicationHandler(client);
        this.logger = logger;
    }

    @Override
    public void run() {

        setName();

        Thread.currentThread().setName(this.name);

        while (this.client.isBound() && !this.client.isClosed()) {

            try {
                readClientMessage();

            } catch (IOException e) {
                logger.log(Level.SEVERE, this.name + " has left the chat");
                this.chatServer.removeClient(this.name);
                break;
            }
        }
    }

    /**
     * Reads and processes messages from the client.
     */
    private void readClientMessage() throws IOException {

            String messageContent = this.communicationHandler.readMessage();

            if (messageContent == null) {
                return;
            }

            if(messageContent.startsWith("/")) {
                processCommand(messageContent);
            } else {
                chatServer.broadcastMessage(this.name + ": " + messageContent);
            }
    }

    private void processCommand(String messageContent) throws IOException {

        String[] command = messageContent.split(" ", 2);

        switch (command[0].toLowerCase()) {
            case "/quit":
                handleQuitCommand();
                break;
            case "/list":
                handleListCommand();
                break;
            case "/whisper":
                handleWhisperCommand(command.length > 1 ? command[1] : null);
                break;
            case "/name":
                handleChangeNameCommand(command.length > 1 ? command[1] : null);
                break;
            case "/help":
                this.communicationHandler.writeMessage("\n*** COMMANDS ***\n");

                for(Commands c : Commands.values()) {
                    this.communicationHandler.writeMessage(c.toString() + "\n");
                }
                break;
            default:
                this.communicationHandler.writeMessage("Invalid command. Type /help for list of available commands.\n");
                break;
        }
    }

    /**
     * Handles the /quit command, allowing the client to leave the chat.
     */
    private void handleQuitCommand() throws IOException {

        chatServer.broadcastMessage(this.name + " has left the server");

        logger.log(Level.INFO, "Client " + (this.chatServer.getClientNames().indexOf(this.name)+1) + " ("+ this.name +")"+ " has left the chat");

        this.chatServer.removeClient(this.name);
        this.client.close();

        this.communicationHandler.endConnection();
    }

    /**
     * Handles the /list command, listing all connected clients.
     */
    private void handleListCommand() throws IOException {

        List<String> clients = chatServer.getClientNames();
        this.communicationHandler.writeMessage("Connected clients: " + String.join(", ", clients) + "\n");
    }

    /**
     * Handles the /whisper command, sending a private message to a specific user.
     */
    private void handleWhisperCommand(String command) throws IOException {

        if (command != null && command.contains(" ")) {
            String recipient = command.split(" ")[0];
            String privateMessage = command.substring(recipient.length()).trim();
            chatServer.sendPrivateMessage(recipient, privateMessage, this.name);

        } else {
            this.communicationHandler.writeMessage("Invalid whisper command. Use /whisper <username> <message>\n");
        }
    }

    /**
     * Handles the /name command, allowing the client to change their displayed name.
     */
    private void handleChangeNameCommand(String newName) throws IOException {

        if (newName != null && !newName.isEmpty()) {
            String oldName = this.name;
            this.name = newName;
            chatServer.broadcastMessage(oldName + " changed their name to " + newName);

        } else {
            this.communicationHandler.writeMessage("Invalid name. Please provide a new name.\n");
        }
    }

    public String getName() {

        return this.name;
    }

    /**
     * Prompts the user to provide a username.
     */
    private void setName() {

        try {
            this.communicationHandler.writeMessage("Please enter your username:\n");
            String userInput = this.communicationHandler.readMessage();

            if (userInput == null || userInput.isEmpty()) {
                this.communicationHandler.writeMessage("Invalid username! Please try again.\n");
                setName();
            } else {
                this.name = userInput;
                this.chatServer.broadcastMessage(this.name + " has now joined the chat!");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public CommunicationHandler getCommunicationHandler() {

        return this.communicationHandler;
    }
}

