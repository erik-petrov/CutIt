package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Helper;
import com.project.cutit.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;

public class TimeController extends Application {

    @FXML
    private Slider speedSlider;
    @FXML
    private MediaView mediaView;
    @FXML
    private Label speedFactor;
    private MediaPlayer mediaPlayer;
    private final Helper Helper = new Helper();

    public static void main(String[] args) {
        launch(args);
    }

    public void initialize() {
        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener);
            mediaView.setMediaPlayer(mediaPlayer);
        });

        speedSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> speedFactor.textProperty().setValue(
                String.valueOf(newValue.intValue())));
    }

    public void ChangeSpeed() throws IOException {
        double factor = Math.floor(speedSlider.getValue());
        if(factor < 0){
            factor = 1/Math.abs(factor);
        }

        if(factor == 0){
            factor = 1.0;
        }

        String filter = factor >= 0.5 ? "[0:v]setpts="+1/factor+"*PTS[v];[0:a]atempo="+factor+"[a]" : "[0:v]setpts="+1/factor+"*PTS[v]"; //if slowdown then no audio
        String[] extras = factor >= 0.5 ? new String[]{"-map", "[a]", "-map", "[v]"} : new String[]{"-map", "[v]"};

        FFmpegCommands.GenerateSpeedCommand(factor, extras, filter);
    }

    @Override
    public void start(Stage stage) throws Exception {
    }
}
