package com.codeforall.online.chatclient.communication;

import java.net.Socket;

public class CommunicationHandler {

    protected Socket socket;

    public CommunicationHandler(Socket socket) {
        this.socket = socket;
    }
}