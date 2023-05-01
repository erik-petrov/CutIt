package com.project.cutit.controllers;

import com.project.cutit.Main;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;

import static java.util.ResourceBundle.getBundle;

public class CutController extends Application {
    @FXML
    private RangeSlider seekSlider;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    public void initialize() {
        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
            seekSlider.setMax(media.getDuration().toMillis());
            seekSlider.adjustHighValue(seekSlider.getMax());

            mediaView.setMediaPlayer(mediaPlayer);

            seekSlider.highValueProperty().addListener((ov, old_val, new_val) -> mediaPlayer.seek(new Duration(new_val.doubleValue())));
            seekSlider.lowValueProperty().addListener((ov, old_val, new_val) -> mediaPlayer.seek(new Duration(new_val.doubleValue())));
        });

    }

    private final EventHandler<KeyEvent> keyListener = event -> {
        System.out.println("hii");
        if(event.getCode() == KeyCode.SPACE) {
            Toggle();
        }
        event.consume();
    };

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
        Main.GenerateCutCommand(Double.valueOf(seekSlider.getLowValue()).intValue(), Double.valueOf(seekSlider.getHighValue()).intValue());
        //TODO:make it so that a window pops out
    }

    @Override
    public void start(Stage primaryStage) {}
}
