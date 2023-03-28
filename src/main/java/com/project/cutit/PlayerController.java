package com.project.cutit;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;



public class PlayerController {
    @FXML
    public MediaView mediaView;
    @FXML
    public Pane playerPane;
    @FXML
    public Button toggleButton;
    private MediaPlayer mediaPlayer;

    public PlayerController() {
    }

    public void initialize() {
        Media media = new Media(Main.getFilePath());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> playerPane.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener));
//        mediaView = new MediaView(mediaPlayer);
        mediaView.setMediaPlayer(mediaPlayer);

        var height = playerPane.getHeight();

//        mediaView.setViewport( new Rectangle2D(0,0, height, CalculateWidthByAspectRatio(height)));

        mediaView.setPreserveRatio(true);

        System.out.println(mediaView.getFitHeight());

    }

    private double CalculateWidthByAspectRatio(double height){
        var ratio = 1.7778;
        return height * ratio;
    }
    private EventHandler<KeyEvent> keyListener = new EventHandler<>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.SPACE) {
                Toggle();
            }
            event.consume();
        }
    };

    public void Toggle(){
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if(playing){
            mediaPlayer.pause();
            toggleButton.setText("Play");
        }else{
            mediaPlayer.play();
            toggleButton.setText("Pause");
        }

    }
    public void Reset(){mediaPlayer.stop();}

}