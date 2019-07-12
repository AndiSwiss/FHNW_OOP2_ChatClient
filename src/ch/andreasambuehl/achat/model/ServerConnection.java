package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;
import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * This class is for establishing a server connection and for talking with the server.
 */
public class ServerConnection {

    private Logger logger;

    private Socket socket;
    private boolean socketConnected;

    public OutputStreamWriter outStream;
    public DataInputStream inStream;

    // used for waiting for an answer after having sent a command to the server:
    private boolean requestPending;
    private boolean timeOut;

    private String serverAnswer;


    public ServerConnection(AChatModel model, String serverIpAddress, int serverPort) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        try {
            // PROBLEM SOLVED (Problem: I was not able to get a "SocketTimeoutException"!)
            //  Instead of providing the ipAddress and the port directly in the constructor of the socket,
            //  you provide the address in the method .connect()
            //  There you can optionally provide a timeout -> this throws a SocketTimeoutException after the timeout.
            //  -> That works perfectly (code from slides 01_Exceptions.pdf, slide 32: Port connection logic)
            //  -> (comparing to my previous code, where I created a separate runnable (which just waited for one second
            //     and then set the boolean value timeOut to true). This was sort of working, but the thread was never
            //     terminated resulting the app to not properly terminate when quitting the app.
            socket = new Socket();
            try {
                // connect with the socket and define a timeout in ms:
                socket.connect(new InetSocketAddress(serverIpAddress, serverPort), 2000);
                socketConnected = true;
            } catch (SocketTimeoutException e) {
                socketConnected = false;
            }


            if (!socketConnected) {
                logger.warning("Server was not reachable!");
                model.setServerConnectionFailed(true);

            } else {
                logger.info("Connected with server " + serverIpAddress
                        + ":" + serverPort);

                // Create thread to read incoming messages
                inStream = new DataInputStream(socket.getInputStream());

                Runnable r = () -> {
                    while (true) {
                        String msg;
                        try {
                            msg = inStream.readLine();
                            logger.info("Message received: " + msg);

                            String msgType = msg.substring(0, msg.indexOf('|'));

                            if (msgType.equals("Result")) {
                                serverAnswer = msg;
                                requestPending = false;
                            } else if (msgType.equals("MessageText")) {

                                // If I sent the message, also save to sendChatMsgAnswer for validation in the
                                // model
                                if (requestPending) {
                                    model.setSendChatMsgAnswer(msg.split("\\|"));
                                }

                                // If I just write the following line:
                                //   model.getObservableChatHistory().add(LocalDateTime.now().toString() + "|" + msg;
                                // then doing the following for the first time, everything seems to be fine. But
                                // if I do it for the 2nd and later times, it works, but it throws an error:
                                // Exception in thread "Thread-7" java.lang.IllegalStateException: Not on FX application thread; currentThread = Thread-7
                                // Since I write something to another thread, I have to realize that with Platform.runLater().
                                //  -> see https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
                                Platform.runLater(() -> model.getObservableChatHistory().add(currentDateTime() + "|" + msg));

                            } else {
                                logger.warning("received a message other than 'Result|...' or 'MessageText|...': "
                                        + msg);
                            }
                        } catch (IOException e) {
                            break;
                        }
                    }
                };
                Thread t = new Thread(r);
                t.start();

                outStream = new OutputStreamWriter(socket.getOutputStream());
                logger.info("Server connection established");
                model.setServerConnected(true);
            }
        } catch (IOException e) {
            logger.info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
//        } catch (InterruptedException e) {
//            logger.warning(e.toString());
        }
    }


    /**
     * Provides nice output of date and time.
     *
     * @return formatted String
     */
    private String currentDateTime() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dateFormat.format(now);
    }


    /**
     * Sends a command to the server and returns the answer as soon as it gets one.
     *
     * @param command command
     * @return answer
     */
    String sendCommand(String command) {
        // initialize important flags:
        requestPending = false;
        timeOut = false;

        // While sending the command later on in this method, I create first this separate thread,
        // which just simply waits for a second. If there is no server-answer in this time period,
        // then the flag timeOut is set to true and then, this method will stop waiting for the answer.
        Runnable timeOutRunnable = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                timeOut = true;
            }
            timeOut = true;
        };
        Thread timeOutThread = new Thread(timeOutRunnable);
        timeOutThread.start();

        try {
            outStream.write(command + '\n');
            outStream.flush();
        } catch (IOException e) {
            logger.warning("Error while trying to write the following command to the outStream: " + command);
            logger.warning(e.getMessage());
        }

        requestPending = true;
        // while waiting for the response (from the other thread)

        logger.info("Message sent: " + command);

        while (requestPending && !timeOut) Thread.yield();

        if (timeOut) {
            requestPending = false;
            logger.warning("Connection time out while trying to send command " + command);
            return "Result|timeout";
        } else {
            return serverAnswer;
        }
    }
}
