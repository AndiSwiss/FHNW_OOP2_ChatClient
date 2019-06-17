package ch.andreasambuehl.achat.view;

import ch.andreasambuehl.achat.abstractClasses.View;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the main view for the chat client AChat.
 */
public class AChatView extends View<AChatModel> {
    //--------------//
    // top section: //
    //--------------//
    // menu
    private Menu menuFile;
    private Menu menuFileLanguage;
    private Menu menuHelp;
    private MenuItem menuAbout;

    // connectionSection
    private Label lblConnectionSection;
    private Label lblServer;
    private TextField txtServer;
    private Label lblPort;
    private TextField txtPort;
    private CheckBox chkboxSSL;
    private Button btnConnect;
    private Button btnDisconnect;
    private Label lblStatus;
    private Label lblStatusCurrent;


    private Label lblAccountSection;

    // left section:
    private Label lblBuddiesSection;
    private Label lblChatroomsSection;

    // center section:
    private Label lblChatSection;

    // todo: remove testing-objects
    public Label lblNumber;
    public Button btnClick;


    /**
     * Constructor
     *
     * @param stage stage
     * @param model model
     */
    public AChatView(Stage stage, AChatModel model) {
        super(stage, model);
        ServiceLocator.getServiceLocator().getLogger().info("Application view initialized");
    }


    /**
     * Creates the GUI
     *
     * @return Scene
     */
    @Override
    protected Scene create_GUI() {
        ServiceLocator sl = ServiceLocator.getServiceLocator();
        Logger logger = sl.getLogger();

        // basic layout: BorderPane:
        BorderPane root = new BorderPane();

        //--------------//
        // top section: //
        //--------------//
        // MenuBar
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
        menuAbout = new MenuItem();
        menuHelp.getItems().add(menuAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);

        // connection section
        lblConnectionSection = new Label();
        lblServer = new Label();
        txtServer = new TextField();
        lblPort = new Label();
        txtPort = new TextField();
        chkboxSSL = new CheckBox();

        btnConnect = new Button();
        btnDisconnect = new Button();
        lblStatus = new Label();
        lblStatusCurrent = new Label();
        HBox connection1 = new HBox();
        connection1.getChildren().addAll(lblServer, txtServer, lblPort, txtPort, chkboxSSL);
        HBox connection2 = new HBox();
        connection2.getChildren().addAll(btnConnect, btnDisconnect, lblStatus, lblStatusCurrent);

        connection2.setId("specialHBox");

        VBox connectionVBox = new VBox();
        connectionVBox.getChildren().addAll(lblConnectionSection, connection1, connection2);

        // for accessing specific CSS for a boxedSection:
        connectionVBox.getStyleClass().add("boxedSection");


        // account section
        lblAccountSection = new Label();
        VBox accountVBox = new VBox();
        accountVBox.getChildren().addAll(lblAccountSection);
        accountVBox.getStyleClass().add("boxedSection");

        VBox topSection = new VBox();
        topSection.getChildren().addAll(menuBar, connectionVBox, accountVBox);

        root.setTop(topSection);

        //---------------//
        // left section: //
        //---------------//
        // buddies section
        lblBuddiesSection = new Label();
        VBox buddiesVBox = new VBox();

        String[] exampleBuddiesList = {"Hanna", "Dario", "Luca"};
        ObservableList observableExampleBuddiesList = FXCollections.<String>observableArrayList(Arrays.asList(exampleBuddiesList));
        ListView<String> buddiesList = new ListView<String>(observableExampleBuddiesList);
        buddiesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        buddiesVBox.getChildren().addAll(lblBuddiesSection, buddiesList);
        buddiesVBox.getStyleClass().add("boxedSection");

        // chatrooms section
        lblChatroomsSection = new Label();
        VBox chatroomsVBox = new VBox();

        String[] exampleChatroomsList = {"CatChat", "oop2"};
        ObservableList observableExampleChatroomsList = FXCollections.<String>observableArrayList(Arrays.asList(exampleChatroomsList));
        ListView<String> chatroomsList = new ListView<String>(observableExampleChatroomsList);
        chatroomsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        chatroomsVBox.getChildren().addAll(lblChatroomsSection, chatroomsList);
        chatroomsVBox.getStyleClass().add("boxedSection");


        VBox leftSection = new VBox();
        leftSection.getChildren().addAll(buddiesVBox, chatroomsVBox);
        root.setLeft(leftSection);


        //-----------------//
        // center section: //
        //-----------------//
        lblChatSection = new Label();
        lblNumber = new Label();
        lblNumber.setText(Integer.toString(model.getValue()));
        lblNumber.setMinWidth(200);
        lblNumber.setAlignment(Pos.BASELINE_CENTER);

        btnClick = new Button();
        btnClick.setMinWidth(200);

        VBox centerSection = new VBox();
        centerSection.getChildren().addAll(lblChatSection, lblNumber, btnClick);
        centerSection.getStyleClass().add("boxedSection");
        root.setCenter(centerSection);

        updateTexts();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/aChat.css").toExternalForm());

        return scene;
    }

    /**
     * Updates all texts with the chosen translation
     */
    protected void updateTexts() {
        Translator t = ServiceLocator.getServiceLocator().getTranslator();

        // top section
        // the menu entries
        menuFile.setText(t.getString("program.menu.file"));
        menuFileLanguage.setText(t.getString("program.menu.file.language"));
        menuHelp.setText(t.getString("program.menu.help"));
        menuAbout.setText(t.getString("program.menu.help.about"));

        // connection section
        lblConnectionSection.setText(t.getString("label.connection"));
        lblServer.setText(t.getString("label.connection.server"));
        txtServer.setText(t.getString("txt.connection.server"));
        lblPort.setText(t.getString("label.connection.port"));
        btnConnect.setText(t.getString("button.connect"));
        btnDisconnect.setText(t.getString("button.disconnect"));
        chkboxSSL.setText(t.getString("label.connection.useSSL"));
        lblStatus.setText(t.getString("label.connection.status"));

        // todo: make this field to update the text correspondingly -> with accessing a variable where the current
        //  connection status is saved:
        lblStatusCurrent.setText(t.getString("label.connection.status-failed"));

        lblAccountSection.setText(t.getString("label.account"));

        // left section
        lblBuddiesSection.setText(t.getString("label.buddies"));
        lblChatroomsSection.setText(t.getString("label.chatrooms"));

        // center section
        lblChatSection.setText(t.getString("label.chat"));

        // Other controls
        btnClick.setText(t.getString("button.clickMe"));

        stage.setTitle(t.getString("program.name"));
    }
}
