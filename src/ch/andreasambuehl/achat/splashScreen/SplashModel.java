package ch.andreasambuehl.achat.splashScreen;

import ch.andreasambuehl.achat.abstractClasses.Model;
import ch.andreasambuehl.achat.common.Configuration;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import javafx.concurrent.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 *
 * @author Brad Richards. With modifications by Andreas Ambühl
 */
public class SplashModel extends Model {
    private ServiceLocator serviceLocator;

    public SplashModel() {
        super();
    }

    // A task is a JavaFX class that implements Runnable. Tasks are designed to
    // have attached listeners, which we can use to monitor their progress.
    final Task<Void> initializer = new Task<Void>() {
        @Override
        protected Void call() {
            artificialWaitingTime();
            this.updateProgress(1, 6);

            // Create the service locator to hold our resources
            serviceLocator = ServiceLocator.getServiceLocator();
            artificialWaitingTime();
            this.updateProgress(2, 6);

            // Initialize the resources in the service locator
            serviceLocator.setLogger(configureLogging());
            artificialWaitingTime();
            this.updateProgress(3, 6);

            serviceLocator.setConfiguration(new Configuration());
            artificialWaitingTime();
            this.updateProgress(4, 6);

            String language = serviceLocator.getConfiguration().getOption("Language");
            serviceLocator.setTranslator(new Translator(language));
            artificialWaitingTime();
            this.updateProgress(5, 6);

            // ... more resources would go here ...
            artificialWaitingTime();
            this.updateProgress(6, 6);

            artificialWaitingTime();
            return null;
        }
    };

    public void initialize() {
        new Thread(initializer).start();
    }

    /**
     * We create a logger with the name of the application, and attach a file
     * handler to it. All logging should be done using this logger. Messages to
     * this logger will also flow up to the root logger, and from there to the
     * console-handler.
     * <p>
     * We set the level of the console-handler to "INFO", so that the console
     * only receives the more important messages. The levels of the loggers and
     * the file-handler are set to "FINEST".
     */
    private Logger configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.FINEST);

        // By default there is one handler: the console
        Handler[] defaultHandlers = Logger.getLogger("").getHandlers();
        defaultHandlers[0].setLevel(Level.INFO);

        // Introduced by Andreas Ambühl:
        // styling the logger:
        // Based on a code fragment seen on:
        // https://stackoverflow.com/questions/53211694/change-color-and-format-of-java-util-logging-logger-output-in-eclipse
        // by Manuel Moser (https://stackoverflow.com/users/8715352/manuel-moser).
        // I added some special styling for level WARNING and SEVERE + some other modifications.
        defaultHandlers[0].setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                final String ANSI_RESET = "\u001B[0m";
                final String ANSI_YELLOW = "\u001B[33m";
                final String ANSI_WHITE = "\u001B[37m";
                final String ANSI_RED = "\u001B[31m";

                // This example will print date/time, class, and log level in yellow,
                // followed by the log message and it's parameters in white .
                StringBuilder builder = new StringBuilder();

                if (record.getLevel().equals(Level.WARNING) || record.getLevel().equals(Level.SEVERE)) {
                    builder.append(ANSI_RED);
                } else {
                    builder.append(ANSI_YELLOW);
                }

                builder.append("[");
                builder.append(calcDate(record.getMillis()));
                builder.append("]");

                builder.append(" [");
                builder.append(record.getSourceClassName());
                builder.append("]");

                builder.append(" [");
                builder.append(record.getLevel().getName());
                builder.append("]");

                builder.append(ANSI_WHITE);
                builder.append(" - ");
                builder.append(record.getMessage());

                Object[] params = record.getParameters();

                if (params != null)
                {
                    builder.append("\t");
                    for (int i = 0; i < params.length; i++)
                    {
                        builder.append(params[i]);
                        if (i < params.length - 1)
                            builder.append(", ");
                    }
                }
                builder.append(ANSI_RESET);
                builder.append("\n");
                return builder.toString();
            }
        });

        // Add our logger
        Logger ourLogger = Logger.getLogger(serviceLocator.getAPP_NAME());
        ourLogger.setLevel(Level.FINEST);

        // Add a file handler, putting the rotating files in the tmp directory
        try {
            Handler logHandler = new FileHandler("%t/"
                    + serviceLocator.getAPP_NAME() + "_%u" + "_%g" + ".log",
                    1_000_000, 9);
            logHandler.setLevel(Level.FINEST);
            ourLogger.addHandler(logHandler);
        } catch (Exception e) { // If we are unable to create log files
            throw new RuntimeException("Unable to initialize log files: " + e.toString());
        }

        return ourLogger;
    }

    /**
     * For being able to see the splash screen, I introduce here an artificial pause:
     */
    private void artificialWaitingTime() {
        //
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Provides nice output of date and time.
     *
     * @param milliSeconds Time in milli seconds
     * @return formatted String
     */
    private String calcDate(long milliSeconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = new Date(milliSeconds);
        return dateFormat.format(resultDate);
    }
}
