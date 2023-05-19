package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.Main;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class TimeController extends MenuBarHelper {
    @FXML
    public Button timeButton;
    @FXML
    private Slider speedSlider;
    @FXML
    private MediaView mediaView;
    @FXML
    private MediaPlayer mediaPlayer;
    private CommonHelper Helper;

    public void initialize() {
        Helper = new CommonHelper(mediaPlayer, mediaView, () -> mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener));
        Helper.setMediaItems();

    }

    public void ChangeSpeed() throws IOException {
        double factor = Math.floor(speedSlider.getValue());
        if (factor < 0){
            factor = 1/Math.abs(factor);
        }

        if (factor == 0){
            factor = 1.0;
        }

        String filter = factor >= 0.5 ? "[0:v]setpts="+1/factor+"*PTS[v];[0:a]atempo="+factor+"[a]" : "[0:v]setpts="+1/factor+"*PTS[v]"; //if slowdown then no audio
        String[] extras = factor >= 0.5 ? new String[]{"-map", "[a]", "-map", "[v]"} : new String[]{"-map", "[v]"};

        FFmpegCommands.GenerateSpeedCommand(factor, extras, filter);
    }
}
