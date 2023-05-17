package com.project.cutit;

import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.I18n_Helper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import java.io.File;
import java.io.IOException;

import static com.project.cutit.helpers.MenuBarHelper.isOpen;

public class Main extends Application {
    private static Media Media;
    public static FFmpeg FFmpeg;
    public static FFprobe FFprobe;
    private static String temporaryFilePath;

    public void start(Stage stage) throws IOException {
        temporaryFilePath = System.getenv("APPDATA")+"/CutIt/temp.mp4";
        new File(System.getenv("APPDATA")+"/CutIt/").mkdirs();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        fxmlLoader.setResources(I18n_Helper.getTranslationBundle());
        Parent root = fxmlLoader.load();
        root.setId("startup");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getCss("style"));
        scene.getStylesheets().add(getCss("startup"));
        stage.setTitle(I18n_Helper.getTranslation("cutit"));
        stage.setResizable(false);
        stage.onCloseRequestProperty();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        try{
            FFmpeg = new FFmpeg("src\\main\\resources\\ffmpeg\\bin\\ffmpeg.exe");
            FFprobe = new FFprobe("src\\main\\resources\\ffmpeg\\bin\\ffprobe.exe");

        }catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please INSTALL ffmpeg INTO resources FOLDER and RENAME the folder to 'ffmpeg'");
            a.showAndWait();
            System.exit(0);
        }
    }

    private static String getCss(String styleFilename){
        var filename = CheckFilename(styleFilename, ".css");
        var styleResource = Main.class.getResource("/css/" + filename);
        return styleResource != null ? styleResource.toExternalForm() : "";
    }

    public static void switchScene(Stage stage, String fxmlFile) {
        var filename = CheckFilename(fxmlFile);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(filename));
            loader.setResources(I18n_Helper.getTranslationBundle());

            Parent root = loader.load();
            root.setId(fxmlFile);
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getCss("style"));
            try {
                scene.getStylesheets().add(getCss(filename.split("\\.")[0]));
            } catch (Exception ignored) {
                System.out.println("Stylesheet missing");
            }

            stage.setOnCloseRequest(CommonHelper.closeClick);
            stage.setTitle(I18n_Helper.getTranslation("cutit"));
            stage.setScene(scene);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void switchScene(String fxmlFile) {
        var filename = CheckFilename(fxmlFile);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(filename));
            loader.setResources(I18n_Helper.getTranslationBundle());

            var lastStage = getLastStage();

            Parent root = loader.load();
            root.setId(fxmlFile);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getCss("style"));

            try {
                scene.getStylesheets().add(getCss(filename.split("\\.")[0]));
            } catch (Exception ignored){

                System.out.println("Stylesheet missing");
            }

            Stage stage = !isOpen ? lastStage : new Stage();
            stage.setResizable(false);
            stage.setOnCloseRequest(CommonHelper.closeClick);
            stage.setTitle(I18n_Helper.getTranslation("cutit"));
            stage.setScene(scene);
            stage.centerOnScreen();
            if (!isOpen) {
                lastStage.close();
            }

            stage.show();


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Stage getLastStage() {
        var windows = Window.getWindows();
        try {
            return (Stage) windows.get(windows.size() - 1);
        }catch (Exception ignored) {
            return new Stage();
        }

    }

    public static String CheckFilename(String fxmlFile) {
        return CheckFilename(fxmlFile, ".fxml");
    }
    public static String CheckFilename(String file, String extension) {
        var a = file.split("(\\.)");
        if (a.length == 1){
            return file + extension;
        }
        return file;
    }
    public static Media getMedia() { return Media; }

    public static void setMedia(Media newValue) { Media = newValue; }

    public static String getAppDataFile() { return temporaryFilePath; }
    public static void updateMediaName(String name) { temporaryFilePath = System.getenv("APPDATA") + "/CutIt/" + name + ".mp4"; }

    public static void main(String[] args) { launch(args); }
}