package ch.andreasambuehl.achat.controller;

import ch.andreasambuehl.achat.abstractClasses.Controller;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Platform;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is the main controller for the chat client AChat.
 */
public class AChatController extends Controller<AChatModel, AChatView> {
    private ServiceLocator serviceLocator;
    private Logger logger;

    public AChatController(AChatModel model, AChatView view) {
        super(model, view);

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();

        // register to listen for button clicks
        // Server connection:
        view.btnConnect.setOnAction(event -> {
            if (model.isServerConnected.get()) {
                model.disconnectServer();
                view.btnConnect.setText(serviceLocator.getTranslator().getString("button.connect"));
            } else {
                String ipAddress = view.txtServer.getText();
                String portString = view.txtPort.getText();
                boolean useSSL = view.chkboxSSL.isSelected();

                model.connectServer(ipAddress, portString, useSSL);
                view.btnConnect.setText(serviceLocator.getTranslator().getString("button.disconnect"));
            }
        });

        view.btnCommand.setOnAction(event -> {
            model.sendMessage(view.txtCommand.getText());
            logger.info("Sending command: " + view.txtCommand.getText());
        });

        // todo: create a listener for receiving server-answers in the GUI
        AChatModel.serverAnswers.addListener((observable, oldValue, newValue) -> {
            view.listServerAnswers.getItems().add(newValue);
            logger.info("Received answer from server: " + newValue);
        });

        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            serviceLocator.disconnectServer();
            Platform.exit();
        });


        logger.info("Application controller initialized");
    }


}
