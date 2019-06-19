package ch.andreasambuehl.achat.view;

import ch.andreasambuehl.achat.abstractClasses.View;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    public TextField txtServer;
    private Label lblPort;
    public TextField txtPort;
    public CheckBox chkboxSSL;
    public Button btnConnectDisconnect;
    private Label lblStatus;
    public Label lblStatusCurrent;

    // accountSection
    private Label lblAccountSection;
    private Label lblUsername;
    public TextField txtUsername;
    private Label lblPassword;
    public TextField txtPassword;
    public Button btnSignInSignOut;
    public Button btnCreateLogin;

    // left section:
    private Label lblBuddiesSection;
    private Label lblChatroomsSection;

    // center section:
    private Label lblChatSection;

    // bottom section:
    private Label lblCommand;
    public TextField txtCommand;
    public Button btnSendCommand;
    private Label lblServerAnswers;
    public ListView listServerAnswers;


    /**
     * Constructor of the view
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
        // todo: instead of providing the initial server-value here, I should read it from the config-file!
        txtServer = new TextField("147.86.8.31");
        lblPort = new Label();
        txtPort = new TextField("50001");
        chkboxSSL = new CheckBox();
        chkboxSSL.setDisable(true);

        btnConnectDisconnect = new Button();
        lblStatus = new Label();
        lblStatusCurrent = new Label();
        HBox connection1 = new HBox();
        connection1.getChildren().addAll(lblServer, txtServer, lblPort, txtPort, chkboxSSL);
        HBox connection2 = new HBox();
        connection2.getChildren().addAll(btnConnectDisconnect, lblStatus, lblStatusCurrent);

        // todo: optimize CSS-Styling!!
        connection2.setId("specialHBox");

        VBox connectionVBox = new VBox();
        connectionVBox.getChildren().addAll(lblConnectionSection, connection1, connection2);

        // for accessing specific CSS for a boxedSection:
        connectionVBox.getStyleClass().add("boxedSection");


        // account section
        lblAccountSection = new Label();

        lblUsername = new Label();
        txtUsername = new TextField();
        lblPassword = new Label();
        txtPassword = new TextField();
        btnSignInSignOut = new Button();
        btnCreateLogin = new Button();

        HBox account1 = new HBox();
        account1.getChildren().addAll(lblUsername, txtUsername, lblPassword, txtPassword);
        HBox account2 = new HBox();
        account2.getChildren().addAll(btnSignInSignOut, btnCreateLogin);

        // todo: optimize CSS-Styling!!
        account2.setId("specialHBox");

        VBox accountVBox = new VBox();
        accountVBox.getChildren().addAll(lblAccountSection, account1, account2);
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

        VBox centerSection = new VBox();
        centerSection.getChildren().addAll(lblChatSection);
        centerSection.getStyleClass().add("boxedSection");
        root.setCenter(centerSection);

        //-----------------//
        // bottom section: //
        //-----------------//
        lblCommand = new Label("Command:");
        txtCommand = new TextField();
        btnSendCommand = new Button("Send");
        lblServerAnswers = new Label("Server Answers:");
        listServerAnswers = new ListView();

        GridPane bottomGrid = new GridPane();
        bottomGrid.add(lblServerAnswers, 0, 0);
        bottomGrid.add(listServerAnswers, 1, 0);
        bottomGrid.add(lblCommand, 0, 1);

        HBox commandAndSend = new HBox();
        commandAndSend.getChildren().addAll(txtCommand, btnSendCommand);
        bottomGrid.add(commandAndSend, 1, 1);
        bottomGrid.getStyleClass().addAll("boxedSection");
        root.setBottom(bottomGrid);


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
        lblPort.setText(t.getString("label.connection.port"));

        if (AChatModel.isServerConnected.get()) {
            btnConnectDisconnect.setText(t.getString("button.disconnect"));
        } else {
            btnConnectDisconnect.setText(t.getString("button.connect"));
        }
        chkboxSSL.setText(t.getString("label.connection.useSSL"));
        lblStatus.setText(t.getString("label.connection.status"));

        // todo: make this field to update the text correspondingly -> with accessing a variable where the current
        //  connection status is saved:
        lblStatusCurrent.setText(t.getString("label.connection.status-failed"));

        // account section
        lblAccountSection.setText(t.getString("label.account"));
        lblUsername.setText(t.getString("label.account.username"));
        lblPassword.setText(t.getString("label.account.password"));
        // todo: make this button to update to signIn/SignOut
        btnSignInSignOut.setText(t.getString("button.account.signIn"));
        btnCreateLogin.setText(t.getString("button.account.createLogin"));

        // left section
        lblBuddiesSection.setText(t.getString("label.buddies"));
        lblChatroomsSection.setText(t.getString("label.chatrooms"));

        // center section
        lblChatSection.setText(t.getString("label.chat"));

        stage.setTitle(t.getString("program.name"));
    }
}
