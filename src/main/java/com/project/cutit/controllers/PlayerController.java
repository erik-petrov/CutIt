package com.project.cutit.controllers;

import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.I18n_Helper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerController extends MenuBarHelper {
    @FXML
    public MediaView mediaView;
    @FXML
    public Button toggleButton;
    private MediaPlayer mediaPlayer;
    private CommonHelper Helper;

    public void initialize() {
        Helper =  new CommonHelper(mediaPlayer, mediaView,
                () -> mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener));
        Helper.setMediaItems();
    }

    public void Toggle(){
        boolean playing = Helper.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PLAYING);
        if (playing) {
            toggleButton.setText(I18n_Helper.getTranslation("player.button.play"));
            Helper.getMediaPlayer().pause();
        } else {
            toggleButton.setText(I18n_Helper.getTranslation("player.button.pause"));
            Helper.getMediaPlayer().play();
        }
    }

    public void Reset() {
        Helper.getMediaPlayer().stop();
        toggleButton.setText(I18n_Helper.getTranslation("player.button.play"));
    }

}