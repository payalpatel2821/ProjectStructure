package com.task.newapp.utils.videoautoplay;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.task.newapp.App;
import com.task.newapp.R;

import java.io.IOException;

/**
 * Created by tuanha00 on 1/22/2018.
 */

public class VideoView extends RelativeLayout implements TextureView.SurfaceTextureListener {

    //    private Video video;
    private String videoUrl;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private OnCompletionListener onCompletionListener;

    public VideoView(Context context) {
        super(context);
        init(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

//    public void setVideo(Video video) {
//        this.video = video;
//    }

    public void setVideo(String video) {
        this.videoUrl = video;
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_video, this);
        TextureView textureView = findViewById(R.id.surfaceView);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            Log.e("TextureView", "onSurfaceTextureAvailable: ");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(new Surface(surfaceTexture));
            HttpProxyCacheServer proxy = App.getProxy(getContext());
//            String proxyUrl = proxy.getProxyUrl(video.getUrlVideo());
            String proxyUrl = proxy.getProxyUrl(videoUrl);
            mediaPlayer.setDataSource(proxyUrl);

            Log.e("TextureView:proxyUrl:", proxyUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.e("TextureView", "onSurfaceTextureSizeChanged: ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.e("TextureView", "onSurfaceTextureDestroyed: ");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        Log.e("TextureView", "onSurfaceTextureUpdated: ");
    }

    public void play(final OnPreparedListener onPreparedListener) {
        if (isPlaying) return;
        isPlaying = true;
        try {
            this.mediaPlayer.prepareAsync();
            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.seekTo(video.getSeekTo());
                    mediaPlayer.seekTo(0);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    if (onPreparedListener != null) onPreparedListener.onPrepared();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!isPlaying) return;
        isPlaying = false;
        if (mediaPlayer != null) {
            //video.setSeekTo(mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() ? 0 : mediaPlayer.getCurrentPosition());
            mediaPlayer.pause();
            mediaPlayer.stop();
        }
    }

    public interface OnCompletionListener {
        void onCompletion();
    }


    public interface OnPreparedListener {
        void onPrepared();
    }

}

