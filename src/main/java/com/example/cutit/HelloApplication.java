package com.example.cutit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class HelloApplication extends Application {
    public MediaPlayer mediaPlayer;
    public Boolean playing = false;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        //AnchorPane pane = fxmlLoader.load();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyListener);

        Map<String, Object> fxmlNamespace = fxmlLoader.getNamespace();
        MediaView mediaView = (MediaView) fxmlNamespace.get("mediaView");

        VBox dragTarget = (VBox) fxmlNamespace.get("mainBox");

        dragTarget.setOnDragOver(event -> {
            if (event.getGestureSource() != dragTarget
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dragTarget.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                String path = db.getFiles().get(0).toURI().toString();

                Media media = new Media(path);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);

                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
    }

    private EventHandler<KeyEvent> keyListener = new EventHandler<>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.SPACE) {
                if(playing){
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.play();
                }
                playing = !playing;
            }
            event.consume();
        }
    };

    public static void main(String[] args) {launch();}
}