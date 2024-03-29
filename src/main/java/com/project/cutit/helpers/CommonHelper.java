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
    private MediaView _mediaView;
    private Runnable _runnable = () -> {};
    public CommonHelper() {}

    public CommonHelper(MediaPlayer plr, MediaView view) {
        _mediaPlayer = plr;
        _mediaView = view;

        CommonHelperManager.registerHelper(this);
    }

    public CommonHelper(MediaPlayer plr, MediaView view, Runnable runnable) {
        _mediaPlayer = plr;
        _mediaView = view;
        _runnable =  runnable;

        CommonHelperManager.registerHelper(this);
    }
    public MediaView getMediaView() { return _mediaView; }
    public MediaPlayer getMediaPlayer() { return _mediaPlayer; }

    public void setPlayer(MediaPlayer plr) {
        _mediaPlayer = plr;
    }

    public void setRunnable(Runnable runnable) {
        _runnable = runnable;
    }

    public void setMediaItems() {
        _mediaPlayer = new MediaPlayer(Main.getMedia());

        _mediaPlayer.setOnReady(() -> {
            _runnable.run();
            _mediaView.setMediaPlayer(_mediaPlayer);
        });

        _mediaPlayer.setOnError(this::setMediaItems);
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


    public Alert setAlert(String key, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.setTitle(I18n_Helper.getTranslation("alert"));
        alert.setHeaderText("");
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

    public void Toggle() {
        boolean playing = _mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);

        if (playing) {
            _mediaPlayer.stop();
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
