package com.project.cutit.controllers;

import com.project.cutit.Helper;
import com.project.cutit.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerController {
    @FXML
    public MediaView mediaView;
    @FXML
    public Button toggleButton;
    private MediaPlayer mediaPlayer;
    private final Helper Helper = new Helper();

    public void initialize() {
        Media media = Main.getMedia();

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(() -> System.out.println(mediaPlayer.getError()));
        mediaView.setOnError((ar) -> System.out.println(ar));
        mediaPlayer.setOnReady(() -> mediaView.addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener));
        mediaView.setMediaPlayer(mediaPlayer);

    }

    public void Toggle(){
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if(playing){
            mediaPlayer.pause();
        }else{
            mediaPlayer.play();
        }
    }

    public void Reset(){ mediaPlayer.stop(); }

}