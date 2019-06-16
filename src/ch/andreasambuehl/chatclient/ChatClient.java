package ch.andreasambuehl.chatclient;

import ch.andreasambuehl.chatclient.controller.ChatClientController;
import ch.andreasambuehl.chatclient.model.ChatClientModel;
import ch.andreasambuehl.chatclient.view.ChatClientView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This is the main starting point of the whole chat client.
 * Further information about this project is provided in the README.md
 *
 * @author Andreas Ambühl (with code fragments by Prof. Dr. Brad Richards)
 * (particularly: this application is built on the "JavaFX_App_Template v2" by
 * Prof. Dr. Brad Richards, then edited and adopted by Andreas Ambühl)
 *
 * <p>
 * @version 0.1e
 *
 * <p>
 * Copyright 2019, Andreas Ambühl. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 *
 * <p>
 * This copyright is also applicable for all the code found inside this folder or its sub-folders,
 * unless there is another copyright-info on the specific file.
 */
public class ChatClient extends Application {
    private ChatClientView view;
    private ChatClientController controller;
    private ChatClientModel model;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        model = new ChatClientModel();
        view = new ChatClientView(primaryStage, model);
        controller = new ChatClientController(model, view);
        view.start();
    }

    @Override
    public void stop() throws Exception {
        if (view != null) {
            view.stop();
        }
    }
}
