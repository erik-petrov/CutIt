package com.project.cutit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.lang3.math.Fraction;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.ResourceBundle.getBundle;

public class Main extends Application {
    private static Media Media;
    private static Stage Stage;
    private static FFmpeg FFmpeg;
    private static FFprobe FFprobe;
    private static String temporaryFilePath = System.getenv("APPDATA")+"/temp.mp4";

    public static FFmpeg FFmpeg;
    public static FFprobe FFprobe;
    private static Locale projectLocale = Locale.forLanguageTag("en-GB");

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("startup.fxml"));
        fxmlLoader.setResources(getBundle("com.project.cutit.translation", projectLocale));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CutIt Media player!");
        stage.setScene(scene);
        stage.show();
        try{
            FFmpeg = new FFmpeg("src\\main\\resources\\ffmpeg\\bin\\ffmpeg.exe");
            FFprobe = new FFprobe("src\\main\\resources\\ffmpeg\\bin\\ffprobe.exe");

        }catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("PLEASE INSTALL ffmpeg INTO resources FOLDER and rename the folder to 'ffmpeg'");
            Optional<ButtonType> result = a.showAndWait();
            if(result.get() != null){
                System.exit(0);
            }
        }
        Stage = stage;
    }
    public static void switchScene(String fxmlFile) {
        var filename = CheckFilename(fxmlFile);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(filename));
            loader.setResources(getBundle(Main.class.getPackageName()+".translation", projectLocale));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            //Screen screen = Screen.getPrimary();

            // Get the size of the screen
            //Rectangle2D screenBounds = screen.getVisualBounds();

            // Set the position of the stage to center it
            //Stage.setX((screenBounds.getWidth() - Stage.getWidth()) / 2);
            //Stage.setY((screenBounds.getHeight() - Stage.getHeight()) / 2);

            Stage stage = new Stage();

            stage.setScene(scene);
            if(fxmlFile != "modules"){
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

    public static void setMedia(Media newValue) {
        Media = newValue;
    }

    public static void main(String[] args) { launch(args); }

    public static String normalizePath(String original){ return original.split("/", 2)[1].replaceAll("%20", " "); }

    public static String getDesktop() {return System.getProperty("user.home") + "/Desktop";}

    public static FFmpegProbeResult getMediaData(Media media) throws IOException {
        return FFprobe.probe(normalizePath(media.getSource()));
    }

    public static void GenerateCutCommand(Integer from, Integer to) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Stage);

        FFmpegProbeResult data = getMediaData(Media);

        Integer audioChannels = data.getStreams().get(0).channels;
        String format = data.getFormat().format_name;
        Integer framerate = data.getStreams().get(0).avg_frame_rate.intValue();
        Long duration = (long)(to.doubleValue() - from.doubleValue());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(normalizePath(Media.getSource()))     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput(temporaryFilePath)   // Filename for the destination

                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(data.getStreams().get(0).sample_rate)
                .setAudioBitRate(data.getStreams().get(0).bit_rate)

                .setVideoCodec("libx264")     // Video using x264
                .setVideoFrameRate(framerate, 1)
                .setVideoResolution(Media.getWidth(), Media.getHeight()) // at 640x480 resolution maybe changed

                .setStartOffset(from, TimeUnit.MILLISECONDS)
                .setDuration(duration, TimeUnit.MILLISECONDS)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done(); // Allow FFmpeg to use experimental specs
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        executor.createJob(builder).run();
    }
    public static void GenerateSpeedCommand(Double factor) throws IOException {
        FFmpegProbeResult data = getMediaData(Media);

        if(factor < 0){
            factor = 1/Math.abs(factor);
        }

        if(factor == 0){
            factor = 1.0;
        }

        Integer sampleRate = data.getStreams().get(0).sample_rate > 0 ? data.getStreams().get(0).sample_rate : 48_000;
        Long bitRate = data.getStreams().get(0).bit_rate > 0 ? data.getStreams().get(0).bit_rate : 32768;
        Integer framerate = data.getStreams().get(0).avg_frame_rate.intValue();
        Integer audioChannels = data.getStreams().get(0).channels == 0 ? 1 : data.getStreams().get(0).channels; //if is 0 then 1
        String format = data.getFormat().format_name.split(",")[0]; //is mov cuz of [0], dunno if we'll change it
        String filter = factor >= 0.5 ? "[0:v]setpts="+1/factor+"*PTS[v];[0:a]atempo="+factor+"[a]" : "[0:v]setpts="+1/factor+"*PTS[v]"; //if slowdown then no audio
        String[] extras = factor >= 0.5 ? new String[]{"-map", "[a]", "-map", "[v]"} : new String[]{"-map", "[v]"};

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(normalizePath(Media.getSource()))     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput(temporaryFilePath)   // Filename for the destination
                .addExtraArgs(extras)
                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec("aac")
                .setAudioSampleRate(sampleRate)
                .setAudioBitRate(bitRate)

                .setVideoCodec("libx264")
                .setVideoFrameRate(framerate, 1)
                .setVideoResolution(Media.getWidth(), Media.getHeight())

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
                .setComplexFilter(filter);
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        executor.createJob(builder).run();
    }
}