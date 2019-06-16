package ch.andreasambuehl.chatclient.controller;

import ch.andreasambuehl.chatclient.abstractClasses.Controller;
import ch.andreasambuehl.chatclient.common.ServiceLocator;
import ch.andreasambuehl.chatclient.model.ChatClientModel;
import ch.andreasambuehl.chatclient.view.ChatClientView;
import javafx.application.Platform;

/**
 * This is the main controller for the chat client.
 */
public class ChatClientController extends Controller<ChatClientModel, ChatClientView> {
    private ServiceLocator serviceLocator;

    public ChatClientController(ChatClientModel model, ChatClientView view) {
        super(model, view);

        // register to listen for button clicks
        // todo: replace this dummy test with actual stuff!
        view.btnClick.setOnAction(event1 -> incrementOnClick());

        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> Platform.exit());

        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Application controller initialized");
    }

    private void incrementOnClick() {
        // todo: replace this dummy test with actual stuff!
        model.incrementValue();
        String newText = Integer.toString(model.getValue());
        view.lblNumber.setText(newText);
    }
}
