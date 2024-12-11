package com.codeforall.online.chatclient.communication;

import java.io.*;
import java.net.Socket;

/**
 * Allows for receiving server messages
 */
public class MessageReceiver extends CommunicationHandler implements Runnable{

    private BufferedReader reader;

    public MessageReceiver(Socket socket) {

        super(socket);

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        } catch (IOException e) {
            System.out.println(e.getMessage());
            endConnection();
        }
    }

    @Override
    public void run() {

        while(this.socket.isConnected()) {

            try {
                String serverMessage = this.reader.readLine();

                if(serverMessage != null && !serverMessage.isEmpty()) {
                    System.out.println(serverMessage);
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Unable to read message from the server.");
            }
        }

        endConnection();
    }

    /**
     * Close the stream reader
     */
    private void endConnection() {

        try {
            this.reader.close();
        } catch (IOException e) {
            System.out.println("Unable to close reader stream.");
        }
    }
}