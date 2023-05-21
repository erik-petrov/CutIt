package com.project.cutit.controllers;

import com.project.cutit.Main;
import com.project.cutit.helpers.I18n_Helper;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;

import java.io.File;

public class StartupController extends MenuBarHelper {

    private void loadAndSwitch(Media media, String where){
        Main.setMedia(media);
        Main.switchScene(where);
    }

    public void OnDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();

        boolean success = false;

        if (dragboard.hasFiles()) {
            Media media = new Media(dragboard.getFiles().get(0).toURI().toString());
            loadAndSwitch(media, "modules");
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

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(I18n_Helper.getTranslation("fileChooser.filter"), "*.mp4", "*.wav","*.avi","*.mov","*.mkv","*.m4v");
        fileChooser.getExtensionFilters().add(filter);

        // Set the title of the file chooser dialog
        fileChooser.setTitle(I18n_Helper.getTranslation("fileChooser.open.title"));
        // Set the initial directory of the file chooser
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Desktop/"));

        // Show the file chooser dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(openButton.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile != null) {
            Media media = new Media(selectedFile.toURI().toString());
            loadAndSwitch(media, "modules");
        }
    }

}
