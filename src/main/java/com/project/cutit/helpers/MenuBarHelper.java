package com.project.cutit.helpers;

import com.project.cutit.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static com.project.cutit.Main.switchScene;

public abstract class MenuBarHelper {

    public static boolean isOpen = false;

    public void LanguageSwitch(ActionEvent actionEvent) {
        var menuItem = (MenuItem) actionEvent.getSource();
        String newLocale = menuItem.getUserData().toString();

        List<Window> openWindows = new ArrayList<>(Window.getWindows());

        I18n_Helper.setProjectLocale(newLocale);

        for (Window window : openWindows) {
            if (window instanceof Stage stage) {
                double oldX = stage.getX();
                double oldY = stage.getY();

                Main.switchScene(stage, stage.getScene().getRoot().getId());

                stage.setX(oldX);
                stage.setY(oldY);
            }
        }
    }

    public void KeepOpen(ActionEvent actionEvent) {
        var menuItem = (CheckMenuItem) actionEvent.getSource();
        isOpen = menuItem.isSelected();

        findAndSetCheckedMenuItems(isOpen);
    }

    private void findAndSetCheckedMenuItems(boolean selected) {
        for (Window window : Window.getWindows()) {
            Scene scene = window.getScene();
            if (scene != null) {
                MenuBar menuBar = (MenuBar) scene.lookup("#menubar");
                if (menuBar != null) {
                    for (MenuItem item : menuBar.getMenus().get(0).getItems()) {
                        if (item instanceof CheckMenuItem) {
                            ((CheckMenuItem) item).setSelected(selected);
                        }
                    }
                }
            }
        }
    }

    public void SaveAs(ActionEvent actionEvent) {
        var menuItem = (MenuItem) actionEvent.getSource();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n_Helper.getTranslation("fileChooser.saveAs.title"));
        fileChooser.setInitialDirectory(new File(CommonHelper.getDesktop()));
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("MP4 (*.mp4)", "*.mp4"));
        File file = fileChooser.showSaveDialog(menuItem.getParentPopup().getOwnerWindow().getScene().getWindow());
        if (file != null) {
            try {
                Files.copy(Paths.get(Main.getAppDataFile()), Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) { }
        }
    }

    public void WindowExit(ActionEvent actionEvent) {
        var Helper = new CommonHelper();
        if (Helper.setAlert("alert.confirm", Alert.AlertType.CONFIRMATION).getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE)
            Platform.exit();
    }


    public void ModuleSwitch(ActionEvent actionEvent) {
        switchScene( ((MenuItem) actionEvent.getSource()).getUserData().toString() );
    }

    public void AboutWindow(ActionEvent actionEvent) {
        var menuItem = (MenuItem) actionEvent.getSource();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(I18n_Helper.getTranslation("alert.info"));
        alert.setHeaderText(null);

        var translation = I18n_Helper.getTranslation("about." +  menuItem.getParentPopup().getOwnerWindow().getScene().getRoot().getId() );
        translation = translation.replace("\\n", System.lineSeparator());
        TextArea textArea = new TextArea(translation);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}
