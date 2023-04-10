package com.project.cutit;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;

import java.io.File;

public class StartupController {

    public void OnDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            Media media = new Media(dragboard.getFiles().get(0).toURI().toString());
            Main.setMedia(media);
            Main.switchScene("modules");
            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    public void OnDragOver(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != dragEvent.getTarget()
                && dragEvent.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    public void OpenFileDialog(ActionEvent actionEvent) {
        var openButton = (Button)actionEvent.getSource();

        FileChooser fileChooser = new FileChooser();

        // Set the title of the file chooser dialog
        fileChooser.setTitle("Open File");

        // Set the initial directory of the file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Show the file chooser dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(openButton.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile == null) {
            throw new RuntimeException();
        }
        Media media = new Media(selectedFile.toURI().toString());
        if(media.getError() != null){
            throw new RuntimeException();
        }
        Main.setMedia(media);
        Main.switchScene("modules");
    }
    public void LanguageEt(){
        Main.setLocale("et-ET");
        Main.switchScene("startup");
    }
    public void LanguageEn(){
        Main.setLocale("en-GB");
        Main.switchScene("startup");

    }
}
