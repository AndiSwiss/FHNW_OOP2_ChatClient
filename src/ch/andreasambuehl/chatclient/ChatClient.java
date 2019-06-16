package ch.andreasambuehl.chatclient;

import ch.andreasambuehl.chatclient.controller.ChatClientController;
import ch.andreasambuehl.chatclient.model.ChatClientModel;
import ch.andreasambuehl.chatclient.view.ChatClientView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This is the main class of the ChatClient (= entry-point of the application).
 * Further information is provided in the README.md
 *
 * @author Andreas Ambühl (with code fragments by Prof. Dr. Brad Richards)
 * (particularly: this application is built on the "JavaFX_App_Template v2" by
 * Prof. Dr. Brad Richards, then edited and adopted by Andreas Ambühl)
 *
 * @version 0.1d
 *
 * Copyright -> see LICENSE.txt-file.
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
