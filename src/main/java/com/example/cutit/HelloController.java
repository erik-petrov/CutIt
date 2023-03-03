package com.example.cutit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.MediaView;


public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private MediaView mediaView;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}