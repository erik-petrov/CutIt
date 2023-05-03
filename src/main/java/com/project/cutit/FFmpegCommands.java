package com.project.cutit;

import com.project.cutit.controllers.ProgressController;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.project.cutit.FFmpegStreamHelper.*;
import static com.project.cutit.Main.FFmpeg;
import static com.project.cutit.Main.FFprobe;
import static java.util.ResourceBundle.getBundle;

public class FFmpegCommands {
    private static final String temporaryFilePath = Main.getAppDataFile();
    private static final FFmpegStreamHelper streamHelper = new FFmpegStreamHelper();
    public static void initiateProgressBar(Task<Void> task) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("progress.fxml"));
        loader.setResources(getBundle(Main.class.getPackageName()+".translation", Main.getLocale()));

        Parent root = loader.load();

        ProgressController cntrl = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        //important - change media to temp, so we change the edited video once again
        task.setOnSucceeded(event -> {
            Main.setMedia(new Media(new File(temporaryFilePath).toURI().toString()));
            stage.close();
        });
        cntrl.activateProgressBar(task);
        stage.setScene(scene);
        stage.show();
        new Thread(task).start();
    }

    public static void GenerateImageCommand(String imagePath, String filter) throws IOException {
        File imageFile = new File(imagePath);
        streamHelper.setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Helper.normalizePath(Main.getMedia().getSource()))
                .addInput(FFprobe.probe(imageFile.getAbsolutePath()))
                .addExtraArgs("-filter_complex", filter)
                .addOutput(Main.getAppDataFile())
                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec(audioCodec)
                .setAudioSampleRate(sampleRate)
                .setVideoFrameRate(frameRate, 1)
                .setAudioBitRate(bitRate)
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        Task<Void> task = new Task<>() {
            @Override
            public Void call(){
                executor.createJob(builder, new ProgressListener() {
                    final double duration_ns = duration * TimeUnit.MILLISECONDS.toNanos(1);
                    @Override
                    public void progress(Progress progress) {
                        double percentage = progress.out_time_ns / duration_ns;
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed) : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status).equals("continue") ? "Processing.." : "End");
                    }
                }).run();
                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateTextCommand(String filter) throws IOException {
        streamHelper.setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Helper.normalizePath(Main.getMedia().getSource()))
                .addOutput(Main.getAppDataFile())
                .setVideoFilter(filter)
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        Task<Void> task = new Task<>() {
            @Override
            public Void call(){
                executor.createJob(builder, new ProgressListener() {
                    final double duration_ns = duration * TimeUnit.MILLISECONDS.toNanos(1);
                    @Override
                    public void progress(Progress progress) {
                        double percentage = progress.out_time_ns / duration_ns;
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed) : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status).equals("continue") ? "Processing.." : "End");
                    }
                }).run();
                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateCutCommand(Integer from, Integer to) throws IOException {
        long duration = (long)(to.doubleValue() - from.doubleValue());
        streamHelper.setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Helper.normalizePath(Main.getMedia().getSource()))
                .overrideOutputFiles(true)
                .addOutput(temporaryFilePath)

                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec(audioCodec)        // using the aac codec
                .setAudioSampleRate(sampleRate)
                .setAudioBitRate(bitRate)
                .setVideoCodec(videoCodec)     // Video using x264
                .setVideoFrameRate(frameRate, 1)

                .setStartOffset(from, TimeUnit.MILLISECONDS)
                .setDuration(duration, TimeUnit.MILLISECONDS)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                executor.createJob(builder, new ProgressListener() {
                    final double duration_ns = duration * TimeUnit.MILLISECONDS.toNanos(1);
                    @Override
                    public void progress(Progress progress) {
                        double percentage = progress.out_time_ns / duration_ns;
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed) : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        //TODO: TRNSALTE
                        updateTitle(String.valueOf(progress.status).equals("continue") ? "Processing.." : "End");
                    }
                }).run();
                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateSpeedCommand(Double factor) throws IOException {
        streamHelper.setStreamData();

        if(factor < 0){
            factor = 1/Math.abs(factor);
        }

        if(factor == 0){
            factor = 1.0;
        }


        String filter = factor >= 0.5 ? "[0:v]setpts="+1/factor+"*PTS[v];[0:a]atempo="+factor+"[a]" : "[0:v]setpts="+1/factor+"*PTS[v]"; //if slowdown then no audio
        String[] extras = factor >= 0.5 ? new String[]{"-map", "[a]", "-map", "[v]"} : new String[]{"-map", "[v]"};

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(Helper.normalizePath(Main.getMedia().getSource()))     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput(temporaryFilePath)   // Filename for the destination
                .addExtraArgs(extras)

                .setAudioChannels(audioChannels)         // 1 - mono, 2 - stereo?
                .setAudioCodec(audioCodec)
                .setAudioSampleRate(sampleRate)
                .setAudioBitRate(bitRate)

                .setVideoCodec(videoCodec)
                .setVideoFrameRate(frameRate, 1)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done().setComplexFilter(filter);
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        Double finalFactor = factor;
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                executor.createJob(builder, new ProgressListener() {
                    final double duration_ns = (duration * TimeUnit.SECONDS.toNanos(1)) / finalFactor;
                    @Override
                    public void progress(Progress progress) {
                        double percentage = progress.out_time_ns / duration_ns;
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed) : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status));
                    }
                }).run();
                return null;
            }
        };
        initiateProgressBar(task);
    }
}
