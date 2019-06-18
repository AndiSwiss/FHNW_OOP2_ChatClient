package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerConnection {

    Logger logger;

    private String serverIpAddress;
    private int serverPort;
    private boolean serverUseSSL;
    public OutputStreamWriter socketOut;

    private Socket socket;

    public ServerConnection(String serverIpAddress, int serverPort, boolean serverUseSSL) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
        this.serverUseSSL = serverUseSSL;


        // todo: implement SSL secure connection

        try {
            socket = new Socket(serverIpAddress, serverPort);
            logger.info("Connected with server " + serverIpAddress
                    + ":" + serverPort);
            AChatModel.isServerConnected.set(true);


            try (BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // Create thread to read incoming messages

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        while (AChatModel.isServerConnected.get()) {
                            String msg;
                            try {
                                msg = socketIn.readLine();
                                System.out.println("Received: " + msg);
                            } catch (IOException e) {
                                break;
                            }
                            if (msg == null) break; // In case the server closes the socket
                        }
                        logger.info("Finished reading inputs from socketIn from the server");

                    }
                };
                Thread t = new Thread(r);
                t.start();


                socketOut = new OutputStreamWriter(socket.getOutputStream());
                // Loop, allowing the user to send messages to the server
                // Note: We still have our scanner
                System.out.println("Enter commands or enter 'quit'");
                try (Scanner in = new Scanner(System.in)) {
                    while (AChatModel.isServerConnected.get()) {
                        String line = in.nextLine();
                        if (line.toLowerCase().equals("quit")) {
                            break;
                        }
                        socketOut.write(line + "\n");
                        socketOut.flush();
                        System.out.println("Sent: " + line);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("Finished reading input from terminal and closed the Scanner");

            }


        } catch (IOException e) {
            logger.info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
            // todo: what should I do, when the connection doesn't work?
        }


    }

/*
    @Override
    public void run() {
        super.run();
    }
*/


/*
    @Override
    public void interrupt() {
        logger.info("Disconnected from server");
//        super.interrupt();
    }
*/
}
