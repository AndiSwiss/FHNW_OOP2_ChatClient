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


        // register to listen for button clicks

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
            });
        });


        //---------------------//
        // connection section: //
        //---------------------//
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


        //------------------//
        // account section: //
        //------------------//
        view.btnSignInSignOut.setOnAction(event -> {
            if (!AChatModel.isServerConnected.get()) {
                view.lblStatusAccount.setText(t.getString("label.account.status.noConnectionYet"));
            } else if (AChatModel.getToken() == null) {
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
            if (!AChatModel.isServerConnected.get()) {
                view.lblStatusAccount.setText(t.getString("label.account.status.noConnectionYet"));
            } else {
                String name = view.txtUsername.getText();
                String password = view.txtPassword.getText();
                boolean successful = model.createLogin(name, password);

                if (successful) {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountCreated"));
                } else {
                    view.lblStatusAccount.setText(t.getString("label.account.status.accountCreationFailed"));
                }
            }
        });

        view.btnDeleteLogin.setOnAction(event -> {
            if (!AChatModel.isServerConnected.get()) {
                view.lblStatusAccount.setText(t.getString("label.account.status.noConnectionYet"));
            } else {
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
            }
        });


        //-------------------//
        // chatroom section: //
        //-------------------//
        view.btnUpdateChatroomsList.setOnAction(event -> {
            boolean success = model.listChatrooms();
            if (success) {
                view.lblLastStatus.setText(t.getString("label.status.updateChatroomsList"));
            } else {
                view.lblLastStatus.setText(t.getString("label.status.updateChatroomsListFailed"));
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
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.createChatroomFailed"));
                }

            });
        });

        view.btnDeleteChatroom.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            // todo: if the translation changes during runtime, somehow the translation only get's changed after
            //  restarting the app (this seems not really logical at this point)
            //  -> try to find a fix
            dialog.setTitle(t.getString("dialog.deleteChatroom.title"));
            dialog.setHeaderText(t.getString("dialog.deleteChatroom.header"));
            dialog.setContentText(t.getString("dialog.deleteChatroom.content"));
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                boolean success = model.deleteChatroom(name);
                model.listChatrooms();

                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.deleteChatroomSuccess"));
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.deleteChatroomFailed"));
                }
            });
        });

        view.btnJoinSelectedChatroom.setOnAction(event -> {
            String selectedItem = view.chatroomsList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                boolean success = model.joinChatroom(selectedItem, view.txtUsername.getText());

                if (success) {
                    view.lblLastStatus.setText(t.getString("label.status.joinChatSuccess"));
                } else {
                    view.lblLastStatus.setText(t.getString("label.status.joinChatFailed"));
                }
            } else {
                view.lblLastStatus.setText(t.getString("label.status.noChatroomSelected"));
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


        //--------//
        // other: //
        //--------//
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
        String success = model.sendChatMessage(target, message);

        if (success.equals("success")) {
            view.lblLastStatus.setText(t.getString("label.status.chatMessageSent") + target);
        } else if (success.equals("noBroadcast")) {
            // this happens when sending a message to a public/private chatroom of which I'm not a member and when
            // sending a message directly to another person
            // just forward the message to the caller -> individual handling!
            return success;
        } else {
            view.lblLastStatus.setText(t.getString("label.status.chatMessageSendingFailed"));
        }

        return success;
    }
}
