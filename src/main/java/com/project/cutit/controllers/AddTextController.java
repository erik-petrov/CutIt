package com.project.cutit.controllers;

import com.project.cutit.FFmpegCommands;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
    public TextField cordY, cordX, fontSize, fontBorder;
    @FXML
    public TextArea text;
    @FXML
    public ColorPicker boxColor, fontColor;
    @FXML
    public Slider boxOpacity;
    @FXML
    public Pane boxOptions;
    private CommonHelper Helper;

    public void initialize() {
        Helper = new CommonHelper(mediaPlayer, mediaView, () -> mediaView.getScene().addEventHandler(KeyEvent.KEY_PRESSED, Helper.keyListener));

        TextField[] fieldArray = {cordX, cordY, fontSize, fontBorder};
        for (var field: fieldArray) field.setTextFormatter(Helper.getNumberFormatter());

        boxOptions.setDisable(true);
        Helper.setMediaItems();

    }
    public void AddTextToVideo() throws IOException {
        if (text.getText() == null || text.getText().trim().equals("")) {
            Helper.setAlert("addText.control." + text.getId(), Alert.AlertType.ERROR);
            return;
        }
        List<TextField> fieldArray = new ArrayList<>(List.of(cordX, cordY, fontSize, fontBorder));

        var box = "";
        if (!boxOptions.isDisabled()) {
            box = String.format(":box=%s:boxcolor=%s@%s", 1 /* Enable box */, boxColor.getValue().toString(), boxOpacity.getValue() / 100);
        }

        for (var field: fieldArray) {
            if (Helper.isInvalid(field)){
                Helper.setAlert("addText.control." + field.getId(), Alert.AlertType.ERROR);
                return;
            }
        }


        var videoFilter = String.format("drawtext=text='%s':fontsize=%s:fontcolor=%s:x=%s:y=%s:borderw=%s%s", text.getText(), fontSize.getText(), fontColor.getValue().toString(),cordX.getText(), cordY.getText(), fontBorder.getText(), box);
        FFmpegCommands.GenerateTextCommand(videoFilter);
    }

    public void AddBoxClick(ActionEvent actionEvent) {
        boxOptions.setDisable( !((CheckBox)actionEvent.getSource()).isSelected() );
    }

    public void mediaClick(MouseEvent mouseEvent) {
        var coords = Helper.getAccurateCoordinates(mouseEvent, mediaView);
        cordX.setText(String.valueOf((coords[0])));
        cordY.setText(String.valueOf(coords[1]));
    }
}
