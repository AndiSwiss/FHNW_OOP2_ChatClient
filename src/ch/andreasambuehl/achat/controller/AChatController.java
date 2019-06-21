package ch.andreasambuehl.achat.controller;

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

        //======================================//
        // register to listen for button clicks //
        //======================================//

        //---------------//
        // menu section: //
        //---------------//
        // Language:
        view.menuFileLanguage.getItems().forEach(menuItem -> {
            String language = menuItem.getText();
            menuItem.setOnAction(event -> {
                serviceLocator.getConfiguration().setLocalOption("Language", language);
                serviceLocator.setTranslator(new Translator(language));
                view.updateTexts();

                // in order for the dialogs to also update the language:
                t = new Translator(language);

                // also set some status in the new language according to the current state of the system:
                if (model.getServerConnected()) {
                    view.lblStatusServer.setText(t.getString("label.connection.status.connected"));
                } else {
                    view.lblStatusServer.setText(t.getString("label.connection.status.notConnected"));
                }

                if (model.getToken() != null) {
                    view.lblStatusAccount.setText(t.getString("label.account.status.loggedIn"));
                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));
                }

                // for the general status field, just clear it:
                view.lblLastStatus.setText("");
            });
        });

        view.menuDev.setOnAction(event -> {
            if (view.showDevSection) {
                view.devSection.setVisible(false);
                view.showDevSection = false;
                view.menuDev.setText(t.getString("program.menu.devShow"));
            } else {
                view.devSection.setVisible(true);
                view.showDevSection = true;
                view.menuDev.setText(t.getString("program.menu.devHide"));
            }
        });


        // todo: create about-dialog
//        view.menuAbout.setOnAction(event -> );


        //---------------------//
        // connection section: //
        //---------------------//
        view.btnConnectDisconnect.setOnAction(event -> {
            if (model.getServerConnected()) {
                if (model.getToken() != null) {
                    model.logout();
                    view.btnSignInSignOut.setText(t.getString("button.account.signIn"));
                    view.lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));
                    view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblStatusAccount.getStyleClass().add("alert");
                    view.btnCreateLogin.setDisable(false);
                }
                model.disconnectServer();
                view.btnConnectDisconnect.setText(t.getString("button.connect"));
                view.btnPingServer.setDisable(true);
                view.lblStatusServer.setText(t.getString("label.connection.status.notConnected"));
                view.lblStatusServer.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblStatusServer.getStyleClass().add("alert");
            } else {
                String ipAddress = view.txtServer.getText();
                String portString = view.txtPort.getText();
                boolean useSSL = view.chkboxSSL.isSelected();

                boolean successful = model.connectServer(ipAddress, portString, useSSL);
                if (successful) {
                    view.btnConnectDisconnect.setText(t.getString("button.disconnect"));
                    view.btnPingServer.setDisable(false);
                    view.lblStatusServer.setText(t.getString("label.connection.status.connected"));
                    view.lblStatusServer.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblStatusServer.getStyleClass().add("ok");
                }
            }
        });

        view.btnPingServer.setOnAction(event -> {
            boolean success = model.pingServer();
            if (success) {
                view.lblStatusServer.setText(t.getString("label.connection.status.pingSuccess"));
                view.lblStatusServer.getStyleClass().removeIf(style -> style.equals("alert"));
                view.lblStatusServer.getStyleClass().add("ok");
            } else {
                view.lblStatusServer.setText(t.getString("label.connection.status.pingFailed"));
                view.lblStatusServer.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblStatusServer.getStyleClass().add("alert");
            }
        });


        //------------------//
        // account section: //
        //------------------//
        view.btnSignInSignOut.setOnAction(event -> {
            if (model.getToken() == null) {
                String name = view.txtUsername.getText();
                String password = view.txtPassword.getText();
                boolean successful = model.login(name, password);

                if (successful) {
                    view.btnSignInSignOut.setText(t.getString("button.account.signOut"));
                    view.lblStatusAccount.setText(t.getString("label.account.status.loggedIn"));
                    view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblStatusAccount.getStyleClass().add("ok");
                    view.btnCreateLogin.setDisable(true);
                    model.listChatrooms();
                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.loginFailed"));
                    view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblStatusAccount.getStyleClass().add("alert");
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
                view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblStatusAccount.getStyleClass().add("alert");
            }
        });

        view.btnCreateLogin.setOnAction(event -> {
            String name = view.txtUsername.getText();
            String password = view.txtPassword.getText();
            boolean successful = model.createLogin(name, password);
            if (successful) {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountCreated"));
                view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("alert"));
                view.lblStatusAccount.getStyleClass().add("ok");
            } else {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountCreationFailed"));
                view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblStatusAccount.getStyleClass().add("alert");
            }
        });

        view.btnDeleteLogin.setOnAction(event -> {
            // Login if not already logged in:
            boolean loginSuccess = true;
            if (model.getToken() == null) {
                String name = view.txtUsername.getText();
                String password = view.txtPassword.getText();
                loginSuccess = model.login(name, password);
            }
            if (loginSuccess) {
                boolean deleteSuccess = model.deleteLogin();
                if (deleteSuccess) {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountDeleted"));
                    view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblStatusAccount.getStyleClass().add("ok");
                    view.btnSignInSignOut.setText(t.getString("button.account.signIn"));
                    view.btnCreateLogin.setDisable(false);

                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountDeletionFailed"));
                    view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblStatusAccount.getStyleClass().add("alert");
                }
            } else {
                view.lblStatusAccount.setText(t.getString("label.account.status.accountDeletionNotFound"));
                view.lblStatusAccount.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblStatusAccount.getStyleClass().add("alert");
            }
        });


        //-------------------//
        // chatroom section: //
        //-------------------//
        view.btnUpdateChatroomsList.setOnAction(event -> {
            boolean success = model.listChatrooms();
            if (success) {
                view.lblLastStatus.setText(t.getString("label.status.updateChatroomsList"));
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                view.lblLastStatus.getStyleClass().add("ok");
            } else {
                view.lblLastStatus.setText(t.getString("label.status.updateChatroomsListFailed"));
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblLastStatus.getStyleClass().add("alert");

            }
        });

        view.btnCreateChatroom.setOnAction(event -> {
            // open a dialog
            // adapted code from https://code.makery.ch/blog/javafx-dialogs-official/ -> section "Custom Login Dialog"
            Dialog<Pair<String, Boolean>> dialog = new Dialog<>();

            dialog.setTitle(t.getString("dialog.createChatroom.title"));
            dialog.setHeaderText(t.getString("dialog.createChatroom.header"));

            String buttonText = t.getString("dialog.createChatroom.btnCreate");
            ButtonType btnCreate = new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnCreate, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField roomName = new TextField();
            CheckBox isPublic = new CheckBox();
            isPublic.setSelected(true);

            grid.add(new Label(t.getString("dialog.createChatroom.name")), 0, 0);
            grid.add(roomName, 1, 0);
            grid.add(new Label(t.getString("dialog.createChatroom.public")), 0, 1);
            grid.add(isPublic, 1, 1);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username filed by default
            Platform.runLater(roomName::requestFocus);

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnCreate) {
                    return new Pair<>(roomName.getText(), isPublic.isSelected());
                }
                return null;
            });

            Optional<Pair<String, Boolean>> result = dialog.showAndWait();

            result.ifPresent(r -> {
                boolean success = model.createChatroom(r.getKey(), r.getValue());
                model.listChatrooms();
                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.createChatroomSuccess"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblLastStatus.getStyleClass().add("ok");
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.createChatroomFailed"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblLastStatus.getStyleClass().add("failed");
                }

            });
        });

        view.btnDeleteChatroom.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(t.getString("dialog.deleteChatroom.title"));
            dialog.setHeaderText(t.getString("dialog.deleteChatroom.header"));
            dialog.setContentText(t.getString("dialog.deleteChatroom.content"));
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                boolean success = model.deleteChatroom(name);
                model.listChatrooms();

                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.deleteChatroomSuccess"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblLastStatus.getStyleClass().add("ok");
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.deleteChatroomFailed"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblLastStatus.getStyleClass().add("alert");
                }
            });
        });

        view.btnJoinSelectedChatroom.setOnAction(event -> {
            String selectedItem = view.chatroomsList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                boolean success = model.joinChatroom(selectedItem, view.txtUsername.getText());

                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.joinChatSuccess"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblLastStatus.getStyleClass().add("ok");
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.joinChatFailed"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblLastStatus.getStyleClass().add("alert");
                }
            } else {
                view.lblLastStatus.setText(t.getString("label.status.noChatroomSelected"));
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblLastStatus.getStyleClass().add("alert");
            }
        });

        view.btnLeaveSelectedChatroom.setOnAction(event -> {
            String selectedItem = view.chatroomsList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                boolean success = model.leaveChatroom(selectedItem, view.txtUsername.getText());

                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.leaveChatSuccess"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                    view.lblLastStatus.getStyleClass().add("ok");
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.leaveChatFailed"));
                    view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                    view.lblLastStatus.getStyleClass().add("alert");
                }
            } else {
                view.lblLastStatus.setText(t.getString("label.status.noChatroomSelected"));
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblLastStatus.getStyleClass().add("alert");
            }
        });


        //---------------//
        // chat section: //
        //---------------//
        view.btnSendToSelectedChatroom.setOnAction(event -> {
            String answer = sendChatMessage(
                    view.chatroomsList.getSelectionModel().getSelectedItem(),
                    view.txtSendChat.getText()
            );
            if (answer.equals("noBroadcast")) {
                // this happens when sending a message to a public/private chatroom of which I'm not a member and when
                // sending a message directly to another person
                view.lblLastStatus.setText(t.getString("label.status.chatMessageNotMember")
                        + view.chatroomsList.getSelectionModel().getSelectedItem()
                );
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblLastStatus.getStyleClass().add("alert");
            }
        });


        //--------------//
        // dev section: //
        //--------------//
        view.btnSendCommand.setOnAction(event -> {
            logger.info("Sending command: " + view.txtCommand.getText());
            String answer = model.sendDirectCommand(view.txtCommand.getText());
            view.lblLastServerAnswerContent.setText(answer);
        });


        //=================================//
        // register listeners and actions: //
        //=================================//
        // set visibility of objects depending whether the connection of the server is active
        model.serverConnectedProperty().addListener((observable, oldValue, newValue) -> toggleAccountSection(newValue));

        model.tokenProperty().addListener((observable, oldValue, newValue) -> {
            boolean loggedIn = newValue != null;
            view.chatroomSection.setVisible(loggedIn);
            view.chatSection.setVisible(loggedIn);
        });

        model.serverConnectionFailedProperty().addListener((observable, oldValue, newValue) -> {
             view.lblStatusServer.setText(t.getString("label.connection.status.connectionFailed"));
             model.setServerConnectionFailed(false);
        });

        // set initial visibility:
        toggleAccountSection(false);
        view.chatroomSection.setVisible(false);
        view.chatSection.setVisible(false);

        if (serviceLocator.getConfiguration().getOption("showDevSection") != null
                && serviceLocator.getConfiguration().getOption("showDevSection").equals("true")) {
            view.showDevSection = true;
            view.devSection.setVisible(true);
            view.menuDev.setText(t.getString("program.menu.devHide"));
        } else {
            view.showDevSection = false;
            view.devSection.setVisible(false);
        }


        //========//
        // other: //
        //========//
        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            Platform.exit();
        });

        logger.info("Application controller initialized");
    }

    /**
     * Forwards a message to the model and updates the status-messages:
     *
     * @param target  chatroom or person
     * @param message message
     */
    private String sendChatMessage(String target, String message) {

        // only send message if it contains something:
        if (message.length() == 0) {
            return "no message sent";
        } else {
            String success = model.sendChatMessage(target, message);

            if (success.equals("success")) {
                view.lblLastStatus.setText(t.getString("label.status.chatMessageSent") + target);
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("alert"));
                view.lblLastStatus.getStyleClass().add("ok");
            } else if (success.equals("noBroadcast")) {
                // this happens when sending a message to a public/private chatroom of which I'm not a member and when
                // sending a message directly to another person
                // just forward the message to the caller -> individual handling!
                return success;
            } else {
                view.lblLastStatus.setText(t.getString("label.status.chatMessageSendingFailed"));
                view.lblLastStatus.getStyleClass().removeIf(style -> style.equals("ok"));
                view.lblLastStatus.getStyleClass().add("alert");
            }
            return success;
        }
    }


    /**
     * Toggles 'disabled' for the elements in the account section
     *
     * @param enable enable/disable
     */
    private void toggleAccountSection(boolean enable) {
        view.lblAccountSection.setDisable(!enable);
        view.lblUsername.setDisable(!enable);
        view.txtUsername.setDisable(!enable);
        view.lblPassword.setDisable(!enable);
        view.txtPassword.setDisable(!enable);
        view.btnSignInSignOut.setDisable(!enable);
        view.btnCreateLogin.setDisable(!enable);
        view.btnDeleteLogin.setDisable(!enable);
        view.lblStatusAccount.setDisable(!enable);
    }


}
