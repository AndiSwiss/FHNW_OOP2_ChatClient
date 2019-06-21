package ch.andreasambuehl.achat.view;

import ch.andreasambuehl.achat.abstractClasses.View;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the main view for the chat client AChat.
 */
public class AChatView extends View<AChatModel> {
    //---------------//
    // menu section: //
    //---------------//
    private Menu menuFile;
    public Menu menuFileLanguage;
    public MenuItem menuDev;
    private Menu menuHelp;
    public MenuItem menuAbout;

    //---------------------//
    // connection section: //
    //---------------------//
    private Label lblConnectionSection;
    private Label lblServer;
    public TextField txtServer;
    private Label lblPort;
    public TextField txtPort;
    public CheckBox chkboxSSL;
    public Button btnConnectDisconnect;
    public Button btnPingServer;
    public Label lblStatusServer;

    //------------------//
    // account section: //
    //------------------//
    public Label lblAccountSection;
    public Label lblUsername;
    public TextField txtUsername;
    public Label lblPassword;
    public PasswordField txtPassword;
    public Button btnSignInSignOut;
    public Button btnCreateLogin;
    public Button btnDeleteLogin;
    public Label lblStatusAccount;

    //-------------------//
    // chatroom section: //
    //-------------------//
    public VBox chatroomSection;
    private Label lblChatroomsSection;
    public Button btnUpdateChatroomsList;
    public Button btnCreateChatroom;
    public Button btnDeleteChatroom;
    public Button btnJoinSelectedChatroom;
    public Button btnLeaveSelectedChatroom;
    public Button btnJoinPrivateChatroom;
    public Button btnLeavePrivateChatroom;
    private Label lblPublicChatrooms;
    public ListView<String> chatroomsList;

    //---------------//
    // chat section: //
    //---------------//
    public VBox chatSection;
    private Label lblChatSection;
    // todo: check the type of the following list -> probably change to VBox or something else
    public TextField txtChatSearch;
    public ListView<String> chatHistoryList;
    public TextField txtSendChat;
    private Label lblSendTo;
    public Button btnSendToSelectedChatroom;
    public Button btnSendToPrivateChatroom;
    public Button btnSendToPerson;

    //-----------------//
    // status section: //
    //-----------------//
    private Label lblStatusSection;
    public Label lblLastStatus;

    //--------------//
    // dev section: //
    //--------------//
    public GridPane devSection;
    public TextField txtCommand;
    public Button btnSendCommand;
    public Label lblLastServerAnswerContent;
    public boolean showDevSection;


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


        //---------------//
        // menu section: //
        //---------------//
        MenuBar menuBar = new MenuBar();
        menuFile = new Menu();
        menuFileLanguage = new Menu();
        menuDev = new MenuItem();
        menuFile.getItems().addAll(menuFileLanguage, menuDev);

        for (Locale locale : sl.getLocales()) {
            MenuItem language = new MenuItem(locale.getLanguage());
            menuFileLanguage.getItems().add(language);
        }

        menuHelp = new Menu();
        menuAbout = new MenuItem();
        menuHelp.getItems().add(menuAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);


        //---------------------//
        // connection section: //
        //---------------------//
        lblConnectionSection = new Label();
        lblConnectionSection.getStyleClass().add("labelSmall");
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
        lblStatusServer.getStyleClass().add("alert");

        HBox connection1 = new HBox();
        connection1.getChildren().addAll(lblServer, txtServer, lblPort, txtPort, chkboxSSL);
        HBox connection2 = new HBox();
        connection2.getChildren().addAll(btnConnectDisconnect, btnPingServer, lblStatusServer);

        // todo: optimize CSS-Styling!!
        connection2.getStyleClass().add("specialHBox");

        VBox connectionSection = new VBox();
        connectionSection.getChildren().addAll(lblConnectionSection, connection1, connection2);


        //--------------//
        // dev section: //
        //--------------//
        // -> no translations for dev-section
        Label lblDevSection = new Label("Dev/debug: (english only)");
        lblDevSection.getStyleClass().add("labelSmall");
        Label lblCommand = new Label("Direct command:");
        Label lblServerAnswer = new Label("Last server answer:");
        lblLastServerAnswerContent = new Label("(only answers from commands sent from here!)");
        txtCommand = new TextField();
        btnSendCommand = new Button("Send");

        devSection = new GridPane();
        devSection.add(lblDevSection, 0, 0);
        devSection.add(lblCommand, 0, 2);
        devSection.add(lblServerAnswer, 0, 1);
        devSection.add(lblLastServerAnswerContent, 1, 1);

        HBox commandAndSend = new HBox();
        commandAndSend.getChildren().addAll(txtCommand, btnSendCommand);
        devSection.add(commandAndSend, 1, 2);

        // for accessing specific CSS for a boxedSection:
        devSection.getStyleClass().add("boxedSection");

        HBox connectionAndDevSection = new HBox();
        connectionAndDevSection.getChildren().addAll(connectionSection, devSection);
        connectionAndDevSection.getStyleClass().add("boxedSection");


        //------------------//
        // account section: //
        //------------------//
        lblAccountSection = new Label();
        lblAccountSection.getStyleClass().add("labelSmall");
        lblUsername = new Label();
        txtUsername = new TextField(sl.getConfiguration().getOption("Username"));
        lblPassword = new Label();
        txtPassword = new PasswordField();
        txtPassword.setText(sl.getConfiguration().getOption("Password"));
        btnSignInSignOut = new Button();
        btnCreateLogin = new Button();
        lblStatusAccount = new Label();
        lblStatusAccount.getStyleClass().add("alert");
        btnDeleteLogin = new Button();

        HBox account1 = new HBox();
        account1.getChildren().addAll(lblUsername, txtUsername, lblPassword, txtPassword);
        HBox account2 = new HBox();
        account2.getChildren().addAll(btnSignInSignOut, btnCreateLogin, btnDeleteLogin, lblStatusAccount);

        // todo: optimize CSS-Styling!!
        account2.getStyleClass().add("specialHBox");
        VBox accountSection = new VBox();
        accountSection.getChildren().addAll(lblAccountSection, account1, account2);
        accountSection.getStyleClass().add("boxedSection");

        // construct the whole topSection:
        VBox topSection = new VBox();
        topSection.getChildren().addAll(menuBar, connectionAndDevSection, accountSection);
        root.setTop(topSection);


        //-------------------//
        // chatroom section: //
        //-------------------//
        lblChatroomsSection = new Label();
        lblChatroomsSection.getStyleClass().add("labelSmall");
        btnUpdateChatroomsList = new Button();
        btnCreateChatroom = new Button();
        btnDeleteChatroom = new Button();
        btnJoinSelectedChatroom = new Button();
        btnLeaveSelectedChatroom = new Button();
        btnJoinPrivateChatroom = new Button();
        btnLeavePrivateChatroom = new Button();

        // todo: not yet implemented:
        btnJoinPrivateChatroom.setDisable(true);
        btnJoinPrivateChatroom.getStyleClass().add("notImplemented");
        btnLeavePrivateChatroom.setDisable(true);
        btnLeavePrivateChatroom.getStyleClass().add("notImplemented");

        lblPublicChatrooms = new Label();
        lblPublicChatrooms.getStyleClass().add("labelSmall");
        HBox chatroom1 = new HBox();
        chatroom1.getChildren().addAll(btnUpdateChatroomsList, btnCreateChatroom, btnDeleteChatroom);
        HBox chatroom2 = new HBox();
        chatroom2.getChildren().addAll(btnJoinSelectedChatroom, btnLeaveSelectedChatroom);
        HBox chatroom3 = new HBox(btnJoinPrivateChatroom, btnLeavePrivateChatroom);

        chatroomsList = new ListView<>(model.getObservableChatroomsList());
        chatroomsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        chatroomSection = new VBox();
        chatroomSection.getChildren().addAll(lblChatroomsSection, chatroom1, chatroom2, chatroom3, lblPublicChatrooms, chatroomsList);
        chatroomSection.getStyleClass().add("boxedSection");

        root.setLeft(chatroomSection);


        //---------------//
        // chat section: //
        //---------------//
        lblChatSection = new Label();
        lblChatSection.getStyleClass().add("labelSmall");
        txtChatSearch = new TextField();
        HBox chatSearchRight = new HBox();

        // todo: trying to right-align the search-field
        //  even after 1 hour of trying -> doesn't work at all!
        //  But also when I download alignment examples from the internet -> they don't work as
        //  well -> everything stays always left aligned
        //  -> maybe JavaFX is corrupted?? (or maybe I'm missing something here?!?)
//        txtChatSearch.setAlignment(Pos.BASELINE_RIGHT);
//        Label blindLbl1 = new Label();
//        HBox.setHgrow(blindLbl1, Priority.ALWAYS);
//        blindLbl1.setMaxWidth(Double.MAX_VALUE);
//        chatSearchRight.setMaxWidth(Double.MAX_VALUE);
//        chatSearchRight.getChildren().addAll(blindLbl1, txtChatSearch);


        chatHistoryList = new ListView<>(model.getObservableChatHistory());
        txtSendChat = new TextField();
        lblSendTo = new Label();
        btnSendToSelectedChatroom = new Button();
        btnSendToPrivateChatroom = new Button();
        btnSendToPerson = new Button();

        txtSendChat.setPrefWidth(600);

        // todo: not yet implemented:
        txtChatSearch.setDisable(true);
        txtChatSearch.getStyleClass().add("notImplemented");
        btnSendToPrivateChatroom.setDisable(true);
        btnSendToPrivateChatroom.getStyleClass().add("notImplemented");
        btnSendToPerson.setDisable(true);
        btnSendToPerson.getStyleClass().add("notImplemented");

        HBox chatBox1 = new HBox();
        chatBox1.getChildren().addAll(txtChatSearch);
        HBox chatBox2 = new HBox();
        chatBox2.getChildren().addAll(txtSendChat);
        HBox chatBox3 = new HBox();
        chatBox3.getChildren().addAll(lblSendTo, btnSendToSelectedChatroom, btnSendToPrivateChatroom, btnSendToPerson);

        chatSection = new VBox();
        chatSection.getChildren().addAll(lblChatSection, chatBox1, chatHistoryList, chatBox2, chatBox3);
        chatSection.getStyleClass().add("boxedSection");
        root.setCenter(chatSection);


        //-----------------//
        // status section: //
        //-----------------//
        lblStatusSection = new Label();
        lblStatusSection.getStyleClass().add("labelSmall");
        lblLastStatus = new Label();
        HBox statusHBox = new HBox();
        statusHBox.getChildren().addAll(lblStatusSection, lblLastStatus);
        statusHBox.getStyleClass().add("boxedSection");



        VBox bottomVBox = new VBox();
        bottomVBox.getChildren().addAll(statusHBox);

        root.setBottom(bottomVBox);


        //----------//
        // general: //
        //----------//
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

        // application name:
        stage.setTitle(t.getString("program.name"));

        // menu section
        menuFile.setText(t.getString("program.menu.file"));
        menuFileLanguage.setText(t.getString("program.menu.file.language"));
        menuHelp.setText(t.getString("program.menu.help"));
        menuAbout.setText(t.getString("program.menu.help.about"));
        if (showDevSection) {
            menuDev.setText(t.getString("program.menu.devHide"));
        } else {
            menuDev.setText(t.getString("program.menu.devShow"));
        }

        // connection section
        lblConnectionSection.setText(t.getString("label.connection"));
        lblServer.setText(t.getString("label.connection.server"));
        lblPort.setText(t.getString("label.connection.port"));
        if (model.getServerConnected()) {
            btnConnectDisconnect.setText(t.getString("button.disconnect"));
        } else {
            btnConnectDisconnect.setText(t.getString("button.connect"));
        }
        btnPingServer.setText(t.getString("button.connection.pingServer"));
        chkboxSSL.setText(t.getString("label.connection.useSSL"));
        lblStatusServer.setText(t.getString("label.connection.status.notConnected"));

        // account section
        lblAccountSection.setText(t.getString("label.account"));
        lblUsername.setText(t.getString("label.account.username"));
        lblPassword.setText(t.getString("label.account.password"));
        if (model.getToken() == null) {
            btnSignInSignOut.setText(t.getString("button.account.signIn"));
        } else {
            btnSignInSignOut.setText(t.getString("button.account.signOut"));
        }
        btnCreateLogin.setText(t.getString("button.account.createLogin"));
        btnDeleteLogin.setText(t.getString("button.account.deleteLogin"));
        lblStatusAccount.setText(t.getString("label.account.status.notLoggedIn"));

        // chatroom section
        lblChatroomsSection.setText(t.getString("label.chatrooms"));
        btnUpdateChatroomsList.setText(t.getString("button.chatrooms.updateChatroomsList"));
        btnCreateChatroom.setText(t.getString("button.chatrooms.createChatroom"));
        btnDeleteChatroom.setText(t.getString("button.chatrooms.deleteChatroom"));
        btnJoinSelectedChatroom.setText(t.getString("button.chatrooms.joinSelectedChatroom"));
        btnJoinPrivateChatroom.setText(t.getString("button.chatrooms.joinPrivateChatroom"));
        lblPublicChatrooms.setText(t.getString("label.chatrooms.publicChatrooms"));
        btnLeaveSelectedChatroom.setText(t.getString("button.chatrooms.LeaveSelectedChatroom"));
        btnLeavePrivateChatroom.setText(t.getString("button.chatrooms.LeavePrivateChatroom"));

        // chat section
        lblChatSection.setText(t.getString("label.chat"));
        txtChatSearch.setPromptText(t.getString("text.chat.search"));
        txtSendChat.setPromptText(t.getString("text.chat.sendChat"));
        lblSendTo.setText(t.getString("label.chat.sendTo"));
        btnSendToSelectedChatroom.setText(t.getString("button.chat.sendToSelectedChatroom"));
        btnSendToPrivateChatroom.setText(t.getString("button.chat.sendToPrivateChatroom"));
        btnSendToPerson.setText(t.getString("button.chat.sendToPerson"));

        // status section:
        lblStatusSection.setText(t.getString("label.status"));
    }
}
