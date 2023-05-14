package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.Main;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddImageController extends MenuBarHelper {
    @FXML
    public ImageView imageView;
    @FXML
    public Label dragLabel;
    @FXML
    public TextField imageHeight, imageWidth, cordY, cordX;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private final CommonHelper CommonHelper = new CommonHelper();

    public void initialize() {
        TextField[] fieldArray = {cordX, cordY, imageHeight, imageWidth};
        for (var field: fieldArray) field.setTextFormatter(CommonHelper.getNumberFormatter());

        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, CommonHelper.keyListener);
            mediaView.setMediaPlayer(mediaPlayer);
        });

        CommonHelper.setPlayer(mediaPlayer);
    }

    public void mediaClick(MouseEvent mouseEvent) {
        var coords = CommonHelper.getAccurateCoordinates(mouseEvent, mediaView);
        cordX.setText(String.valueOf((coords[0])));
        cordY.setText(String.valueOf(coords[1]));
    }

    public void OpenFile(ActionEvent ev){
        File selectedFile = CommonHelper.OpenFileDialog(ev);
        if (selectedFile != null) {
            imageView.setImage(new Image(selectedFile.getAbsolutePath()));
            dragLabel.setVisible(false);
        }
    }

    public void onDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();

        boolean success = false;
        if (dragboard.hasFiles()) {
            imageView.setImage(new Image(dragboard.getFiles().get(0).toURI().toString()));
            dragLabel.setVisible(false);
            success = true;
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    public void OnDragOver(DragEvent dragEvent) {
        CommonHelper.OnDragOver(dragEvent);
    }

    public void AddImage() throws IOException {
        boolean useScale = false;
        if (imageView.getImage() == null) {
            CommonHelper.setAlert(imageView.getId(), Alert.AlertType.ERROR);
            return;
        }

        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY));
        if (!imageWidth.getText().isEmpty() || !imageHeight.getText().isEmpty()){
            fieldArray.add(imageHeight);
            fieldArray.add(imageWidth);
            useScale = true;
        }

        for (var field: fieldArray) {
            if (CommonHelper.isInvalid(field)){
                CommonHelper.setAlert("addImage.control." + field.getId(), Alert.AlertType.ERROR);
                return;
            }
        }

        String imageFilter = "";
        if (useScale) {
            imageFilter = String.format("[1:v]scale=%s:%s[img];[0:v][img]", imageWidth.getText(), imageHeight.getText());
        }
        imageFilter += String.format("overlay=%s:%s", cordX.getText(), cordY.getText());

        FFmpegCommands.GenerateImageCommand(imageView.getImage().getUrl(), imageFilter);
    }
}

