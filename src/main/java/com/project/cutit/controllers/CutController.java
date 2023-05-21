package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Main;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;

public class CutController extends MenuBarHelper {
    @FXML
    private RangeSlider seekSlider;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private CommonHelper Helper;

    public void initialize() {
        Helper = new CommonHelper(mediaPlayer, mediaView);
        Helper.setRunnable(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
            seekSlider.setMax(Main.getMedia().getDuration().toMillis());
            seekSlider.adjustHighValue(seekSlider.getMax());
            seekSlider.highValueProperty().addListener((ov, old_val, new_val) -> Helper.getMediaPlayer().seek(new Duration(new_val.doubleValue())));
            seekSlider.lowValueProperty().addListener((ov, old_val, new_val) -> Helper.getMediaPlayer().seek(new Duration(new_val.doubleValue())));
        });
        Helper.setMediaItems();
    }

    public final EventHandler<KeyEvent> keyListener = event -> {
        if(event.getCode() == KeyCode.SPACE) {
            Toggle();
        }
        event.consume();
    };

    public void Toggle(){
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        System.out.println("toggling");
        if(playing){
            mediaPlayer.stop();
        }else{
            mediaPlayer.setStartTime(new Duration(seekSlider.getLowValue()));
            mediaPlayer.setStopTime(new Duration(seekSlider.getHighValue()));
            mediaPlayer.play();
        }

    }

    public void Cut() throws IOException {
        FFmpegCommands.GenerateCutCommand(Double.valueOf(seekSlider.getLowValue()).intValue(), Double.valueOf(seekSlider.getHighValue()).intValue());
    }

}
