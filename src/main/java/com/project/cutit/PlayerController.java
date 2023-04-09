package com.project.cutit;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import static java.util.ResourceBundle.getBundle;

public class PlayerController {
    @FXML
    public MediaView mediaView;
    @FXML
    public Pane playerPane;
    @FXML
    public Button toggleButton;
    private MediaPlayer mediaPlayer;

    public void initialize() {
        Media media = Main.getMedia();

        System.out.println(media.getSource());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> playerPane.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener));
        mediaView.setMediaPlayer(mediaPlayer);

        //mediaView.setPreserveRatio(true);

        System.out.println(mediaView.getFitHeight());

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
            toggleButton.setText(getBundle(getClass().getPackageName()+".translation").getString("player.button.play"));
        }else{
            mediaPlayer.play();
            toggleButton.setText(getBundle(getClass().getPackageName()+".translation").getString("player.button.pause"));
        }

    }
    public void Reset(){ mediaPlayer.stop(); }

}