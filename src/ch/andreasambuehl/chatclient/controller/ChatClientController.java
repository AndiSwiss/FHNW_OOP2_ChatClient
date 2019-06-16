package ch.andreasambuehl.chatclient.controller;

import ch.andreasambuehl.chatclient.model.ChatClientModel;
import ch.andreasambuehl.chatclient.view.ChatClientView;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class ChatClientController {
    final private ChatClientModel model;
    final private ChatClientView view;

    public ChatClientController(ChatClientModel model, ChatClientView view) {
        this.model = model;
        this.view = view;

        // register to listen for button clicks
        view.btnClick.setOnAction(this::clickAndIncrement);

        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> {
            view.stop();
            Platform.exit();
        });
    }

    private void clickAndIncrement(ActionEvent event) {
        model.incrementValue();
        String newText = Integer.toString(model.getValue());
        view.lblNumber.setText(newText);
    }
}
