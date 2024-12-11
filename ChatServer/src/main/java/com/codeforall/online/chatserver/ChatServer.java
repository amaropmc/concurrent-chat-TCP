package com.codeforall.online.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that functions as a server
 */
public class ChatServer {

    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private static final int PORT = 8888;

    private ServerSocket server;
    private Socket clientSocket;
    private ClientHandler client;
    private List<ClientHandler> clientsList = new ArrayList<>();

    public static void main(String[] args) {

        ChatServer chatServer = new ChatServer();
        chatServer.init();
    }

    public ChatServer() {

        try {
            this.server = new ServerSocket(PORT);
            logger.log(Level.INFO, "Server bind to port " + PORT);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to bind to port: " + PORT);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Server listens for new connections on default port continuously
     */
    public void init() {

        while(true) {

            try {
                this.clientSocket = this.server.accept();
                this.client = new ClientHandler(this, this.clientSocket, this.logger);
                clientsList.add(client);

                Thread clientThread = new Thread(this.client);
                clientThread.start();

                logger.log(Level.INFO, "Client " + clientsList.size() + " established connection.");

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Unable to connect to server");
                logger.log(Level.SEVERE, e.getMessage());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Connection failed");}
        }
    }

    /**
     * Sends a message to all clients linked to the server
     *
     * @param message the message to be sent
     * @throws IOException
     */
    public void broadcastMessage(String message) throws IOException {

        for(ClientHandler client : clientsList) {

            String messageOwner = message.split(":")[0]; //Gets the name of the owner

            if(messageOwner.equals(client.getName())) {
                continue;
            }

            client.getCommunicationHandler().writeMessage(message + "\n");
        }
    }

    /**
     * Sends a private message to another client
     *
     * @param recipientName The username of the receiver client
     * @param message       The content of the message
     * @param senderName    The usarname of the sender client
     * @throws IOException
     */
    public synchronized void sendPrivateMessage(String recipientName, String message, String senderName) throws IOException {

        ClientHandler recipientClient = this.getClientHandlerByName(recipientName);

        if(recipientClient != null) {
            recipientClient.getCommunicationHandler().writeMessage("[Private] " + senderName + ": " + message + "\n");
            return;
        }

        // If the recipient is not found, send a message back to the sender
        ClientHandler senderClient = this.getClientHandlerByName(senderName);
        senderClient.getCommunicationHandler().writeMessage("User '" + recipientName + "' not found.\n");
    }

    /**
     * Disconnects a client from the server
     * @param clientName
     */
    public synchronized void removeClient(String clientName) {

        ClientHandler clientToRemove = this.getClientHandlerByName(clientName);
        clientsList.remove(clientToRemove);
    }

    /**
     * Fetch all the usernames present in the server
     * @return  list of clients' usernames
     */
    public synchronized List<String> getClientNames() {

        List<String> names = new ArrayList<>();

        for (ClientHandler client : clientsList) {
            names.add(client.getName());
        }

        return names;
    }

    /**
     * Finds a specific client in the connected clients list
     * @param clientName
     * @return
     */
    private ClientHandler getClientHandlerByName(String clientName) {

        try {
            return clientsList.stream()
                    .filter(client -> client.getName().equals(clientName))
                    .findFirst().get();
        }

        catch(NoSuchElementException e) {
            return null;
        }

    }
}
