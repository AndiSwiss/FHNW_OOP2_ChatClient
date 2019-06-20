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
        //--------------//
        // top section: //
        //--------------//
        // Server section:
        view.btnConnectDisconnect.setOnAction(event -> {
            if (AChatModel.isServerConnected.get()) {
                model.disconnectServer();
                view.btnConnectDisconnect.setText(t.getString("button.connect"));
                view.btnPingServer.setDisable(true);
                view.lblStatusServer.setText(t.getString("label.connection.status.failed"));
            } else {
                String ipAddress = view.txtServer.getText();
                String portString = view.txtPort.getText();
                boolean useSSL = view.chkboxSSL.isSelected();

                boolean successful = model.connectServer(ipAddress, portString, useSSL);
                if (successful) {
                    view.btnConnectDisconnect.setText(t.getString("button.disconnect"));
                    view.btnPingServer.setDisable(false);
                    view.lblStatusServer.setText(t.getString("label.connection.status.connected"));
                }
            }
        });

        view.btnPingServer.setOnAction(event -> {
            boolean success = model.pingServer();
            if (success) {
                view.lblStatusServer.setText(t.getString("label.connection.status.pingSuccess"));
            } else {
                view.lblStatusServer.setText(t.getString("label.connection.status.pingFailed"));
            }
        });

        // Account section:
        view.btnSignInSignOut.setOnAction(event -> {
            if (AChatModel.getToken() == null) {
                String name = view.txtUsername.getText();
                String password = view.txtPassword.getText();
                boolean successful = model.login(name, password);

                if (successful) {
                    view.btnSignInSignOut.setText(t.getString("button.account.signOut"));
                    view.lblStatusAccount.setText(t.getString("label.account.status.loggedIn"));
                    view.btnCreateLogin.setDisable(true);
                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.loginFailed"));
                }
            } else {
                boolean successful = model.logout();

                if (successful) {
                    view.btnSignInSignOut.setText(t.getString("button.account.signIn"));
                    view.lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));
                    view.btnCreateLogin.setDisable(false);
                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.logoutFailed"));
                }
            }
        });


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


        view.btnDeleteLogin.setOnAction(event -> {
            // Login if not already logged in:
            boolean loginSuccess = true;
            if (AChatModel.getToken() == null) {
                String name = view.txtUsername.getText();
                String password = view.txtPassword.getText();
                loginSuccess = model.login(name, password);

            }
            if (loginSuccess) {
                boolean deleteSuccess = model.deleteLogin();
                if (deleteSuccess) {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountDeleted"));
                    view.btnSignInSignOut.setText(t.getString("button.account.signIn"));

                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountDeletionFailed"));
                }
            } else {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountDeletionNotFound"));
            }
        });

        //---------------//
        // left section: //
        //---------------//
        view.btnUpdateChatroomsList.setOnAction(event -> {
            boolean success = model.listChatrooms();

            // todo: introduce a general status field in the GUI for being able to post success-messages (maybe even
            //  the logger messages themselves, for less overhead in the code!
        });



        // for development:
        view.btnSendCommand.setOnAction(event -> {
            logger.info("Sending command: " + view.txtCommand.getText());
            String answer = model.sendDirectCommand(view.txtCommand.getText());
            view.lastServerAnswerContent.setText(answer);
        });


        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            model.disconnectServer();
            Platform.exit();
        });

        logger.info("Application controller initialized");
    }


}
