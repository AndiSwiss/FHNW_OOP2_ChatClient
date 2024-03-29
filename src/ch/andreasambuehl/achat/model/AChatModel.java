package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is the main model for the chat client AChat.
 */
public class AChatModel extends Model {
    private ServiceLocator serviceLocator;
    private Logger logger;

    // connection section:
    private SimpleBooleanProperty serverConnected;
    private SimpleBooleanProperty serverConnectionFailed;

    // account section:
    private SimpleStringProperty token;

    // people section:
    private ObservableList<String> observablePeopleList = FXCollections.observableArrayList();

    // chatroom section:
    private ObservableList<String> observableChatroomsList = FXCollections.observableArrayList();

    // chat section:
    private ObservableList<String> observableChatHistory = FXCollections.observableArrayList();
    private String[] sendChatMsgAnswer;


    /**
     * Constructor of the model
     */
    public AChatModel() {
        serverConnected = new SimpleBooleanProperty(false);
        serverConnectionFailed = new SimpleBooleanProperty(false);
        token = new SimpleStringProperty();

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();
        logger.info("Application model initialized");
    }


    //------------------------------//
    // send commands to the server: //
    //------------------------------//

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


    //---------------------//
    // connection section: //
    //---------------------//

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

        serviceLocator.createServerConnection(this, ipAddress, port);

        return serverConnected.get();
    }

    /**
     * Disconnects from the server
     */
    public void disconnectServer() {
        serviceLocator.disconnectServer(this);
        // set the serverConnected to false, when everything was successful
        serverConnected.set(false);
        logger.info("Server is disconnected.");
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


    //------------------//
    // account section: //
    //------------------//

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
        String[] answer = sendCommand("DeleteLogin|" + token.get());

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Account deleted successfully");

            // also reset the token:
            token.set(null);
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
            token.set(answer[2]);
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
            token.set(null);
            logger.info("Logout was successful");
            return true;
        } else {
            logger.warning("Logout was not successful!");
            return false;
        }
    }


    //-------------------//
    // chatroom section: //
    //-------------------//

    /**
     * Shows (and updates) the list of chatrooms
     *
     * @return success
     */
    public boolean listChatrooms() {
        String[] answer = sendCommand("ListChatrooms|" + token.get());

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
        String[] answer = sendCommand("CreateChatroom|" + token.get() + '|' + name + '|' + isPublic);

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
        String[] answer = sendCommand("DeleteChatroom|" + token.get() + '|' + name);

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
        String[] answer = sendCommand("JoinChatroom|" + token.get() + '|' + name + '|' + user);

        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Joined chatroom");
            return true;
        } else {
            logger.warning("Chatroom could not be joined!");
            return false;
        }
    }

    /**
     * Leaving a chatroom.
     * You can always remove yourself. Chatroom creator can remove anyone.
     *
     * @param name name of the chatroom
     * @param user username
     * @return success
     */
    public boolean leaveChatroom(String name, String user) {
        String[] answer = sendCommand("LeaveChatroom|" + token.get() + '|' + name + '|' + user);
        if (answer.length == 2 && answer[1].equals("true")) {
            logger.info("Left chatroom");
            return true;
        } else {
            logger.warning("Chatroom could not be left!");
            return false;
        }
    }


    //---------------//
    // chat section: //
    //---------------//

    /**
     * Sends a message to the server. Filters out any pipes for not breaking the communication.
     *
     * @param target  Chatroom or person
     * @param message message
     * @return success
     */
    public String sendChatMessage(String target, String message) {

        // Even though pipes will get filtered out by the server (and ignoring everything after the pipe), I rather
        // choose to filter them out and replace them:
        message = message.replace('|', '_');


        String[] answer = sendCommand("SendMessage|" + token.get() + '|' + target + '|' + message);

        if (answer.length == 2 && answer[1].equals("true")) {
            // But there is a problem: If I try to send the message to a public chatroom of which I'm not a member,
            // the server-answer is still "Result|true", even if it actually failed.
            // That is why I have to introduce a second check:
            // If the server broadcasts the whole message, such as: MessageText|andi|chatroom1|my message
            // then sending of a public message was a success
            String[] ans2 = sendChatMsgAnswer;
            if (ans2 != null && ans2.length == 4
                    && ans2[0].equals("MessageText") && ans2[2].equals(target) && ans2[3].equals(message)) {
                logger.info("Sent message '" + message + "' to target '" + target + "'");

                // reset to null for the next message:
                sendChatMsgAnswer = null;
                return "success";
            } else {
                logger.warning("Problems while sending a message!");

                // reset to null for the next message (most likely not necessary, because it should already be null!):
                sendChatMsgAnswer = null;
                return "noBroadcast";
            }
        } else {
            logger.warning("Sending message failed! (Message: " + message + ", target: " + target + ')');
            return "failed";
        }
    }


    //----------------//
    // other methods: //
    //----------------//

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


    //----------------------//
    // getters and setters: //
    //----------------------//
    public String getToken() {
        return token.get();
    }

    public SimpleStringProperty tokenProperty() {
        return token;
    }

    public ObservableList<String> getObservableChatHistory() {
        return observableChatHistory;
    }

    void setSendChatMsgAnswer(String[] sendChatMsgAnswer) {
        this.sendChatMsgAnswer = sendChatMsgAnswer;
    }


    public boolean getServerConnected() {
        return serverConnected.get();
    }

    public SimpleBooleanProperty serverConnectedProperty() {
        return serverConnected;
    }

    public void setServerConnected(boolean serverConnected) {
        this.serverConnected.set(serverConnected);
    }

    public ObservableList<String> getObservablePeopleList() {
        return observablePeopleList;
    }

    public ObservableList<String> getObservableChatroomsList() {
        return observableChatroomsList;
    }

    public SimpleBooleanProperty serverConnectionFailedProperty() {
        return serverConnectionFailed;
    }

    public void setServerConnectionFailed(boolean serverConnectionFailed) {
        this.serverConnectionFailed.set(serverConnectionFailed);
    }
}
