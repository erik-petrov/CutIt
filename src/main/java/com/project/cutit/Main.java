package com.project.cutit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.ResourceBundle.getBundle;

public class Main extends Application {
    private static Media Media;
    private static Stage Stage;
    private static FFmpeg FFmpeg;
    private static FFprobe FFprobe;
    private static Locale projectLocale = Locale.forLanguageTag("en-GB");

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        fxmlLoader.setResources(getBundle(Main.class.getPackageName()+".translation", projectLocale));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
        FFmpeg = new FFmpeg("C:\\Users\\erikp\\IdeaProjects\\CutIt\\src\\main\\resources\\ffmpeg\\bin\\ffmpeg.exe");
        FFprobe = new FFprobe("C:\\Users\\erikp\\IdeaProjects\\CutIt\\src\\main\\resources\\ffmpeg\\bin\\ffprobe.exe");
        Stage = stage;
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
            Stage.setX((screenBounds.getWidth() - Stage.getWidth()) / 2);
            Stage.setY((screenBounds.getHeight() - Stage.getHeight()) / 2);

            Stage.setScene(scene);
            Stage.show();

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
    public static Media getMedia() {
        return Media;
    }

    public static void setMedia(Media newMedia) { Media = newMedia; }

    public static void main(String[] args) { launch(args); }
    public static void GenerateCommand(String format, Integer audioChannels, Integer framerate, Integer from, Integer to){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Stage);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Media.getSource())     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput(selectedDirectory.getPath()+"/output"+format)   // Filename for the destination

                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(48_000)  // at 48KHz
                .setAudioBitRate(32768)

                .setVideoCodec("libx264")     // Video using x264
                .setVideoFrameRate(framerate, 1)     // at 24 frames per second
                .setVideoResolution(Media.getWidth(), Media.getHeight()) // at 640x480 resolution maybe changed

                .setStartOffset(from, TimeUnit.MILLISECONDS)
                .setDuration((long) (Media.getDuration().toMillis() - to.doubleValue()), TimeUnit.MILLISECONDS)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done(); // Allow FFmpeg to use experimental specs
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        executor.createJob(builder).run();
    }
}