package com.project.cutit.helpers;

import com.project.cutit.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.io.File;

public class Helper {

    private MediaPlayer _mediaPlayer;
    public Helper() {}

    public void setPlayer(MediaPlayer plr) {
        _mediaPlayer = plr;
    }
    public void centerControl(Control control) { control.setLayoutX(control.getParent().getLayoutX() - control.getMaxWidth()); }
    public boolean isInvalid(TextField field) { return field.getText().isEmpty(); }
    public static String normalizePath(String original){ return original.split("/", 2)[1].replaceAll("%20", " "); }

    public static String getDesktop() { return System.getProperty("user.home") + "/Desktop"; }

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
    public void setMediaItems(MediaView mediaView) {
        setMediaItems(mediaView, () -> {});
    }

    public void setMediaItems(MediaView mediaView, Runnable runnable) {
        _mediaPlayer = new MediaPlayer(Main.getMedia());
        _mediaPlayer.setOnError(() -> setMediaItems(mediaView, runnable));
        _mediaPlayer.setOnReady(() -> {
                mediaView.setMediaPlayer(_mediaPlayer);
                mediaView.addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
                System.out.println(mediaView.getFitWidth());
                System.out.println(Main.getMedia().getOnError());
                runnable.run();
        });
    }


    public void setAlert(String id) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n_Helper.getTranslation("general.error"));
        alert.setContentText(I18n_Helper.getTranslation("addImage.control." + id) + " " + I18n_Helper.getTranslation("error.fieldText"));
        alert.show();
    }

    public File OpenFileDialog(ActionEvent actionEvent) {
        var openButton = (Button)actionEvent.getSource();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n_Helper.getTranslation("fileChooser.open.title"));
        fileChooser.setInitialDirectory(new File(getDesktop()));

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

    public int[] getAccurateCoordinates(MouseEvent event, MediaView mediaView) {
        double viewWidth = _mediaPlayer.getMedia().getWidth();
        double viewHeight = _mediaPlayer.getMedia().getHeight();
        double videoWidth = mediaView.getFitWidth();
        double videoHeight = mediaView.getFitHeight();

        // calculate the scaling factor
        double scaleX = viewWidth / videoWidth;
        double scaleY = viewHeight / videoHeight;

        // get the click position within the MediaView
        double clickX = event.getX();
        double clickY = event.getY();

        // calculate the accurate x and y coordinates based on the scaling factor
        double accurateX = clickX * scaleX;
        double accurateY = clickY * scaleY;
        return new int[] {(int) accurateX, (int) accurateY};
    }

    public void Toggle(){
        boolean playing = _mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);

        if (playing) {
            _mediaPlayer.pause();
        } else {
            _mediaPlayer.play();
        }
    }
}
