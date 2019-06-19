package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ServerConnection {

    Logger logger;

    private String serverIpAddress;
    private int serverPort;
    public OutputStreamWriter outStream;
    public DataInputStream inStream;

    // used for waiting for an answer after having sent a command to the server:
    private boolean requestPending;

    private Socket socket;

    public String serverAnswer;


    public ServerConnection(String serverIpAddress, int serverPort) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;


        try {
            socket = new Socket(serverIpAddress, serverPort);
            logger.info("Connected with server " + serverIpAddress
                    + ":" + serverPort);
            AChatModel.isServerConnected.set(true);

            requestPending = false;

            // Create thread to read incoming messages

            inStream = new DataInputStream(socket.getInputStream());
            serverAnswer = new String();


            Runnable r = () -> {
                while (true) {
                    String msg;
                    try {
                        msg = inStream.readLine();

                        // todo: remove the println as soon as I can communicate via the GUI
                        System.out.println("Received: " + msg);

                        serverAnswer = msg;
                        requestPending = false;


                    } catch (IOException e) {
                        break;
                    }
                    if (msg == null) break; // In case the server closes the socket
                }
                logger.info("Finished reading inputs from inStream from the server");

            };
            Thread t = new Thread(r);
            t.start();


            outStream = new OutputStreamWriter(socket.getOutputStream());

            logger.info("Server connection established");

/*
                // Loop, allowing the user to send messages to the server
                System.out.println("Enter commands:");
                try (Scanner in = new Scanner(System.in)) {
                    while (AChatModel.isServerConnected.get()) {
                        String line = in.nextLine();
                        outStream.write(line + "\n");
                        outStream.flush();
                        System.out.println("Sent: " + line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/


        } catch (IOException e) {
            logger.info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
            // todo: what should I do, when the connection doesn't work?
        }


    }


    // todo: eventually change to private!
    public String sendCommand(String command) {
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
