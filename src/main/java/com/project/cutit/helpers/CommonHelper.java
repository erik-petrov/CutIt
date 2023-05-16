package com.project.cutit.helpers;

import com.project.cutit.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

public class CommonHelper {

    private MediaPlayer _mediaPlayer;
    public CommonHelper() {}

    public void setPlayer(MediaPlayer plr) {
        _mediaPlayer = plr;
    }
    public boolean isInvalid(TextField field) { return field.getText().isEmpty(); }
    public static String normalizePath(String original){ return original.split("/", 2)[1].replaceAll("%20", " "); }

    public static String getDesktop() { return System.getProperty("user.home") + "/Desktop"; }

    public static final EventHandler<WindowEvent> closeClick = event -> {
        var stage = (Stage) event.getSource();
        var rootId = stage.getScene().getRoot().getId();
        stage.close();
        if (!MenuBarHelper.isOpen) {

            if (!rootId.equals("modules") && !rootId.equals("startup")) {
                Main.switchScene("modules");
            }

            else if (rootId.equals("modules")) {
                Main.switchScene("startup");
            }
        }

        event.consume();
    };

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
    public void setMediaItems(MediaView mediaView, MediaPlayer mediaPlayer) {
        setMediaItems(mediaView, mediaPlayer,() -> {});
    }

    public static void setMediaItems(MediaView mediaView, MediaPlayer mediaPlayer, Runnable runnable) {
        var helper = new CommonHelper();
        helper.setPlayer(mediaPlayer);

        mediaPlayer = new MediaPlayer(Main.getMedia());
        MediaPlayer finalMediaPlayer = mediaPlayer;
        mediaPlayer.setOnError(() -> setMediaItems(mediaView, finalMediaPlayer, runnable));
        mediaPlayer.setOnReady(() -> {
                mediaView.setMediaPlayer(finalMediaPlayer);
                mediaView.addEventHandler(KeyEvent.KEY_PRESSED, helper.keyListener);
                System.out.println(mediaView.getFitWidth());
                System.out.println(Main.getMedia().getOnError());
                runnable.run();
        });
    }


    public Alert setAlert(String key, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.setTitle(I18n_Helper.getTranslation("alert"));

        var alertText = I18n_Helper.getTranslation(key);
        if (type == Alert.AlertType.ERROR) alertText+= " " + I18n_Helper.getTranslation("error.fieldText");

        alert.setContentText(alertText);
        alert.showAndWait();
        return alert;
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

    public boolean CheckValues(String ... args){
        for (String arg : args) {
            if(arg.isEmpty()){
                return false;
            }
        }
        return true;
    }
}
