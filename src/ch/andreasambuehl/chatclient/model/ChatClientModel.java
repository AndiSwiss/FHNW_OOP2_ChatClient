package ch.andreasambuehl.chatclient.model;

public class ChatClientModel {
    private int value;

    public ChatClientModel() {
        value = 0;
    }

    public int getValue() {
        return value;
    }

    public int incrementValue() {
        value++;
        return value;
    }
}
