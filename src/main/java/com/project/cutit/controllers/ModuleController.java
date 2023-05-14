package com.project.cutit.controllers;

import com.project.cutit.Main;
import com.project.cutit.helpers.MenuBarHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ModuleController extends MenuBarHelper {
    public void ToModule(ActionEvent actionEvent) {
        var button = (Button)actionEvent.getSource();
        Main.switchScene( button.getUserData().toString() );
    }
}
