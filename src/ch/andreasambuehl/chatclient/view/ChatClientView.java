package ch.andreasambuehl.chatclient.view;

import ch.andreasambuehl.chatclient.model.ChatClientModel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChatClientView {
    private Stage stage;
    private ChatClientModel model;
    public Label lblNumber;
    public Button btnClick;

    public ChatClientView(Stage stage, ChatClientModel model) {
        this.stage = stage;
        this.model = model;
        stage.setTitle("Chat Client");

        GridPane pane = new GridPane();
        lblNumber = new Label();
        lblNumber.setText(Integer.toString(model.getValue()));
        pane.add(lblNumber, 0, 0);

        btnClick = new Button();
        btnClick.setText("Click Me!");
        pane.add(btnClick, 0, 1);

        Scene scene = new Scene(pane);

        // todo: implement CSS:
//        scene.getStylesheets().add(getClass().getResource("ChatClient.css").toExternalForm());
        stage.setScene(scene);
    }

    public void start() {
        stage.show();
    }

    public void stop() {
        stage.hide();
    }

    public Stage getStage() {
        return stage;
    }
}
