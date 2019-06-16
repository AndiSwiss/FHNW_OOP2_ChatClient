package ch.andreasambuehl.chatclient.model;

import ch.andreasambuehl.chatclient.abstractClasses.Model;
import ch.andreasambuehl.chatclient.common.ServiceLocator;

public class ChatClientModel extends Model {
    ServiceLocator serviceLocator;
    private int value;

    public ChatClientModel() {
        value = 0;

        // todo: ServiceLocator
    }

    public int getValue() {
        return value;
    }

    public int incrementValue() {
        value++;
        // todo: ServiceLocator
        return value;
    }
}
