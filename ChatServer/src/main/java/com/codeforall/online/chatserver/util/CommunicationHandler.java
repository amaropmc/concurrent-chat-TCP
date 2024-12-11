package com.codeforall.online.chatserver.util;

import java.io.*;
import java.net.Socket;

public class CommunicationHandler {

    private BufferedReader reader;
    private BufferedWriter writer;

    public CommunicationHandler(Socket client) {

        try {
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a message from the client
     * @throws IOException
     **/
    public String readMessage() throws IOException {
        return reader.readLine();
    }

    /**
     * Writes messages to the client
     * @param message
     * @throws IOException
     */
    public void writeMessage(String message) throws IOException {

        writer.write(message);
        writer.flush();

    }

    /**
     * Closes all connections
     * @throws IOException
     */
    public void endConnection() throws IOException {

        this.reader.close();
        this.writer.close();

    }
}
