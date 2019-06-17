package ch.andreasambuehl.chatclient.view;

import ch.andreasambuehl.chatclient.abstractClasses.View;
import ch.andreasambuehl.chatclient.common.ServiceLocator;
import ch.andreasambuehl.chatclient.common.Translator;
import ch.andreasambuehl.chatclient.model.ChatClientModel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the main view for the chat client.
 */
public class ChatClientView extends View<ChatClientModel> {
    private Menu menuFile;
    private Menu menuFileLanguage;
    private Menu menuHelp;

    public Label lblNumber;
    public Button btnClick;

    public ChatClientView(Stage stage, ChatClientModel model) {
        super(stage, model);
        ServiceLocator.getServiceLocator().getLogger().info("Application view initialized");
    }

    @Override
    protected Scene create_GUI() {
        ServiceLocator sl = ServiceLocator.getServiceLocator();
        Logger logger = sl.getLogger();

        MenuBar menuBar = new MenuBar();
        menuFile = new Menu();
        menuFileLanguage = new Menu();
        menuFile.getItems().add(menuFileLanguage);

        for (Locale locale : sl.getLocales()) {
            MenuItem language = new MenuItem(locale.getLanguage());
            menuFileLanguage.getItems().add(language);

            // todo: I think that the following code should be moved to the controller!
            language.setOnAction(event -> {
                sl.getConfiguration().setLocalOption("Language", locale.getLanguage());
                sl.setTranslator(new Translator(locale.getLanguage()));
                updateTexts();
            });
        }

        menuHelp = new Menu();
        // todo: Implement a Help-File or at least a small about pop-up or something!

        menuBar.getMenus().addAll(menuFile, menuHelp);

        GridPane root = new GridPane();
        root.add(menuBar, 0, 0);

        lblNumber = new Label();
        lblNumber.setText(Integer.toString(model.getValue()));
        lblNumber.setMinWidth(200);
        lblNumber.setAlignment(Pos.BASELINE_CENTER);
        root.add(lblNumber, 0, 1);

        btnClick = new Button();
        btnClick.setMinWidth(200);
        root.add(btnClick, 0, 2);

        updateTexts();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/ChatClient.css").toExternalForm());

        return scene;
    }

    protected void updateTexts() {
        Translator t = ServiceLocator.getServiceLocator().getTranslator();

        // The menu entries
        menuFile.setText(t.getString("program.menu.file"));
        menuFileLanguage.setText(t.getString("program.menu.file.language"));
        menuHelp.setText(t.getString("program.menu.help"));

        // Other controls
        btnClick.setText(t.getString("button.clickMe"));

        stage.setTitle(t.getString("program.name"));
    }
}
