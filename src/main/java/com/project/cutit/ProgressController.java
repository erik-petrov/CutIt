package com.project.cutit;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressController extends Application {
    private Stage _stage;
    @FXML
    private ProgressBar progressBar = new ProgressBar();
    @FXML
    private Label timeLeft = new Label();
    @FXML
    private Label status = new Label();

    public void initialize() {}

    public ProgressController(){
}

    public ProgressController(Stage stage){
        _stage = stage;
        progressBar.setProgress(-1F);
    }

    public Stage getStage(){
        return _stage;
    }

    public void activateProgressBar(final Task<?> task)  {
        progressBar.progressProperty().bind(task.progressProperty());
        timeLeft.textProperty().bind(task.messageProperty());
        status.textProperty().bind(task.titleProperty());
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
