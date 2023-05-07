package com.project.cutit.helpers;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

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
        //Save temp as specified name in specified location
    }

    public void WindowExit(ActionEvent actionEvent) {
        //IDEA -> File -> EXIT
    }

    public void ModuleSwitch(ActionEvent actionEvent) {
        //Hide / Close window
        //Switch
    }

    public void AboutWindow() {
        //override in controller class
        //set alert with information about this window
    }
}
