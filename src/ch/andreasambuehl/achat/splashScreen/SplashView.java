package ch.andreasambuehl.achat.splashScreen;

import ch.andreasambuehl.achat.abstractClasses.View;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 *
 * @author Brad Richards
 */
public class SplashView extends View<SplashModel> {

    ProgressBar progress;
    private Label lblStatus;

    public SplashView(Stage stage, SplashModel model) {
        super(stage, model);
        stage.initStyle(StageStyle.TRANSPARENT); // also undecorated
    }

    @Override
    protected Scene create_GUI() {
        BorderPane root = new BorderPane();
        root.setId("splash");

        lblStatus = new Label("Loading AChat ...");
        root.setTop(lblStatus);

        progress = new ProgressBar();
        HBox bottomBox = new HBox();
        bottomBox.setId("progressbox");
        bottomBox.getChildren().add(progress);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 480, 360, Color.TRANSPARENT);
        scene.getStylesheets().addAll(
                this.getClass().getResource("splash.css").toExternalForm()
        );

        return scene;
    }
}
