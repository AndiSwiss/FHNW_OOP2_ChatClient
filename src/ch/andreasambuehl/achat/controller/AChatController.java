package ch.andreasambuehl.achat.controller;

import ch.andreasambuehl.achat.AChat;
import ch.andreasambuehl.achat.abstractClasses.Controller;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;
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
        // Language:
        view.menuFileLanguage.getItems().forEach(menuItem -> {
            String language = menuItem.getText();
            menuItem.setOnAction(event -> {
                serviceLocator.getConfiguration().setLocalOption("Language", language);
                serviceLocator.setTranslator(new Translator(language));
                view.updateTexts();
            });
        });

        // Server section:
        view.btnConnectDisconnect.setOnAction(event -> {
            if (AChatModel.isServerConnected.get()) {
                if (AChatModel.getToken() != null) {
                    model.logout();
                    view.btnSignInSignOut.setText(t.getString("button.account.signIn"));
                    view.lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));
                    view.btnCreateLogin.setDisable(false);
                }
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
                    view.btnCreateLogin.setDisable(false);

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

        view.btnCreateChatroom.setOnAction(event -> {
            // open a dialog
            // adapted code from https://code.makery.ch/blog/javafx-dialogs-official/ -> section "Custom Login Dialog"
            Dialog<Pair<String, Boolean>> dialog = new Dialog<>();
            dialog.setTitle("Create a chatroom");
            dialog.setHeaderText("You can create a chatroom, if it doesn't already exist.");

            ButtonType btnCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnCreate, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField roomName = new TextField();
            roomName.setPromptText("chatroom-name");
            CheckBox isPrivate = new CheckBox();

            grid.add(new Label("Chatroom name:"), 0, 0);
            grid.add(roomName, 1, 0);
            grid.add(new Label("Private:"), 0, 1);
            grid.add(isPrivate,1,1);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username filed by default
            Platform.runLater(roomName::requestFocus);

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnCreate) {
                    return new Pair<>(roomName.getText(), isPrivate.isSelected());
                }
                return null;
            });

            Optional<Pair<String, Boolean>> result = dialog.showAndWait();

            result.ifPresent(r -> {
                boolean success = model.createChatroom(r.getKey(), r.getValue());

                // todo: introduce a general status field in the GUI for being able to post success-messages (maybe even
                //  the logger messages themselves, for less overhead in the code!
            });
        });



        // for development:
        view.btnSendCommand.setOnAction(event -> {
            logger.info("Sending command: " + view.txtCommand.getText());
            String answer = model.sendDirectCommand(view.txtCommand.getText());
            view.lastServerAnswerContent.setText(answer);
        });


        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            Platform.exit();
        });

        logger.info("Application controller initialized");
    }


}
