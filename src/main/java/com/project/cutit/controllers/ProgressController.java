package com.project.cutit.controllers;

import com.project.cutit.helpers.MenuBarHelper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressController extends MenuBarHelper {
    @FXML
    private ProgressBar progressBar = new ProgressBar();
    @FXML
    private Label timeLeft = new Label();
    @FXML
    private Label status = new Label();

    public void initialize() {}

    public ProgressController(){
}

    public void activateProgressBar(final Task<?> task)  {
        progressBar.progressProperty().bind(task.progressProperty());
        timeLeft.textProperty().bind(task.messageProperty());
        status.textProperty().bind(task.titleProperty());
    }
}
