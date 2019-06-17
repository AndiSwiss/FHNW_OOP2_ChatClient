package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private int value;

    public AChatModel() {
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
