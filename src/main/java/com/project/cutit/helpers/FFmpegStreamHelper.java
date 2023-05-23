package com.project.cutit.helpers;

import com.project.cutit.Main;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;

import static com.project.cutit.Main.FFprobe;

public class FFmpegStreamHelper {
    public static int sampleRate;
    public static long bitRate;
    public static int frameRate;
    public static int audioChannels;

    public static double duration;
    public static String videoCodec;
    public static String audioCodec;

    public static FFmpegProbeResult getMediaData() throws IOException {
        return FFprobe.probe(CommonHelper.normalizePath(Main.getMedia().getSource()));
    }

    public static void setStreamData() throws IOException {
        FFmpegProbeResult data = getMediaData();
        FFmpegStream videoStream = null;
        FFmpegStream audioStream = null;

        for (FFmpegStream temp : data.getStreams()) {
            if(temp.sample_rate > 0 && audioStream == null){
                audioStream = temp;
            }
            if(temp.avg_frame_rate.intValue() > 0 && videoStream == null){
                videoStream = temp;
            }
        }

        if(audioStream != null){
            sampleRate = audioStream.sample_rate > 0 ? audioStream.sample_rate : 48_000;
            audioCodec = audioStream.codec_name;
            audioChannels = audioStream.channels <= 0 ? 1 : audioStream.channels;
        }
        bitRate = videoStream.bit_rate > 0 ? videoStream.bit_rate : 32768;
        frameRate = videoStream.avg_frame_rate.intValue();
        duration = videoStream.duration;
        videoCodec = videoStream.codec_name;
    }
}
