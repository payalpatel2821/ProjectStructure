package com.task.newapp.utils.exo_video.video_trim.trim;


import static com.task.newapp.utils.UtilsKt.checkTrimFolder;
import static com.task.newapp.utils.exo_video.video_trim.utils.UIThreadUtil.runOnUiThread;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import com.task.newapp.utils.exo_video.video_trim.interfaces.VideoTrimListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Jni.FFmpegCmd;
import VideoHandle.OnEditorListener;
import iknow.android.utils.DeviceUtil;
import iknow.android.utils.UnitConverter;
import iknow.android.utils.callback.SingleCallback;
import iknow.android.utils.thread.BackgroundExecutor;


public class VideoTrimmerUtil {

    public static final long MIN_SHOOT_DURATION = 3000L;// 最小剪辑时间3s
    public static final int VIDEO_MAX_TIME = 10;// 10秒
    public static final int MAX_COUNT_RANGE = 10;  //seekBar的区域内一共有多少张图片
    public static final int RECYCLER_VIEW_PADDING = UnitConverter.dpToPx(35);
    private static final String TAG = VideoTrimmerUtil.class.getSimpleName();
    private static final int SCREEN_WIDTH_FULL = DeviceUtil.getDeviceWidth();
    public static final int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
    public static final int THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
    private static final int THUMB_HEIGHT = UnitConverter.dpToPx(50);
    public static long MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 1000L;//视频最多剪切多长时间10s

    public static void trim(Context context, String inputFile, String outputFile, long startMs, long endMs, final VideoTrimListener callback, int story) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName = "trimmedVideo_" + timeStamp + ".mp4";

//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "";

        outputFile = checkTrimFolder() + "/" + outputName;

        String start = convertSecondsToTime(startMs / 1000);
        String end = convertSecondsToTime(endMs / 1000);
        String duration;
        if (story == 1) {
            duration = "00:00:30";
        } else {
            duration = convertSecondsToTime((endMs - startMs) / 1000);
        }

//        Log.e("trim time and size",start+"----::----"+end+"-----::-----"+duration+":::"+outputFile+"::");
        String[] complexCommand = {"ffmpeg", "-ss", start, "-i", inputFile, "-to", duration, "-c", "copy", outputFile};

//        Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_change_email);
//        dialog.show();
        String finalOutputFile = outputFile;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegCmd.exec(complexCommand, 0, new OnEditorListener() {
                    @Override
                    public void onSuccess() {
//        alertDialog.dismiss();
                        if (callback != null && finalOutputFile != null && !finalOutputFile.isEmpty())
                            callback.onFinishTrim(finalOutputFile);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                        Toast.makeText(context, "Video trim Sucessful.", Toast.LENGTH_SHORT).show();
//                  dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                        Toast.makeText(context, "Failed to trim", Toast.LENGTH_SHORT).show();
//                  dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onProgress(float progress) {

                    }
                });
            }
        }).start();


    }

    public static void shootVideoThumbInBackground(final Context context, final Uri videoUri, final int totalThumbsCount, final long startPosition,
                                                   final long endPosition, final SingleCallback<Bitmap, Integer> callback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
            @Override
            public void execute() {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(context, videoUri);
                    // Retrieve media data use microsecond
                    long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    for (long i = 0; i < totalThumbsCount; ++i) {
                        long frameTime = startPosition + interval * i;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        if (bitmap == null) continue;
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                        callback.onSingleCallback(bitmap, (int) interval);
                    }
                    mediaMetadataRetriever.release();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        });
    }

    public static String getVideoFilePath(String url) {
        if (TextUtils.isEmpty(url) || url.length() < 5) return "";
        if (url.substring(0, 4).equalsIgnoreCase("http")) {

        } else {
            url = "file://" + url;
        }

        return url;
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + i;
        } else {
            retStr = "" + i;
        }
        return retStr;
    }
}
