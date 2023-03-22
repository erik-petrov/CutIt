package com.project.cutit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static String filePath = "C:\\Users\\Calm\\IdeaProjects\\CutIt\\src\\main\\resources\\Videos\\sample.mp4";
    private static Stage stage;
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
        Main.stage = stage;
    }

    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Screen screen = Screen.getPrimary();

            // Get the size of the screen
            Rectangle2D screenBounds = screen.getVisualBounds();

            // Set the position of the stage to center it
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            stage.setScene(scene);
            stage.show();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String newValue) {
        filePath = newValue;
    }

    public static void main(String[] args) {launch();}
}