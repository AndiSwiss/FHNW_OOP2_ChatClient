package ch.andreasambuehl.achat.controller;

import ch.andreasambuehl.achat.abstractClasses.Controller;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Platform;

import java.util.logging.Logger;

/**
 * This is the main controller for the chat client AChat.
 */
public class AChatController extends Controller<AChatModel, AChatView> {
    private ServiceLocator serviceLocator;
    private Logger logger;
    private Translator t;

    /**
     * Constructor of the controller. Also initializes all the listeners and action handlers.
     *
     * @param model model
     * @param view  view
     */
    public AChatController(AChatModel model, AChatView view) {
        super(model, view);

        serviceLocator = ServiceLocator.getServiceLocator();
        logger = serviceLocator.getLogger();
        t = serviceLocator.getTranslator();


        // register to listen for button clicks
        // Server section:
        view.btnConnectDisconnect.setOnAction(event -> {
            if (AChatModel.isServerConnected.get()) {
                model.disconnectServer();
                view.btnConnectDisconnect.setText(t.getString("button.connect"));
                view.lblStatusServer.setText(t.getString("label.connection.status-failed"));
            } else {
                String ipAddress = view.txtServer.getText();
                String portString = view.txtPort.getText();
                boolean useSSL = view.chkboxSSL.isSelected();

                boolean successful = model.connectServer(ipAddress, portString, useSSL);
                if (successful) {
                    view.btnConnectDisconnect.setText(t.getString("button.disconnect"));
                    view.lblStatusServer.setText(t.getString("label.connection.status-connected"));
                }
            }
        });

        // Account section:
        view.btnCreateLogin.setOnAction(event -> {
            String name = view.txtUsername.getText();
            String password = view.txtPassword.getText();
            boolean successful = model.createLogin(name, password);

            if (successful) {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountCreated"));
            } else {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountCreationFailed"));
            }
        });

        view.btnSendCommand.setOnAction(event -> {
            logger.info("Sending command: " + view.txtCommand.getText());
            String answer = model.sendDirectCommand(view.txtCommand.getText());
            view.listServerAnswers.getItems().add(answer);

        });


        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            model.disconnectServer();
            Platform.exit();
        });


        logger.info("Application controller initialized");
    }


}
