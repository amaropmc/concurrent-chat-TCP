package com.codeforall.online.chatserver.util;

public enum Commands {
    LIST("List all the clients in the server"),
    WHISPER("Send a message to a specific user"),
    NAME("Change the username"),
    HELP("List all the available commands");

    private final String description;

    Commands(String description) {
        this.description = description;
    }

    public String toString() {
        StringBuilder command = new StringBuilder();

        command.append("/")
                .append(name().toLowerCase())
                .append(": ")
                .append(description);

        return command.toString();
    }
}
