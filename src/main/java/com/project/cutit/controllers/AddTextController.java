package com.project.cutit.controllers;

import com.project.cutit.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.project.cutit.Main.FFmpeg;
import static com.project.cutit.Main.FFprobe;

public class AddTextController {
    @FXML
    public MediaView mediaView;
    @FXML
    public MediaPlayer mediaPlayer;
    @FXML
    public TextField cordY, cordX, boxBorder, fontSize, fontBorder;
    @FXML
    public TextArea text;
    @FXML
    public ColorPicker boxColor, fontColor;
    @FXML
    public Slider boxOpacity;
    @FXML
    public Pane boxOptions;

    public void initialize() {

        TextField[] fieldArray = {cordX, cordY, boxBorder, fontSize, fontBorder};
        for (var field: fieldArray) {
            field.setTextFormatter(getNumberFormatter());
        }

        boxOptions.setDisable(true);
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

    private boolean isInvalid(TextField field) {
        return field.getText().isEmpty();
    }
    private void setAlert(TextInputControl field) {
        var rb = ResourceBundle.getBundle("com.project.cutit.translation", Main.getLocale());

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        //TODO translate
        alert.setContentText(String.format("%s field is empty", rb.getString("addText.control." + field.getId())));
        alert.show();
    }
    public void AddTextToVideo() {
        if (text.getText() == null || text.getText().trim().equals("")) {
            setAlert(text);
            return;
        }
        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY, fontSize));

        var box = "";
        if (!boxOptions.isDisabled()) {
            fieldArray.add(boxBorder);
            box = String.format(":box=%s:boxcolor=%s@%s:borderw=%s", 1, boxColor.getValue().toString(), boxOpacity.getValue(), boxBorder.getText());
        }

        for (var field: fieldArray) {
            if (isInvalid(field)){
                setAlert(field);
                return;
            }
        }


        var videoFilter = String.format("drawtext=text='%s':fontsize=%s:fontcolor=%s:x=%s:y=%s%s", text.getText(), fontSize.getText(), fontColor.getValue().toString(),cordX.getText(),cordY.getText(), box);

        var directory = mediaView.getMediaPlayer().getMedia().getSource();
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Main.normalizePath(directory))
                .addOutput(Main.getAppDataFile())
                .setVideoFilter(videoFilter)
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        executor.createJob(builder).run();
    }

    public void AddBoxClick(ActionEvent actionEvent) {
        boxOptions.setDisable( !((CheckBox)actionEvent.getSource()).isSelected() );
    }

    public void mediaClick(MouseEvent mouseEvent) {
        cordX.setText(String.valueOf(((int) mouseEvent.getX())));
        cordY.setText(String.valueOf((int) mouseEvent.getY()));
    }
}
