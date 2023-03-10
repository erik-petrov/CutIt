package com.example.cutit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.Objects;


public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField seekTextField;
    @FXML
    public MediaView mediaView;

    @FXML
    protected void onTextInput(KeyEvent event) {
        Character evChar = null;
        KeyCode code = event.getCode();
        if(code.isDigitKey() || code.isLetterKey()){ //text != ""
            evChar = event.getText().charAt(0);
        }

        MediaPlayer plr = mediaView.getMediaPlayer();

        if(plr == null){
            System.out.println("Player is null");
            return;
        }

        Media m = plr.getMedia();
        Duration target = new Duration(0.0);

        if(m.getSource() == ""){
            System.out.println("No source");
            return;
        }

        if(evChar != null){
            //if its isnt a digit or ":", return
            if(!(Character.isDigit(evChar) || evChar.equals(':'))){
                System.out.println("Invalid char");
                System.out.println(Character.isDigit(evChar));
                System.out.println(evChar.equals(":"));
                return;
            }
        }

        String time = seekTextField.getText();
        String[] timeArr = time.split(":");
        System.out.println(time);
        switch (timeArr.length){
            case 1:
                target = target.add(new Duration(
                        Integer.parseInt(timeArr[0])
                ));
                break;
            case 2:
                target = target.add(new Duration(
                        Integer.parseInt(timeArr[0]) * 60
                                +
                           Integer.parseInt(timeArr[1])
                ));
                break;
            case 3:
                target = target.add(new Duration(
                        Integer.parseInt(timeArr[0]) * 60
                        +
                        Integer.parseInt(timeArr[1])
                        +
                        Float.parseFloat(timeArr[2])/1000
                ));
                break;
            default:
                break;
        }
        plr.seek(target.multiply(1000));
        System.out.println("currently at: "+plr.getCurrentTime());
        event.consume();
    }
}