package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.logging.Logger;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private Logger logger;

    // server connection
    public static SimpleBooleanProperty isServerConnected;

    public AChatModel() {
        isServerConnected = new SimpleBooleanProperty(false);

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();
        logger.info("Application model initialized");
    }

    public boolean connectServer(String ipAddress, String portString, boolean useSSL) {
        boolean valid = validateIpAddress(ipAddress);
        if (!valid) {
            logger.warning("ipAddress was not valid: " + ipAddress);
            return false;
        }

        valid = validatePortNumber(portString);
        if (!valid) {
            logger.warning("portString was not valid: " + portString);
            return false;
        }
        int port = Integer.parseInt(portString);

        serviceLocator.createServerConnection(ipAddress, port);

        return isServerConnected.get();
    }

    public void disconnectServer() {
        serviceLocator.disconnectServer();
        // set the isServerConnected to false, when everything was successful
        isServerConnected.set(false);
        logger.info("Server is disconnected.");
    }

    public String sendCommand(String message) {
        return serviceLocator.getServerConnection().sendCommand(message);
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
