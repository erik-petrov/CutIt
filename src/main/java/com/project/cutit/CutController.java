package com.project.cutit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

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
        Media media = new Media(Main.getFilePath());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener));
        mediaView.setMediaPlayer(mediaPlayer);

        seekSlider.setOn
    }
    private final EventHandler<KeyEvent> keyListener = event -> {
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
            mediaPlayer.play();
        }

    }

    public void SeekToDestination(double dest){
        mediaPlayer.seek(new Duration(dest));
    }

    @Override
    public void start(Stage primaryStage) {}
}
