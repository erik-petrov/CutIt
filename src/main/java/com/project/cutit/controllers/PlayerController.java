package com.project.cutit.controllers;

import com.project.cutit.Main;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerController extends MenuBarHelper {
    @FXML
    public MediaView mediaView;
    @FXML
    public Button toggleButton;
    private MediaPlayer mediaPlayer;
    private CommonHelper CommonHelper;

    public void initialize() {
        CommonHelper =  new CommonHelper(mediaPlayer, mediaView);
        CommonHelper.setMediaItems(() -> mediaView.addEventHandler(KeyEvent.KEY_PRESSED, CommonHelper.keyListener));
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