package com.project.cutit;

import com.project.cutit.controllers.ProgressController;
import com.project.cutit.helpers.CommonHelper;
import com.project.cutit.helpers.I18n_Helper;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.project.cutit.Main.*;
import static com.project.cutit.helpers.FFmpegStreamHelper.*;

public class FFmpegCommands {
    private static String temporaryFilePath = Main.getAppDataFile();
    private static void updateTempFilePath() { temporaryFilePath = Main.getAppDataFile(); }
    public static String genName() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();

        Random rnd = new Random();
        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        return salt.toString();
    }
    public static void initiateProgressBar(Task<Void> task) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("progress.fxml"));
        loader.setResources(I18n_Helper.getTranslationBundle());

        Parent root = loader.load();
        ProgressController cntrl = loader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        //important - change media to temp, so we change the edited video once again
        task.setOnSucceeded(event -> {
            setMedia(new Media(new File(temporaryFilePath).toURI().toString()));
            updateMediaName(genName());
            updateTempFilePath();
            stage.close();
        });

        cntrl.activateProgressBar(task);
        stage.setScene(scene);
        stage.show();
        new Thread(task).start();
    }

    public static void GenerateImageCommand(String imagePath, String filter) throws IOException {
        File imageFile = new File(imagePath);
        setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(CommonHelper.normalizePath(Main.getMedia().getSource()))
                .addInput(FFprobe.probe(imageFile.getAbsolutePath()))
                .addExtraArgs("-filter_complex", filter)
                .addOutput(Main.getAppDataFile())
                .setAudioChannels(audioChannels)
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
                        updateTitle(String.valueOf(progress.status).equals("continue")
                                ? I18n_Helper.getTranslation("progressBar.progress")
                                : I18n_Helper.getTranslation("progressBar.end"));
                    }
                }).run();
                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateCropCommand(String filter) throws IOException {
        setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(CommonHelper.normalizePath(Main.getMedia().getSource()))
                .overrideOutputFiles(true)
                .addOutput(Main.getAppDataFile())
                .addExtraArgs("-filter:v",filter)
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
        setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(CommonHelper.normalizePath(Main.getMedia().getSource()))
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
                        long timeLeft = duration_ns - progress.out_time_ns > 0
                                ? (long) ((duration_ns - progress.out_time_ns) / progress.speed)
                                : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status).equals("continue")
                                ? I18n_Helper.getTranslation("progressBar.progress")
                                : I18n_Helper.getTranslation("progressBar.end"));
                    }
                }).run();

                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateCutCommand(Integer from, Integer to) throws IOException {
        long duration = (long)(to.doubleValue() - from.doubleValue());
        setStreamData();
        String inputPath = CommonHelper.normalizePath(Main.getMedia().getSource());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputPath)
                .overrideOutputFiles(true)
                .addOutput(temporaryFilePath)

                .setAudioChannels(audioChannels)
                .setAudioCodec(audioCodec)
                .setAudioSampleRate(sampleRate)
                .setAudioBitRate(bitRate)
                .setVideoCodec(videoCodec)
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
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed)
                                : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status).equals("continue")
                                ? I18n_Helper.getTranslation("progressBar.progress")
                                : I18n_Helper.getTranslation("progressBar.end"));
                    }
                }).run();

                return null;
            }
        };
        initiateProgressBar(task);
    }
    public static void GenerateSpeedCommand(Double factor, String[] extras, String filter) throws IOException {
        setStreamData();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(CommonHelper.normalizePath(Main.getMedia().getSource()))
                .overrideOutputFiles(true)

                .addOutput(temporaryFilePath)
                .addExtraArgs(extras)

                .setAudioChannels(audioChannels)
                .setAudioCodec(audioCodec)
                .setAudioSampleRate(sampleRate)
                .setAudioBitRate(bitRate)

                .setVideoCodec(videoCodec)
                .setVideoFrameRate(frameRate, 1)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done().setComplexFilter(filter);
        FFmpegExecutor executor = new FFmpegExecutor(FFmpeg, FFprobe);
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                executor.createJob(builder, new ProgressListener() {
                    final double duration_ns = (duration * TimeUnit.SECONDS.toNanos(1)) / factor;
                    @Override
                    public void progress(Progress progress) {
                        double percentage = progress.out_time_ns / duration_ns;
                        long timeLeft = duration_ns - progress.out_time_ns > 0 ? (long) ((duration_ns - progress.out_time_ns) / progress.speed) : (long) duration_ns;
                        updateProgress(percentage, 1.0);
                        updateMessage(FFmpegUtils.toTimecode(timeLeft, TimeUnit.NANOSECONDS));
                        updateTitle(String.valueOf(progress.status).equals("continue")
                                ? I18n_Helper.getTranslation("progressBar.progress")
                                : I18n_Helper.getTranslation("progressBar.end"));
                    }
                }).run();

                return null;
            }
        };
        initiateProgressBar(task);
    }
}
