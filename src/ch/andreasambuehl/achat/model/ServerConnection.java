package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerConnection {

    Logger logger;

    private String serverIpAddress;
    private int serverPort;
    public OutputStreamWriter socketOut;
    public DataInputStream socketIn;

    private Socket socket;

    public ServerConnection(String serverIpAddress, int serverPort) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;


        try {
            socket = new Socket(serverIpAddress, serverPort);
            logger.info("Connected with server " + serverIpAddress
                    + ":" + serverPort);
            AChatModel.isServerConnected.set(true);


            // Create thread to read incoming messages

            socketIn = new DataInputStream(socket.getInputStream());

            Runnable r = () -> {
                while (true) {
                    String msg;
                    try {
                        msg = socketIn.readLine();

                        // todo: remove the println as soon as I can communicate via the GUI
                        System.out.println("Received: " + msg);

                        // todo: fix the following line, because currently, this line produces an error:
                        //  Exception in thread "Thread-6" java.lang.UnsupportedOperationException
//                                AChatModel.serverAnswers.add(msg);
                    } catch (IOException e) {
                        break;
                    }
                    if (msg == null) break; // In case the server closes the socket
                }
                logger.info("Finished reading inputs from socketIn from the server");

            };
            Thread t = new Thread(r);
            t.start();


            socketOut = new OutputStreamWriter(socket.getOutputStream());

            logger.info("Server connection established");

/*
                // Loop, allowing the user to send messages to the server
                System.out.println("Enter commands:");
                try (Scanner in = new Scanner(System.in)) {
                    while (AChatModel.isServerConnected.get()) {
                        String line = in.nextLine();
                        socketOut.write(line + "\n");
                        socketOut.flush();
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
