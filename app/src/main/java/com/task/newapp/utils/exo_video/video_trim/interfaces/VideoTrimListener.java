package com.task.newapp.utils.exo_video.video_trim.interfaces;

public interface VideoTrimListener {
    void onStartTrim();

    void onFinishTrim(String url);

    void onCancel();

    void onDestroyTrim();

    void onPauseTrim();

}
