package ch.andreasambuehl.achat.model;

import ch.andreasambuehl.achat.common.ServiceLocator;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Thread {

    private String serverIpAddress;
    private int serverPort;
    private boolean serverUseSSL;

    private Socket socket;

    public ServerConnection(String serverIpAddress, int serverPort, boolean serverUseSSL) {
        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
        this.serverUseSSL = serverUseSSL;

        // todo: implement SSL secure

        try {
            socket = new Socket(serverIpAddress, serverPort);
            ServiceLocator.getServiceLocator().getLogger().info("Connected with server " + serverIpAddress
                    + ":" + serverPort);

        } catch (IOException e) {
            ServiceLocator.getServiceLocator().getLogger().info("Connection with server failed: " + serverIpAddress
                    + ":" + serverPort);
            // todo: what should I do, when the connection doesn't work?
        }


    }

    @Override
    public void run() {
        super.run();
    }


    @Override
    public void interrupt() {
        ServiceLocator.getServiceLocator().getLogger().info("Disconnected from server");
        super.interrupt();
    }
}
