package com.project.cutit.helpers;

import com.project.cutit.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public abstract class MenuBarHelper {

    public static boolean isOpen = false;
    public void LanguageSwitch(ActionEvent actionEvent) {
        var button = (MenuItem)actionEvent.getSource();
        I18n_Helper.setProjectLocale( button.getUserData().toString() );

    }

    public void KeepOpen(ActionEvent actionEvent) {
        var menuItem = (CheckMenuItem) actionEvent.getSource();
        isOpen = menuItem.isSelected();
    }

    public void SaveAs(ActionEvent actionEvent) {
        var control = (Control) actionEvent.getSource();
        //Save temp as specified name in specified location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n_Helper.getTranslation("fileChooser.saveAs.title"));
        File file = fileChooser.showSaveDialog(control.getScene().getWindow());
        if (file != null) {
            // Do something with the selected file...
        }
    }

    public void WindowExit(ActionEvent actionEvent) {
        var Helper = new CommonHelper();
        if (Helper.setAlert("alert.confirm", Alert.AlertType.CONFIRMATION).getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE)
            Platform.exit();

    }


    public void ModuleSwitch(ActionEvent actionEvent) {
        var menuItem = (Control) actionEvent.getSource();
        var userData = menuItem.getUserData().toString();

        if (!isOpen) {
            var windows = Window.getWindows();
            var lastWindow = windows.get(windows.size() - 1);

            for (var thisWindow: windows) {
                if (thisWindow == lastWindow) {
                    continue;
                }
                var stageWindow = (Stage) thisWindow;
                stageWindow.close();
            }

            Main.switchScene(userData);

        } else Main.stayAndSwitch(userData, (Stage) menuItem.getScene().getWindow());
    }

    public void AboutWindow() {
        //override in controller class
        //set alert with information about this window
    }
}
