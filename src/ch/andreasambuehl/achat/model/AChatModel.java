package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.concurrent.Task;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private int value;

    // server connection
    public static volatile SimpleBooleanProperty isServerConnected;
    private static ServerConnection serverConnection;
    public static volatile SimpleListProperty<String> serverAnswers;

    public AChatModel() {
        value = 0;
        isServerConnected = new SimpleBooleanProperty(false);
        serverConnection = null;
        serverAnswers = new SimpleListProperty<>();

        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Application model initialized");
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


    public void connectServer(String ipAddress, String portString, boolean useSSL) {
        boolean valid = validateIpAddress(ipAddress);
        if (!valid) {
            ServiceLocator.getServiceLocator().getLogger().warning("ipAddress was not valid: " + ipAddress);
            return;
        }

        valid = validatePortNumber(portString);
        if (!valid) {
            ServiceLocator.getServiceLocator().getLogger().warning("portString was not valid: " + portString);
            return;
        }
        int port = Integer.parseInt(portString);

        serverConnection = new ServerConnection(ipAddress, port, false);


        // todo: maybe realize the whole thing with a task?  -> 11_Threads_Networking.pdf, slides 16...
/*
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                return null;
            }
        };
*/


        // todo: set the isServerConnected, when everything was successful
        isServerConnected.set(true);




    }

    public void disconnectServer() {

        serverConnection.interrupt();
        serverConnection = null;

        // todo: set the isServerConnected to false, when everything was successful
        isServerConnected.set(false);
    }


    /**
     * Validates an ip-address
     * Cody by Mr. Bradley Richards
     *
     * @param ipAddress ipAddress
     * @return true if validation was correct
     */
    private static boolean validateIpAddress(String ipAddress) {
        boolean formatOK = false;
        // Check for validity (not complete, but not bad)
        String[] ipPieces = ipAddress.split("\\."); // Must escape (see
        // documentation)
        // Must have 4 parts
        if (ipPieces.length == 4) {
            // Each part must be an integer 0 to 255
            formatOK = true; // set to false on the first error
            int byteValue = -1;
            for (String s : ipPieces) {
                byteValue = Integer.parseInt(s); // may throw
                // NumberFormatException
                if (byteValue < 0 | byteValue > 255) formatOK = false;
            }
        }
        return formatOK;
    }


    /**
     * Validates a port number
     * Cody by Mr. Bradley Richards
     *
     * @param portString port as String
     * @return true if validation was correct
     */
    private static boolean validatePortNumber(String portString) {
        boolean formatOK = false;
        try {
            int portNumber = Integer.parseInt(portString);
            if (portNumber >= 1024 & portNumber <= 65535) {
                formatOK = true;
            }
        } catch (NumberFormatException e) {
            // do nothing right here
        }
        return formatOK;
    }


}
