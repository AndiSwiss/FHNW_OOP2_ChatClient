package ch.andreasambuehl.achat.common;

import ch.andreasambuehl.achat.AChat;
import ch.andreasambuehl.achat.model.ServerConnection;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * <p>
 * The singleton instance of this class provide central storage for resources
 * used by the program. It also defines application-global constants, such as
 * the application name.
 *
 * @author Brad Richards
 */
public class ServiceLocator {
    private static ServiceLocator serviceLocator; // singleton

    // Application-global constants
    final private Class<?> APP_CLASS = AChat.class;
    final private String APP_NAME = "AChat";

    // Supported locales (for translations)
    final private Locale[] locales = new Locale[]{
            new Locale("en"),
            new Locale("de")
    };

    // Resources
    private Logger logger;
    private Configuration configuration;
    private Translator translator;
    private ServerConnection serverConnection;

    /**
     * Factory method for returning the singleton
     *
     * @return The singleton resource locator
     */
    public static ServiceLocator getServiceLocator() {
        if (serviceLocator == null) serviceLocator = new ServiceLocator();
        return serviceLocator;
    }

    /**
     * Private constructor, because this class is a singleton
     */
    private ServiceLocator() {
        // We must define this constructor, because otherwise, the default
        // constructor would be public -> then it wouldn't be singleton anymore.
    }

    public void createServerConnection(String serverIpAddress, int serverPort) {
        serverConnection = new ServerConnection(serverIpAddress, serverPort);
    }

    public ServerConnection getServerConnection() {
        // this behaves like a singleton, because the serverConnection is anyhow singleton, because the serviceLocator
        // is a singleton, hence it's fields (in particular serverConnection) is also singleton!
        return serverConnection;
    }

    public void disconnectServer() {
        // first close the sockets (otherwise, the application cannot be really closed by just ending the application!
        try {
            serviceLocator.getServerConnection().inStream.close();
            serviceLocator.getServerConnection().outStream.close();
        } catch (Exception e) {
            // do nothing (e.g. if there is no server connection)
        }
        serverConnection = null;
    }


    public Class<?> getAPP_CLASS() {
        return APP_CLASS;
    }

    public String getAPP_NAME() {
        return APP_NAME;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Locale[] getLocales() {
        return locales;
    }

    public Translator getTranslator() {
        return translator;
    }

    public void setTranslator(Translator translator) {
        this.translator = translator;
    }
}
