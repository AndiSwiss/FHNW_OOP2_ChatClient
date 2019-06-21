package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private Logger logger;

    // server connection
    public static SimpleBooleanProperty isServerConnected;

    // account
    private static String token;

    // left section
    public ObservableList<String> observablePeopleList = FXCollections.observableArrayList();
    public ObservableList<String> observableChatroomsList = FXCollections.observableArrayList();

    // center section
    public ObservableList<VBox> observableChatHistory = FXCollections.observableArrayList();

    /**
     * Constructor of the model
     */
    public AChatModel() {
        isServerConnected = new SimpleBooleanProperty(false);

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();
        logger.info("Application model initialized");
    }

    //--------------//
    // top section: //
    //--------------//

    /**
     * Connect with the server
     *
     * @param ipAddress  ipAddress
     * @param portString port as as String
     * @param useSSL     whether to use SSL
     * @return true if connection was successfully established
     */
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

    /**
     * Disconnects from the server
     */
    public void disconnectServer() {
        serviceLocator.disconnectServer();
        // set the isServerConnected to false, when everything was successful
        isServerConnected.set(false);
        logger.info("Server is disconnected.");
    }


    /**
     * Method for sending any command to the server with a raw answer (simple String)
     *
     * @param message message
     * @return String with the full answer of the server
     */
    public String sendDirectCommand(String message) {

        return serviceLocator.getServerConnection().sendCommand(message);
    }

    /**
     * Internal method for sending any command to the server with a structured answer (String[])
     *
     * @param message message
     * @return answer in separated parts
     */
    private String[] sendCommand(String message) {
        String answer = serviceLocator.getServerConnection().sendCommand(message);
        return answer.split("\\|");
    }

    /**
     * Pings server
     */
    public boolean pingServer() {
        String[] answer = sendCommand("Ping");

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Ping successful");
            return true;
        } else {
            logger.warning("Ping failed");
            return false;
        }
    }

    /**
     * Creates a login
     *
     * @param name     username
     * @param password password
     * @return success
     */
    public boolean createLogin(String name, String password) {
        String[] answer = sendCommand("CreateLogin|" + name + '|' + password);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Login created successfully");
            return true;
        } else {
            logger.warning("Login was not created");
            return false;
        }
    }

    /**
     * Deletes a login. Only works when you are logged in!
     *
     * @return success
     */
    public boolean deleteLogin() {
        String[] answer = sendCommand("DeleteLogin|" + token);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Account deleted successfully");

            // also reset the token:
            token = null;
            return true;
        } else {
            logger.warning("Account was not deleted!");
            return false;
        }
    }

    /**
     * Log in to the server. The returned token is saved to the static field 'token'
     *
     * @param name     username
     * @param password password
     * @return success
     */
    public boolean login(String name, String password) {
        String[] answer = sendCommand("Login|" + name + '|' + password);

        if (answer.length == 3 && answer[1].equals("true")) {
            token = answer[2];
            logger.info("Login successful. Token received: " + answer[2]);
            return true;
        } else {
            logger.warning("Login was not successful!");
            return false;
        }
    }


    /**
     * Log out from the server. And set the token to 'null'
     *
     * @return success
     */
    public boolean logout() {
        String[] answer = sendCommand("Logout");


        if (answer.length == 2 && answer[1].equals("true")) {
            token = null;
            logger.info("Logout was successful");
            return true;
        } else {
            logger.warning("Logout was not successful!");
            return false;
        }
    }

    /**
     * Shows (and updates) the list of chatrooms
     *
     * @return success
     */
    //---------------//
    // left section: //
    //---------------//
    public boolean listChatrooms() {
        String[] answer = sendCommand("ListChatrooms|" + token);

        if (answer.length > 1 && answer[1].equals("true")) {
            List<String> rooms = Arrays.asList(answer).subList(2, answer.length);
            rooms.sort(String::compareToIgnoreCase);
            observableChatroomsList.setAll(rooms);
            logger.info("Chatroom-list successfully fetched");
            return true;
        } else {
            logger.warning("ListChatrooms failed!");
            return false;
        }
    }

    /**
     * Create a chatroom
     *
     * @param name     name of the chatroom
     * @param isPublic public or private
     * @return success
     */
    public boolean createChatroom(String name, boolean isPublic) {
        String[] answer = sendCommand("CreateChatroom|" + token + '|' + name + '|' + isPublic);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Chatroom created successful");
            return true;
        } else {
            logger.warning("Chatroom could not be created!");
            return false;
        }
    }

    /**
     * Delete a chatroom
     *
     * @param name name of the chatroom
     * @return success
     */
    public boolean deleteChatroom(String name) {
        String[] answer = sendCommand("DeleteChatroom|" + token + '|' + name);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Chatroom deleted successful");
            return true;
        } else {
            logger.warning("Chatroom could not be deleted!");
            return false;
        }
    }

    /**
     * Joining a chatroom
     *
     * @param name name of the chatroom
     * @param user username (either oneself or somebody else (only the creator can add user to a private chatroom!)
     * @return success
     */
    public boolean joinChatroom(String name, String user) {
        String[] answer = sendCommand("JoinChatroom|" + token + '|' + name + '|' + user);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Joined chatroom");
            return true;
        } else {
            logger.warning("Chatroom could not be joined!");
            return false;
        }
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


    // getters and setters:

    public static String getToken() {
        return token;
    }
}
