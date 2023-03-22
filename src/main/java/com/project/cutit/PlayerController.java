package com.project.cutit;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
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
    private MediaPlayer mediaPlayer;

    public PlayerController() {
    }

    public void initialize() {
        Media media = new Media(Main.getFilePath());
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);

        var height = playerPane.getHeight();

        mediaView.setViewport( new Rectangle2D(0,0, height, CalculateWidthByAspectRatio(height)));

        mediaView.setPreserveRatio(true);

        System.out.println(mediaView.getFitHeight());

    }

    private double CalculateWidthByAspectRatio(double height){
        var ratio = 1.7778;
        return height * ratio;
    }
    /*private EventHandler<KeyEvent> keyListener = new EventHandler<>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.SPACE) {
                if(playing){
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.play();
                }
                playing = !playing;
            }
            event.consume();
        }
    };*/

    public void Play(){
        mediaPlayer.play();
    }
    public void Pause(){
        mediaPlayer.stop();
    }
    public void Reset(){
        if (mediaPlayer.getStatus() != MediaPlayer.Status.READY) mediaPlayer.seek(Duration.seconds(0.0));
    }

}