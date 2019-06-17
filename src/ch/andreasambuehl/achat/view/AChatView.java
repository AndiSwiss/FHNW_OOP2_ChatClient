package ch.andreasambuehl.achat.view;

import ch.andreasambuehl.achat.abstractClasses.View;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.common.Translator;
import ch.andreasambuehl.achat.model.AChatModel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the main view for the chat client AChat.
 */
public class AChatView extends View<AChatModel> {
    // top section:
    private Menu menuFile;
    private Menu menuFileLanguage;
    private Menu menuHelp;
    private MenuItem menuAbout;


    private Label connectionSection;
    private Label accountSection;

    // left section:
    private Label buddiesSection;
    private Label chatroomsSection;

    // center section:
    private Label chatSection;

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

        connectionSection = new Label();
        accountSection = new Label();

        VBox topSection = new VBox();
        topSection.getChildren().addAll(menuBar, connectionSection, accountSection);

        root.setTop(topSection);

        //---------------//
        // left section: //
        //---------------//
        buddiesSection = new Label();
        chatroomsSection = new Label();
        VBox leftSection = new VBox();
        leftSection.getChildren().addAll(buddiesSection, chatroomsSection);
        root.setLeft(leftSection);


        //-----------------//
        // center section: //
        //-----------------//
        chatSection = new Label();
        lblNumber = new Label();
        lblNumber.setText(Integer.toString(model.getValue()));
        lblNumber.setMinWidth(200);
        lblNumber.setAlignment(Pos.BASELINE_CENTER);

        btnClick = new Button();
        btnClick.setMinWidth(200);

        VBox centerSection = new VBox();
        centerSection.getChildren().addAll(chatSection, lblNumber, btnClick);
        root.setCenter(centerSection);

        updateTexts();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/AChat.css").toExternalForm());

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

        connectionSection.setText(t.getString("label.connection"));
        accountSection.setText(t.getString("label.account"));

        // left section
        buddiesSection.setText(t.getString("label.buddies"));
        chatroomsSection.setText(t.getString("label.chatrooms"));

        // center section
        chatSection.setText(t.getString("label.chat"));

        // Other controls
        btnClick.setText(t.getString("button.clickMe"));

        stage.setTitle(t.getString("program.name"));
    }
}
