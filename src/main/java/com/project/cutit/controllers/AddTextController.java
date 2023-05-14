package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.Main;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddTextController extends MenuBarHelper {
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
    private final CommonHelper CommonHelper = new CommonHelper();

    public void initialize() {
        TextField[] fieldArray = {cordX, cordY, boxBorder, fontSize, fontBorder};
        for (var field: fieldArray) field.setTextFormatter(CommonHelper.getNumberFormatter());

        boxOptions.setDisable(true);
        Media media = Main.getMedia();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, CommonHelper.keyListener);

            mediaView.setMediaPlayer(mediaPlayer);
        });

        CommonHelper.setPlayer(mediaPlayer);
    }
    public void AddTextToVideo() throws IOException {
        if (text.getText() == null || text.getText().trim().equals("")) {
            CommonHelper.setAlert("addText.control." + text.getId(), Alert.AlertType.ERROR);
            return;
        }
        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY, fontSize));

        var box = "";
        if (!boxOptions.isDisabled()) {
            fieldArray.add(boxBorder);
            box = String.format(":box=%s:boxcolor=%s@%s:borderw=%s", 1, boxColor.getValue().toString(), boxOpacity.getValue() / 100, boxBorder.getText());
        }

        for (var field: fieldArray) {
            if (CommonHelper.isInvalid(field)){
                CommonHelper.setAlert(field.getId(), Alert.AlertType.ERROR);
                return;
            }
        }


        var videoFilter = String.format("drawtext=text='%s':fontsize=%s:fontcolor=%s:x=%s:y=%s%s", text.getText(), fontSize.getText(), fontColor.getValue().toString(),cordX.getText(),cordY.getText(), box);
        FFmpegCommands.GenerateTextCommand(videoFilter);
    }

    public void AddBoxClick(ActionEvent actionEvent) {
        boxOptions.setDisable( !((CheckBox)actionEvent.getSource()).isSelected() );
    }

    public void mediaClick(MouseEvent mouseEvent) {
        var coords = CommonHelper.getAccurateCoordinates(mouseEvent, mediaView);
        cordX.setText(String.valueOf((coords[0])));
        cordY.setText(String.valueOf(coords[1]));
    }
}
