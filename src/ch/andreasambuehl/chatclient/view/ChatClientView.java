package ch.andreasambuehl.chatclient.view;

import ch.andreasambuehl.chatclient.abstractClasses.View;
import ch.andreasambuehl.chatclient.model.ChatClientModel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChatClientView extends View<ChatClientModel> {
    // todo: create menus

    private Stage stage;
    private ChatClientModel model;
    public Label lblNumber;
    public Button btnClick;

    public ChatClientView(Stage stage, ChatClientModel model) {
        super(stage, model);

        // todo: ServiceLocator
    }

    @Override
    protected Scene create_GUI() {
        // todo: ServiceLocator
        // todo: Logger

        // todo: rest of the method!!!

        stage.setTitle("Chat Client");

        GridPane pane = new GridPane();
        lblNumber = new Label();
        lblNumber.setText(Integer.toString(model.getValue()));
        pane.add(lblNumber, 0, 0);

        btnClick = new Button();
        btnClick.setText("Click Me!");
        pane.add(btnClick, 0, 1);

        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("css/ChatClient.css").toExternalForm());

        return scene;
    }
}
