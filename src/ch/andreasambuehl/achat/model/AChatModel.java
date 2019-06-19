package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.concurrent.Task;

import java.util.logging.Logger;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private Logger logger;

    // server connection
    public static SimpleBooleanProperty isServerConnected;
//    private static ServerConnection serverConnection;
//    private Task serverTask;
//    private Thread serverThread;
    public static SimpleListProperty<String> serverAnswers;

    public AChatModel() {
        isServerConnected = new SimpleBooleanProperty(false);
//        serverConnection = null;
//        serverTask = null;
        serverAnswers = new SimpleListProperty<>();

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();
        logger.info("Application model initialized");

    }


    public void connectServer(String ipAddress, String portString, boolean useSSL) {
        boolean valid = validateIpAddress(ipAddress);
        if (!valid) {
            logger.warning("ipAddress was not valid: " + ipAddress);
            return;
        }

        valid = validatePortNumber(portString);
        if (!valid) {
            logger.warning("portString was not valid: " + portString);
            return;
        }
        int port = Integer.parseInt(portString);



/*
        // create a separate thread:
        Runnable r = new Runnable() {
            @Override
            public void run() {
                serverConnection = new ServerConnection(ipAddress, port);
            }
        };
        serverThread = new Thread(r);
        serverThread.start();
*/

        // new: create the server-connection via the ServiceLocator
        // and don't create a new Thread. Otherwise, all messages to send have to go via an individual task with
        // all the code-overhead of each task!

        serviceLocator.createServerConnection(ipAddress, port);



        // todo: set the isServerConnected, when everything was successful
        isServerConnected.set(true);


    }

    public void disconnectServer() {

        serviceLocator.disconnectServer();

//        serverConnection.interrupt();
//        serverConnection = null;

        // todo: set the isServerConnected to false, when everything was successful
        isServerConnected.set(false);
        logger.info("Server is disconnected.");
    }

    public void sendMessage(String message) {
        // todo: forward the message to the server-connection, which is living inside
        //  the thread 'serverThread' -> so how can I forward this message?
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
