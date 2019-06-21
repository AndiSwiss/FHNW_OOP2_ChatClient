package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class ServerConnection {

    private Logger logger;

    private Socket socket;

    public OutputStreamWriter outStream;
    public DataInputStream inStream;

    // used for waiting for an answer after having sent a command to the server:
    private boolean requestPending;

    private String serverAnswer;


    public ServerConnection(AChatModel model, String serverIpAddress, int serverPort) {

        logger = ServiceLocator.getServiceLocator().getLogger();

        try {
            socket = new Socket(serverIpAddress, serverPort);

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

                        String msgType = msg.substring(0, msg.indexOf('|'));

                        if (msgType.equals("Result")) {
                            serverAnswer = msg;
                            requestPending = false;
                            System.out.println("Received a result " + msg);
                        } else if (msgType.equals("MessageText")) {
                            System.out.println("Received a message " + msg);

                            // If I sent the message, also save to sendChatMsgAnswer for validation in the
                            // model
                            if (requestPending) {
                                model.setSendChatMsgAnswer(msg.split("\\|"));
                            }

                            // todo: when doing the following for the first time, everything seems to be fine. But
                            //  if I do it for the 2nd and later times, it works, but it throws an error:
                            //  Exception in thread "Thread-7" java.lang.IllegalStateException: Not on FX application thread; currentThread = Thread-7
                            //  -> see https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
                            model.getObservableChatHistory().add(LocalDateTime.now().toString() + "|" + msg);

                        } else {
                            logger.warning("received a message other than 'Result|...' or 'MessageText|...': "
                                    + msg);
                        }

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
            model.setIsServerConnected(true);
        } catch (IOException e) {
            logger.info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
        }
    }


    String sendCommand(String command) {
        // todo: first check, whether the socket still has a connection
        //  I tried a lot with
        //   - socket.isConnected()
        //   - reachable = InetAddress.getByName("javaprojects.ch").isReachable(1000);
        //      - and hundreds of variants of it, never worked
        //  -
        //  But trying to just resolve the name, that helped to determine, whether there is actually still a
        //  connection to the server or not!
        //  -
        //  BUT: THAT ONLY WORKS JUST ONCE!!!!!!!! It seems, that somewhere on my machine, this request gets cached,
        //  so this only works once!!!!!!!!!!!!!!!!!    Why????
        //  -
        //  Everything I tried was in official documentations and I didn't find any information about why this shouldn't
        //  work
        //  -
        //  I don't have another idea to realize this now... and I lost too many hours reaching nothing :-(
/*
        boolean reachable;
        try {
            InetAddress ipAdressFromName = InetAddress.getByName("javaprojects.ch");
            System.out.printf("ipAdressFromName: %s\n", ipAdressFromName);
            reachable = true;
        } catch (UnknownHostException e) {
            reachable = false;
            System.out.println("was not reachable!!");
        }
*/
        // todo: for now, just set it to true!
        boolean reachable = true;

        if (reachable) {
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

            while (requestPending) Thread.yield();

            return serverAnswer;
        } else {
            return "Server not answering!!";
        }
    }

}
