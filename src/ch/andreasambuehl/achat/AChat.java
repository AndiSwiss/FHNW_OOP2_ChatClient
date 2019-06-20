package ch.andreasambuehl.achat;

import ch.andreasambuehl.achat.common.Configuration;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.controller.AChatController;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.splashScreen.SplashController;
import ch.andreasambuehl.achat.splashScreen.SplashModel;
import ch.andreasambuehl.achat.splashScreen.SplashView;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * AChat - a chat client by Andreas Ambühl
 * ---------------------------------------
 *
 * <p>
 * This is the main starting point of the whole chat client AChat.
 * Further information about this project is provided in the README.md
 *
 * @author Andreas Ambühl (with code fragments by Prof. Dr. Brad Richards)
 * (particularly: this application is built on the "JavaFX_App_Template v2" by
 * Prof. Dr. Brad Richards (Copyright 2015, BSD 3-clause license),
 * then edited and adopted by Andreas Ambühl)
 *
 * <p>
 * @version 0.5g
 *
 * <p>
 * Copyright 2019, Andreas Ambühl. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file license.txt).
 *
 * <p>
 * This copyright is also applicable for all the code found inside this folder or its sub-folders,
 * unless there is another copyright-info on the specific file.
 */
public class AChat extends Application {
    private static AChat mainProgram; // singleton
    private SplashView splashView;
    private AChatView view;
    private AChatModel model;


    private ServiceLocator serviceLocator; // resources, after initialization


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Note: This method is called on the main thread, not the JavaFX
     * Application Thread. This means that we cannot display anything to the
     * user at this point. Since we want to show a splash screen, this means
     * that we cannot do any real initialization here.
     * <p>
     * This implementation ensures that the application is a singleton; only one
     * per JVM-instance. On client installations this is not necessary (each
     * application runs in its own JVM). However, it can be important on server
     * installations.
     * <p>
     * Why is it important that only one instance run in the JVM? Because our
     * initialized resources are a singleton - if two programs instances were
     * running, they would use (and overwrite) each other's resources!
     */
    @Override
    public void init() {
        if (mainProgram == null) {
            mainProgram = this;
        } else {
            Platform.exit();
        }
    }

    /**
     * This method is called after init(), and is called on the JavaFX
     * Application Thread, so we can display a GUI. We have two GUIs: a splash
     * screen and the application. Both of these follow the MVC model.
     * <p>
     * We first display the splash screen. The model is where all initialization
     * for the application takes place. The controller updates a progress-bar in
     * the view, and (after initialization is finished) calls the startApp()
     * method in this class.
     */
    @Override
    public void start(Stage primaryStage) {
        // Create and display the splash screen and model
        SplashModel splashModel = new SplashModel();
        splashView = new SplashView(primaryStage, splashModel);
        new SplashController(this, splashModel, splashView);
        splashView.start();

        // Display the splash screen and begin the initialization
        splashModel.initialize();
    }

    /**
     * This method is called when the splash screen has finished initializing
     * the application. The initialized resources are in a ServiceLocator
     * singleton. Our task is to now create the application MVC components, to
     * hide the splash screen, and to display the application GUI.
     * <p>
     * Multitasking note: This method is called from an event-handler in the
     * Splash_Controller, which means that it is on the JavaFX Application
     * Thread, which means that it is allowed to work with GUI components.
     * http://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm
     */
    public void startApp() {
        Stage appStage = new Stage();

        // Initialize the application MVC components. Note that these components
        // can only be initialized now, because they may depend on the
        // resources initialized by the splash screen
        model = new AChatModel();
        view = new AChatView(appStage, model);
        new AChatController(model, view);

        // Resources are now initialized
        serviceLocator = ServiceLocator.getServiceLocator();

        // Close the splash screen, and set the reference to null, so that all
        // Splash_XXX objects can be garbage collected
        splashView.stop();
        splashView = null;

        view.start();
    }

    /**
     * The stop method is the opposite of the start method. It provides an
     * opportunity to close down the program, including GUI components. If the
     * start method has never been called, the stop method may or may not be
     * called.
     * <p>
     * Make the GUI invisible first. This prevents the user from taking any
     * actions while the program is ending.
     */
    @Override
    public void stop() {
        // Make the view invisible:
        if (view != null) {
            view.stop();
        }

        // read some entered values for restoring them the next time:
        Configuration config = serviceLocator.getConfiguration();
        config.setLocalOption("ServerIP", view.txtServer.getText());
        config.setLocalOption("ServerPort", view.txtPort.getText());
        config.setLocalOption("Username", view.txtUsername.getText());
        config.setLocalOption("Password", view.txtPassword.getText());

        // and save the config:
        config.save();

        // logout and disconnect
        if (AChatModel.getToken() != null) model.logout();
        model.disconnectServer();

        serviceLocator.getLogger().info("Application terminated (incl. logout + disconnect + configuration saved)");
    }

    /**
     * The stop method is the opposite of the start method. It provides an
     * opportunity to close down the program, including GUI components. If the
     * start method has never been called, the stop method may or may not be
     * called.
     * <p>
     * Make the GUI invisible first. This prevents the user from taking any
     * actions while the program is ending.
     */
    // Static getter for a reference to the main program object
    protected static AChat getMainProgram() {
        return mainProgram;
    }
}
