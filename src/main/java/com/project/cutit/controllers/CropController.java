package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.Helper;
import com.project.cutit.Main;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;

public class CropController {
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
    private final Helper Helper = new Helper();
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
        x.setText(String.valueOf(((int) mouseEvent.getX())));
        y.setText(String.valueOf(((int) mouseEvent.getY())));
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
