package com.project.cutit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

import static java.util.ResourceBundle.getBundle;

public class Main extends Application {
    private static String filePath;
    private static Stage stage;
    private static Locale projectLocale = Locale.forLanguageTag("en-GB");

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        fxmlLoader.setResources(getBundle(Main.class.getPackageName()+".translation", projectLocale));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
        Main.stage = stage;
    }
    public static void switchScene(String fxmlFile) {
        var filename = CheckFilename(fxmlFile);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(filename));
            loader.setResources(getBundle(Main.class.getPackageName()+".translation", projectLocale));

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

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    public static String CheckFilename(String fxmlFile) {
        var a = fxmlFile.split("(\\.)");
        if (a.length == 1){
            return fxmlFile + ".fxml";
        }
        return fxmlFile;
    }
    public static void setLocale(String newLocale){
        projectLocale = Locale.forLanguageTag(newLocale);
    }
    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String newValue) {
        filePath = newValue;
    }

    public static void main(String[] args) { launch(args); }
}