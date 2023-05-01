package com.project.cutit.controllers;

import com.project.cutit.Main;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ModuleController {
    public void ToModule(ActionEvent actionEvent) {
        var button = (Button)actionEvent.getSource();
        Main.switchScene((String) button.getUserData());
    }
}
