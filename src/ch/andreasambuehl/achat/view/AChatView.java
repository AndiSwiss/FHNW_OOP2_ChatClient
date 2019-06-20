package ch.andreasambuehl.achat.view;

import ch.andreasambuehl.achat.abstractClasses.View;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    public Menu menuFileLanguage;
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
    public Button btnPingServer;
    public Label lblStatusServer;

    // accountSection
    private Label lblAccountSection;
    private Label lblUsername;
    public TextField txtUsername;
    private Label lblPassword;
    public PasswordField txtPassword;
    public Button btnSignInSignOut;
    public Button btnCreateLogin;
    public Button btnDeleteLogin;
    public Label lblStatusAccount;

    // left section:
    private Label lblPeopleSection;
    private Label lblChatroomsSection;
    public Button btnUpdateChatroomsList;
    public Button btnCreateChatroom;
    public Button btnDeleteChatroom;
    public Button btnJoinSelectedChatroom;
    public Button btnJoinPrivateChatroom;
    public Label lblPublicChatrooms;
    public ListView<String> chatroomsList;


    // center section:
    private Label lblChatSection;

    // bottom section:
    // status section:
    private Label lblStatusSection;
    public Label lblLastStatus;

    // dev section:
    private Label lblDevSection;
    private Label lblCommand;
    public TextField txtCommand;
    public Button btnSendCommand;
    private Label lblServerAnswer;
    public Label lastServerAnswerContent;


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
        }

        menuHelp = new Menu();
        menuAbout = new MenuItem();
        menuHelp.getItems().add(menuAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);

        // connection section
        lblConnectionSection = new Label();
        lblConnectionSection.setId("labelSmall");
        lblServer = new Label();
        txtServer = new TextField(sl.getConfiguration().getOption("ServerIP"));
        lblPort = new Label();
        txtPort = new TextField(sl.getConfiguration().getOption("ServerPort"));
        chkboxSSL = new CheckBox();
        chkboxSSL.setDisable(true);

        btnConnectDisconnect = new Button();
        btnPingServer = new Button();
        btnPingServer.setDisable(true);
        lblStatusServer = new Label();
        HBox connection1 = new HBox();
        connection1.getChildren().addAll(lblServer, txtServer, lblPort, txtPort, chkboxSSL);
        HBox connection2 = new HBox();
        connection2.getChildren().addAll(btnConnectDisconnect, btnPingServer, lblStatusServer);

        // todo: optimize CSS-Styling!!
        connection2.setId("specialHBox");

        VBox connectionVBox = new VBox();
        connectionVBox.getChildren().addAll(lblConnectionSection, connection1, connection2);

        // for accessing specific CSS for a boxedSection:
        connectionVBox.getStyleClass().add("boxedSection");


        // account section
        lblAccountSection = new Label();
        lblAccountSection.setId("labelSmall");
        lblUsername = new Label();
        txtUsername = new TextField(sl.getConfiguration().getOption("Username"));
        lblPassword = new Label();
        txtPassword = new PasswordField();
        txtPassword.setText(sl.getConfiguration().getOption("Password"));
        btnSignInSignOut = new Button();
        btnCreateLogin = new Button();
        lblStatusAccount = new Label();
        btnDeleteLogin = new Button();

        HBox account1 = new HBox();
        account1.getChildren().addAll(lblUsername, txtUsername, lblPassword, txtPassword);
        HBox account2 = new HBox();
        account2.getChildren().addAll(btnSignInSignOut, btnCreateLogin, btnDeleteLogin, lblStatusAccount);

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
        // people section
        lblPeopleSection = new Label();
        lblPeopleSection.setId("labelSmall");
        VBox peopleVBox = new VBox();
        ListView<String> peopleList = new ListView<>(model.observablePeopleList);
        peopleList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        peopleVBox.getChildren().addAll(lblPeopleSection, peopleList);
        peopleVBox.getStyleClass().add("boxedSection");

        // chatrooms section
        lblChatroomsSection = new Label();
        lblChatroomsSection.setId("labelSmall");
        btnUpdateChatroomsList = new Button();
        btnCreateChatroom = new Button();
        btnDeleteChatroom = new Button();
        btnJoinSelectedChatroom = new Button();
        btnJoinPrivateChatroom = new Button();

        // todo: not yet implemented:
        btnJoinPrivateChatroom.setDisable(true);

        lblPublicChatrooms = new Label();
        lblPublicChatrooms.setId("labelSmall");
        HBox chatroom1 = new HBox();
        chatroom1.getChildren().addAll(btnUpdateChatroomsList, btnCreateChatroom, btnDeleteChatroom);
        HBox chatroom2 = new HBox();
        chatroom2.getChildren().addAll(btnJoinSelectedChatroom, btnJoinPrivateChatroom);
        chatroomsList = new ListView<>(model.observableChatroomsList);
        chatroomsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VBox chatroomsVBox = new VBox();
        chatroomsVBox.getChildren().addAll(lblChatroomsSection, chatroom1, chatroom2, lblPublicChatrooms, chatroomsList);
        chatroomsVBox.getStyleClass().add("boxedSection");
        VBox leftSection = new VBox();
        leftSection.getChildren().addAll(peopleVBox, chatroomsVBox);
        root.setLeft(leftSection);


        //-----------------//
        // center section: //
        //-----------------//
        lblChatSection = new Label();
        lblChatSection.setId("labelSmall");

        VBox centerSection = new VBox();
        centerSection.getChildren().addAll(lblChatSection);
        centerSection.getStyleClass().add("boxedSection");
        root.setCenter(centerSection);

        //-----------------//
        // bottom section: //
        //-----------------//
        // status section:
        lblStatusSection = new Label();
        lblStatusSection.setId("labelSmall");
        lblLastStatus = new Label();
        HBox statusHBox = new HBox();
        statusHBox.getChildren().addAll(lblStatusSection, lblLastStatus);
        statusHBox.getStyleClass().add("boxedSection");

        // dev section (no translations for dev-section!)
        lblDevSection = new Label("Dev/debug:");
        lblDevSection.setId("labelSmall");
        lblCommand = new Label("Direct command:");
        lblServerAnswer = new Label("Last server answer:");
        lastServerAnswerContent = new Label("(only answers from commands sent from here!)");
        txtCommand = new TextField();
        btnSendCommand = new Button("Send");

        GridPane devGrid = new GridPane();
        devGrid.add(lblCommand, 0, 1);
        devGrid.add(lblServerAnswer, 0, 0);
        devGrid.add(lastServerAnswerContent, 1, 0);

        HBox commandAndSend = new HBox();
        commandAndSend.getChildren().addAll(txtCommand, btnSendCommand);
        devGrid.add(commandAndSend, 1, 1);

        HBox devHBox = new HBox();
        devHBox.getChildren().addAll(lblDevSection, devGrid);
        devHBox.getStyleClass().add("boxedSection");

        VBox bottomVBox = new VBox();
        bottomVBox.getChildren().addAll(statusHBox, devHBox);

        root.setBottom(bottomVBox);

        updateTexts();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/aChat.css").toExternalForm());

        return scene;
    }

    /**
     * Updates all texts with the chosen translation
     */
    public void updateTexts() {
        Translator t = ServiceLocator.getServiceLocator().getTranslator();

        // todo: Problem: if I change the language anytime during running the application,
        //  many labels having different current states. How can I detect those current states for the language translation
        //  to be correct??

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
        btnPingServer.setText(t.getString("button.connection.pingServer"));
        chkboxSSL.setText(t.getString("label.connection.useSSL"));


        lblStatusServer.setText(t.getString("label.connection.status.failed"));

        // account section
        lblAccountSection.setText(t.getString("label.account"));
        lblUsername.setText(t.getString("label.account.username"));
        lblPassword.setText(t.getString("label.account.password"));
        // todo: make this button to update to signIn/SignOut
        if (AChatModel.getToken() == null) {
            btnSignInSignOut.setText(t.getString("button.account.signIn"));
        } else {
            btnSignInSignOut.setText(t.getString("button.account.signOut"));
        }
        btnCreateLogin.setText(t.getString("button.account.createLogin"));
        btnDeleteLogin.setText(t.getString("button.account.deleteLogin"));
        lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));

        // left section
        lblPeopleSection.setText(t.getString("label.people"));
        lblChatroomsSection.setText(t.getString("label.chatrooms"));
        btnUpdateChatroomsList.setText(t.getString("button.chatrooms.updateChatroomsList"));
        btnCreateChatroom.setText(t.getString("button.chatrooms.createChatroom"));
        btnDeleteChatroom.setText(t.getString("button.chatrooms.deleteChatroom"));
        btnJoinSelectedChatroom.setText(t.getString("button.chatrooms.joinSelectedChatroom"));
        btnJoinPrivateChatroom.setText(t.getString("button.chatrooms.joinPrivateChatroom"));
        lblPublicChatrooms.setText(t.getString("label.chatrooms.publicChatrooms"));

        // center section
        lblChatSection.setText(t.getString("label.chat"));

        // bottom section:
        lblStatusSection.setText(t.getString("label.status"));

        stage.setTitle(t.getString("program.name"));
    }
}
