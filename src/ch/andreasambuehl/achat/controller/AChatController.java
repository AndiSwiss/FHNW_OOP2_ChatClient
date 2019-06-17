package ch.andreasambuehl.achat.controller;

import ch.andreasambuehl.achat.abstractClasses.Controller;
import ch.andreasambuehl.achat.common.ServiceLocator;
import ch.andreasambuehl.achat.model.AChatModel;
import ch.andreasambuehl.achat.view.AChatView;
import javafx.application.Platform;

/**
 * This is the main controller for the chat client AChat.
 */
public class AChatController extends Controller<AChatModel, AChatView> {
    private ServiceLocator serviceLocator;

    public AChatController(AChatModel model, AChatView view) {
        super(model, view);

        // register to listen for button clicks
        // todo: replace this dummy test with actual stuff!
        view.btnClick.setOnAction(event1 -> incrementOnClick());

        // register to handle window-closing event
        view.getStage().setOnCloseRequest(event -> Platform.exit());

        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Application controller initialized");
    }

    private void incrementOnClick() {
        // todo: replace this dummy test with actual stuff!
        model.incrementValue();
        String newText = Integer.toString(model.getValue());
        view.lblNumber.setText(newText);
    }
}