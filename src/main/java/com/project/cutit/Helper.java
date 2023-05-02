package com.project.cutit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ResourceBundle;

public class Helper {

    private MediaPlayer _mediaPlayer;
    public Helper() {}

    public void setPlayer(MediaPlayer plr){
        _mediaPlayer = plr;
    }
    public boolean isInvalid(TextField field) { return field.getText().isEmpty(); }
    public static String normalizePath(String original){ return original.split("/", 2)[1].replaceAll("%20", " "); }

    public static String getDesktop() {return System.getProperty("user.home") + "/Desktop";}

    public final EventHandler<KeyEvent> keyListener = event -> {
        if(event.getCode() == KeyCode.SPACE) {
            Toggle();
        }
        event.consume();
    };

    public void OnDragOver(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != dragEvent.getTarget()
                && dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    public void setAlert(String id) {
        var rb = ResourceBundle.getBundle("com.project.cutit.translation", Main.getLocale());

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        //TODO translate
        alert.setContentText(String.format("%s field is empty", rb.getString("addImage.control." + id)));
        alert.show();
    }

    public File OpenFileDialog(ActionEvent actionEvent) {
        var openButton = (Button)actionEvent.getSource();

        //TODO Translate

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return fileChooser.showOpenDialog(openButton.getScene().getWindow());
    }

    public TextFormatter<String> getNumberFormatter() {
        return new TextFormatter<>(change-> {
            String text1 = change.getText();
            if (text1.matches("[0-9]*")) {
                return change;
            }
            return null;
        });
    }

    public void Toggle(){
        boolean playing = _mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if(playing){
            _mediaPlayer.pause();
        }else{
            _mediaPlayer.play();
        }
    }
}
