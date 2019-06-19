package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerConnection {

    private Logger logger;

    public OutputStreamWriter outStream;
    public DataInputStream inStream;

    // used for waiting for an answer after having sent a command to the server:
    private boolean requestPending;

    private String serverAnswer;


    public ServerConnection(String serverIpAddress, int serverPort) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        try {
            Socket socket = new Socket(serverIpAddress, serverPort);
            logger.info("Connected with server " + serverIpAddress
                    + ":" + serverPort);
            requestPending = false;

            // Create thread to read incoming messages

            inStream = new DataInputStream(socket.getInputStream());

            Runnable r = () -> {
                while (true) {
                    String msg;
                    try {
                        msg = inStream.readLine();
                        logger.info("Message received: " + msg);
                        serverAnswer = msg;
                        requestPending = false;
                    } catch (IOException e) {
                        break;
                    }
                    if (msg == null) break; // In case the server closes the socket
                }
            };
            Thread t = new Thread(r);
            t.start();

            outStream = new OutputStreamWriter(socket.getOutputStream());
            logger.info("Server connection established");
            AChatModel.isServerConnected.set(true);
        } catch (IOException e) {
            logger.info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
            // todo: what should I do, when the connection doesn't work?
        }
    }


    // todo: eventually change to private!
    String sendCommand(String command) {
        try {
            outStream.write(command + '\n');
            outStream.flush();
        } catch (IOException e) {
            logger.warning("Error while trying to write the following command to the outStream: " + command);
            logger.warning(e.getMessage());
        }

        requestPending = true;
        // while waiting for the response (from the other thread)

        logger.info("Waiting for server to answer to my command: " + command);

        while (requestPending) Thread.yield();

        return serverAnswer;
    }

}
