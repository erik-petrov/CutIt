package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Main;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.fxml.FXML;
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

    public void initialize() {
        CommonHelper Helper = new CommonHelper(mediaPlayer, mediaView);
        Helper.setRunnable(() -> {
            seekSlider.setMax(Main.getMedia().getDuration().toMillis());
            seekSlider.adjustHighValue(seekSlider.getMax());
            seekSlider.highValueProperty().addListener((ov, old_val, new_val) -> Helper.getMediaPlayer().seek(new Duration(new_val.doubleValue())));
            seekSlider.lowValueProperty().addListener((ov, old_val, new_val) -> Helper.getMediaPlayer().seek(new Duration(new_val.doubleValue())));
        });
        Helper.setMediaItems();
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

}
