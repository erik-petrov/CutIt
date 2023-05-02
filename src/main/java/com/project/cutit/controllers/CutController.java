package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Helper;
import com.project.cutit.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;

public class CutController extends Application {
    @FXML
    private RangeSlider seekSlider;
    @FXML
    private MediaView mediaView;
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
            seekSlider.setMax(media.getDuration().toMillis());
            seekSlider.adjustHighValue(seekSlider.getMax());

            mediaView.setMediaPlayer(mediaPlayer);

            seekSlider.highValueProperty().addListener((ov, old_val, new_val) -> mediaPlayer.seek(new Duration(new_val.doubleValue())));
            seekSlider.lowValueProperty().addListener((ov, old_val, new_val) -> mediaPlayer.seek(new Duration(new_val.doubleValue())));
        });
        Helper.setPlayer(mediaPlayer);
    }

    public void Toggle(){
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if(playing){
            mediaPlayer.pause();
        }else{
            mediaPlayer.setStartTime(new Duration(seekSlider.getHighValue()));
            mediaPlayer.setStopTime(new Duration(seekSlider.getHighValue()));
            mediaPlayer.play();
        }

    }

    public void Cut() throws IOException {
        FFmpegCommands.GenerateCutCommand(Double.valueOf(seekSlider.getLowValue()).intValue(), Double.valueOf(seekSlider.getHighValue()).intValue());
    }

    @Override
    public void start(Stage primaryStage) {}
}
