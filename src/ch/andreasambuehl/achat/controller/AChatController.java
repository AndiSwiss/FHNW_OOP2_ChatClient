package ch.andreasambuehl.achat.controller;

import ch.andreasambuehl.achat.abstractClasses.Controller;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Platform;

/**
 * This is the main controller for the chat client AChat.
 */
public class AChatController extends Controller<AChatModel, AChatView> {
    private ServiceLocator serviceLocator;

    public AChatController(AChatModel model, AChatView view) {
        super(model, view);

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


        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> Platform.exit());

        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Application controller initialized");
    }


}
