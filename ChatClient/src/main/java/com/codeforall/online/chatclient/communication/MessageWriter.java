package com.codeforall.online.chatclient.communication;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Allows sending messages to the server
 */
public class MessageWriter extends CommunicationHandler implements Runnable{

    private BufferedWriter writer;

    public MessageWriter(Socket socket) {

        super(socket);

        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        while(this.socket.isConnected()) {

            try {
                this.writer.write(userInput(scanner) + "\n");
                this.writer.flush();

            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Unable to write message to the server");
                endConnection();
            }
        }

        scanner.close();
        endConnection();
    }

    /**
     * Allows for user keyboard input
     *
     * @param scanner
     * @return
     */
    private String userInput(Scanner scanner) {

        return scanner.nextLine();
    }

    /**
     * Closes stream writer
     */
    private void endConnection() {

        try {
            this.writer.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Unable to close writer stream.");
        }

    }
}