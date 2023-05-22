package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
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
    private CommonHelper Helper;

    public void initialize() {
        Helper = new CommonHelper(mediaPlayer, mediaView, () -> mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener));

        TextField[] fieldArray = {cordX, cordY, imageHeight, imageWidth};
        for (var field: fieldArray) field.setTextFormatter(Helper.getNumberFormatter());

        Helper.setMediaItems();
    }

    public void mediaClick(MouseEvent mouseEvent) {
        var coords = Helper.getAccurateCoordinates(mouseEvent, mediaView);
        cordX.setText(String.valueOf((coords[0])));
        cordY.setText(String.valueOf(coords[1]));
    }

    public void OpenFile(ActionEvent ev){
        File selectedFile = Helper.OpenFileDialog(ev);
        if (selectedFile != null) {
            var fileExtension = getFileExtension(selectedFile.getName()).toLowerCase();

            if (isImageFile(fileExtension) || isGifFile(fileExtension)) {
                imageView.setImage(new Image(selectedFile.getAbsolutePath()));
                dragLabel.setVisible(false);
            }
        }
    }

    public void onDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();

        boolean success = false;
        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            var fileExtension = getFileExtension(file.getName()).toLowerCase();

            if (isImageFile(fileExtension) || isGifFile(fileExtension)) {
                imageView.setImage(new Image(file.toURI().toString()));
                dragLabel.setVisible(false);
                success = true;
            } else {
                Helper.setAlert("error.image", Alert.AlertType.ERROR);
            }
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private boolean isImageFile(String fileExtension) {
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png");
    }

    private boolean isGifFile(String fileExtension) {
        return fileExtension.equals("gif");
    }

    public void OnDragOver(DragEvent dragEvent) {
        Helper.OnDragOver(dragEvent);
    }

    public void AddImage() throws IOException {
        boolean useScale = false;
        if (imageView.getImage() == null) {
            Helper.setAlert(imageView.getId(), Alert.AlertType.ERROR);
            return;
        }

        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY));
        if (!imageWidth.getText().isEmpty() || !imageHeight.getText().isEmpty()){
            fieldArray.add(imageHeight);
            fieldArray.add(imageWidth);
            useScale = true;
        }

        for (var field: fieldArray) {
            if (Helper.isInvalid(field)){
                Helper.setAlert("addImage.control." + field.getId(), Alert.AlertType.ERROR);
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

