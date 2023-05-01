package com.project.cutit.controllers;

import com.project.cutit.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.project.cutit.Main.FFmpeg;
import static com.project.cutit.Main.FFprobe;

public class AddImageController {
    @FXML
    public ImageView imageView;
    @FXML
    public Label dragLabel;
    @FXML
    public TextField imageHeight, imageWidth, cordY, cordX;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;

    public void initialize() {

        TextField[] fieldArray = {cordX, cordY, imageHeight, imageWidth};
        for (var field: fieldArray) {
            field.setTextFormatter(getNumberFormatter());
        }

        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener);

            mediaView.setMediaPlayer(mediaPlayer);
        });

    }

    private TextFormatter<String> getNumberFormatter() {
        return new TextFormatter<>(change-> {
            String text1 = change.getText();
            if (text1.matches("[0-9]*")) {
                return change;
            }
            return null;
        });
    }

    private final EventHandler<KeyEvent> keyListener = event -> {
        if(event.getCode() == KeyCode.SPACE) {
            Toggle();
        }
        event.consume();
    };

    public void Toggle(){
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if(playing){
            mediaPlayer.pause();
        }else{
            mediaPlayer.play();
        }

    }

    public void mediaClick(MouseEvent mouseEvent) {
        cordX.setText(String.valueOf(((int) mouseEvent.getX())));
        cordY.setText(String.valueOf((int) mouseEvent.getY()));
    }

    public void OpenFileDialog(ActionEvent actionEvent) {
        var openButton = (Button)actionEvent.getSource();

        FileChooser fileChooser = new FileChooser();

        //TODO Translate

        // Set the title of the file chooser dialog
        fileChooser.setTitle("Open File");

        // Set the initial directory of the file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Show the file chooser dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(openButton.getScene().getWindow());

        // Check if a file was selected
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
        if (dragEvent.getGestureSource() != dragEvent.getTarget()
                && dragEvent.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    private boolean isInvalid(TextField field) {
        return field.getText().isEmpty();
    }

    private void setAlert(TextInputControl field) {
        var rb = ResourceBundle.getBundle("com.project.cutit.translation", Main.getLocale());

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        //TODO translate
        alert.setContentText(String.format("%s field is empty", rb.getString("addImage.control." + field.getId())));
        alert.show();
    }

    //TODO make a separete FFMPEG class that holds all this stuff so Main isn't clutter and code makes sense. Just pass along the paths, filter and be done. Also the class does the progress bar.
    public void AddImage() throws IOException {
        if (imageView.getImage() == null) {
            var rb = ResourceBundle.getBundle("com.project.cutit.translation", Main.getLocale());
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(String.format("%s field is empty", rb.getString("addImage.control." + imageView.getId())));
            alert.show();
            return;
        }

        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY));
        var useScale = false;
        if (!imageWidth.getText().isEmpty() || !imageHeight.getText().isEmpty()){
            fieldArray.add(imageHeight);
            fieldArray.add(imageWidth);
            useScale = true;
        }

        for (var field: fieldArray) {
            if (isInvalid(field)){
                setAlert(field);
                return;
            }
        }


        String imageFilter = "";
        if (useScale) {
            imageFilter = String.format("[1:v]scale=%s:%s[img];[0:v][img]", imageWidth.getText(), imageHeight.getText());
        }
        imageFilter += String.format("overlay=%s:%s", cordX.getText(), cordY.getText());

        var imageFile = new File(new URL(imageView.getImage().getUrl()).getPath());
        System.out.println(imageFilter);
        System.out.println(imageFile.getAbsolutePath());

        var directory = mediaView.getMediaPlayer().getMedia().getSource();
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Main.normalizePath(directory))
                .addInput(FFprobe.probe(imageFile.getAbsolutePath()))
                .addExtraArgs("-filter_complex", imageFilter)
                .addOutput(Main.getAppDataFile())
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        executor.createJob(builder).run();
    }
}

