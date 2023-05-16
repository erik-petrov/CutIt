package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Main;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class CropController extends MenuBarHelper {
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private TextField x;
    @FXML
    private TextField y;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private final CommonHelper Helper = new CommonHelper();
    public void initialize(){
        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener);
            mediaView.setMediaPlayer(mediaPlayer);
        });

        Helper.setPlayer(mediaPlayer);
    }

    public void mediaClick(MouseEvent mouseEvent) {
        var coords = Helper.getAccurateCoordinates(mouseEvent, mediaView);
        x.setText(String.valueOf(coords[0]));
        y.setText(String.valueOf(coords[1]));
    }

    public void Crop() throws IOException {
        if(!Helper.CheckValues(width.getText(), height.getText(), x.getText(), y.getText())){
            return;
        }

        int w = Integer.parseInt(width.getText());
        int h = Integer.parseInt(height.getText());
        int xV = Integer.parseInt(x.getText());
        int yV = Integer.parseInt(y.getText());

        String filter = "crop="+w+":"+h+":"+xV+":"+yV;

        FFmpegCommands.GenerateCropCommand(filter);
    }
}
