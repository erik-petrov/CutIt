package com.project.cutit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static java.util.ResourceBundle.getBundle;

public class Main extends Application {
    private static Media Media;
    public static FFmpeg FFmpeg;
    public static FFprobe FFprobe;
    private static String temporaryFilePath;

    private static Locale projectLocale = Locale.forLanguageTag("en-GB");

    public void start(Stage stage) throws IOException {
        temporaryFilePath = System.getenv("APPDATA")+"/CutIt/temp.mp4";
        new File(System.getenv("APPDATA")+"/CutIt/").mkdirs();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        fxmlLoader.setResources(getBundle("com.project.cutit.translation", projectLocale));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getCss("style"));
        scene.getStylesheets().add(getCss("startup"));
        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
        try{
            FFmpeg = new FFmpeg("src\\main\\resources\\ffmpeg\\bin\\ffmpeg.exe");
            FFprobe = new FFprobe("src\\main\\resources\\ffmpeg\\bin\\ffprobe.exe");

        }catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("PLEASE INSTALL ffmpeg INTO resources FOLDER and rename the folder to 'ffmpeg'");
            a.showAndWait();
            System.exit(0);
        }
    }

    private static String getCss(String styleFilename){
        var filename = CheckFilename(styleFilename, ".css");
        var styleResource = Main.class.getResource("/css/" + filename);
        return styleResource != null ? styleResource.toExternalForm() : "";
    }

    public static void switchScene(String fxmlFile) {
        var filename = CheckFilename(fxmlFile);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(filename));
            loader.setResources(getBundle(Main.class.getPackageName()+".translation", projectLocale));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getCss("style"));
            try{
                scene.getStylesheets().add(getCss(filename.split("\\.")[0]));
            }catch (Exception ignored){
                System.out.println("Stylesheet missing");
            }

            Stage stage = new Stage();

            stage.setScene(scene);
            if(!fxmlFile.equals("modules")){
                stage.showAndWait();
            }
            else{
                stage.show();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
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
    public static void setLocale(String newLocale){
        projectLocale = Locale.forLanguageTag(newLocale);
    }

    public static Locale getLocale(){ return projectLocale; }

    public static Media getMedia() { return Media; }

    public static void setMedia(Media newValue) { Media = newValue; }

    public static String getAppDataFile() { return temporaryFilePath; }
    public static void updateMediaName(String name) { temporaryFilePath = System.getenv("APPDATA")+"/CutIt/"+name+".mp4"; }

    public static void main(String[] args) { launch(args); }
}