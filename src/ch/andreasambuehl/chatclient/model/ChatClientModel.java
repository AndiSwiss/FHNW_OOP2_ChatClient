package ch.andreasambuehl.chatclient.model;

import ch.andreasambuehl.chatclient.abstractClasses.Model;
import ch.andreasambuehl.chatclient.common.ServiceLocator;

public class ChatClientModel extends Model {
    private ServiceLocator serviceLocator;
    private int value;

    public ChatClientModel() {
        value = 0;

        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Application model initialized");
        // todo: ServiceLocator
    }

    public int getValue() {
        return value;
    }

    public int incrementValue() {
        // todo: replace this simple test with actual stuff for the chat-client

        value++;
        serviceLocator.getLogger().info("Application model: value incremented to " + value);
        return value;
    }
}
