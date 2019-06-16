package ch.andreasambuehl.chatclient.common;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * <p>
 * This class provides functionality for loading and saving program options.
 * Default options may be delivered with the application; local options (changed
 * by the user) are saved to a file. Program constants can be defined by defining
 * options that the user has no way to change.
 * <p>
 *
 * @author Andreas Ambühl (with code fragments by Prof. Dr. Brad Richards)
 */
public class Configuration {
    // for easy reference:
    ServiceLocator sl = ServiceLocator.getServiceLocator();
    Logger logger = sl.getLogger();

    private Properties defaultOptions;
    private Properties localOptions;

    public Configuration() {
        // Load default properties from wherever the code is
        defaultOptions = new Properties();
        String defaultFilename = sl.getAPP_NAME() + "_defaults.cfg";
        // todo: maybe do the defaultOptions-reading in a 'try with resources'?
        //  And the same later on with reading the localOptions.
        //  If I do so, then change the Copyright-message in the JavaDoc of the class.

        InputStream inStream = sl.getAPP_CLASS().getResourceAsStream(defaultFilename);
        try {
            defaultOptions.load(inStream);
            logger.config("Default configuration file found");
        } catch (Exception e) {
            logger.warning("No default configuration file found at: " + defaultFilename);
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }

        // Define locally saved properties; link to the default values
        localOptions = new Properties(defaultOptions);

        // Load the local configuration file, if it exists.
        try {
            inStream = new FileInputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.load(inStream);
        } catch (FileNotFoundException e) { // from opening the properties file
            logger.config("No local configuration file found");
        } catch (IOException e) { // from loading the properties
            logger.warning("Error reading local options file: " + e.toString());
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }

        for (Enumeration<Object> i = localOptions.keys(); i.hasMoreElements(); ) {
            String key = (String) i.nextElement();
            logger.config("Option: " + key + " = " + localOptions.getProperty(key));
        }
    }

    public void save() {
        FileOutputStream propFile = null;
        try {
            propFile = new FileOutputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.store(propFile, null);
            logger.config("Local configuration file saved");
        } catch (Exception e) {
            logger.warning("Unable to save local options: " + e.toString());
        } finally {
            if (propFile != null) {
                try {
                    propFile.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public String getOption(String name) {
        return localOptions.getProperty(name);
    }

    public void setLocalOption(String name, String value) {
        localOptions.setProperty(name, value);
    }
}
