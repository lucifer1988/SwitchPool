package com.switchpool.utility;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorder {
    private static int SAMPLE_RATE_IN_HZ = 8000; // ²ÉÑùÂÊ

    private MediaRecorder mMediaRecorder;
    private String mPath;

    public AudioRecorder(String path) {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        this.mPath = path;
    }

//    private String sanitizePath(String path) {
//        return Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/WifiChat/voiceRecord/" + path + ".amr";
//    }

    public void start() throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted,It is  " + state
                    + ".");
        }
        File directory = new File(mPath).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created");
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // recorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
        mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        mMediaRecorder.setOutputFile(mPath);
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    public void stop() throws IOException {
        mMediaRecorder.stop();
        mMediaRecorder.release();
    }

    public double getAmplitude() {
        if (mMediaRecorder != null) {
            return (mMediaRecorder.getMaxAmplitude());
        } else
            return 0;
    }
}