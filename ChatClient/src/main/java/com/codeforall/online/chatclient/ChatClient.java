package com.codeforall.online.chatclient;

import com.codeforall.online.chatclient.communication.MessageReceiver;
import com.codeforall.online.chatclient.communication.MessageWriter;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Chat client
 */
public class ChatClient {

    private final static Logger logger = Logger.getLogger(ChatClient.class.getName());
    private final static int PORT = 8888;

    private Socket socket;

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();

        chatClient.startClient();
    }

    /**
     * Connects the client to the server and opens IO Threads
     */
    public void startClient() {

        try {
            this.socket = new Socket("localhost", PORT);
            logger.log(Level.INFO, "Connected to server on port: " + PORT);

            Thread receiveMessage = new Thread(new MessageReceiver(this.socket));
            Thread sendMessage = new Thread(new MessageWriter(this.socket));

            receiveMessage.start();
            sendMessage.start();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to connect to server");
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}

