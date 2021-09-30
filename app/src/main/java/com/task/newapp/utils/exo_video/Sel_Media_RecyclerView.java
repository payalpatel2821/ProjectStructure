package com.task.newapp.utils.exo_video;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.task.newapp.R;
import com.task.newapp.ui.activities.chat.ViewPagerActivity;
import com.task.newapp.utils.DateTimeUtils;
import com.task.newapp.utils.UtilsKt;
import com.task.newapp.utils.exo_video.video_trim.interfaces.VideoTrimListener;
import com.task.newapp.utils.exo_video.video_trim.widget.VideoTrimmerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class Sel_Media_RecyclerView extends RecyclerView {
    private static final String TAG = "ExoPlayerRecyclerView";
    public VideoTrimmerView trimmer_view;
    ImageView imageView;
    ImageView add_image;
    EditText caption_edt;
    int type;
    int targetPosition = 0;
    int count = 0;
    ArrayList<Uri> imageurilist;
    ArrayList<Integer> uritype;
    ArrayList<String> caption_arr;
    ArrayList<String> time_arr;
    private ImageView volumeControl;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    private VolumeState volumeState;
    private final OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleVolume();
        }
    };

    public Sel_Media_RecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Sel_Media_RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) Objects.requireNonNull(
                getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        setVolumeControl(VolumeState.ON);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo(!recyclerView.canScrollHorizontally(1));
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                UtilsKt.hideSoftKeyboard((Activity) context);
            }
        });


    }

 /*   private void hideSoftKeyboard(EditText ettext) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(ettext.getWindowToken(), 0);
        }
    }*/

    public int getCurrentItem() {
        return targetPosition;
    }

    public void playVideo(boolean isEndOfList) {


        Log.d(TAG, "playVideo: target isEndOfList: " + isEndOfList);

        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(
                    getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition =
                        startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = imageurilist.size() - 1;
        }

        Log.d(TAG, "playVideo: target position: " + targetPosition);

        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition;

        int currentPosition =
                targetPosition - ((LinearLayoutManager) Objects.requireNonNull(
                        getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        Sel_Media_PlayerHolder holder = (Sel_Media_PlayerHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }

        progressBar = holder.progressBar;
        volumeControl = holder.play_video;
        viewHolderParent = holder.itemView;
        requestManager = holder.requestManager;
        type = Integer.parseInt(holder.content_type.getText().toString());
        trimmer_view = holder.trimmer_view;
        imageView = holder.imageView;
        add_image = holder.add_image;
        caption_edt = holder.caption_edt;

        ((ViewPagerActivity) getContext()).img_rv.scrollToPosition(targetPosition);
        ((ViewPagerActivity) getContext()).image_rv_adapter.selectedPosition = targetPosition;
        ((ViewPagerActivity) getContext()).image_rv_adapter.notifyDataSetChanged();
        if (uritype.get(targetPosition) == 1) {
            ((ViewPagerActivity) getContext()).iv_edit.setVisibility(View.VISIBLE);
            ((ViewPagerActivity) getContext()).iv_crop.setVisibility(View.GONE);
            ((ViewPagerActivity) getContext()).trim_time.setVisibility(View.GONE);
        } else {
            ((ViewPagerActivity) getContext()).iv_edit.setVisibility(View.GONE);
            ((ViewPagerActivity) getContext()).iv_crop.setVisibility(View.GONE);
            ((ViewPagerActivity) getContext()).trim_time.setVisibility(View.VISIBLE);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//          retriever.setDataSource(getRealName(targetPosition));
            retriever.setDataSource(imageurilist.get(targetPosition).getPath()/*, new HashMap<String, String>()*/);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();
            String duration = new DateTimeUtils().convertMillieToHMmSs(timeInMillisec);
            ((ViewPagerActivity) getContext()).trim_time.setText(duration);
            if (!duration.equals(""))
                ((ViewPagerActivity) getContext()).time_arr.set(targetPosition, duration);
        }


        caption_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.println(Log.ASSERT, "sss----", s.toString());
                if (!s.toString().equals("")) {
                    ((ViewPagerActivity) getContext()).caption_arr.set(targetPosition, s.toString());
                } else {
                    ((ViewPagerActivity) getContext()).caption_arr.set(targetPosition, null);
                }
            }
        });

        if (caption_arr.get(targetPosition) == null) {
            caption_edt.setText("");
        } else {
            caption_edt.setText(caption_arr.get(targetPosition));
        }

        if (type != 1) {
            trimmer_view.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);


            viewHolderParent.setOnClickListener(videoViewClickListener);
            String mediaUrl = imageurilist.get(targetPosition).getPath();

            if (mediaUrl != null) {

                if (trimmer_view != null) {
                    trimmer_view.setOnTrimVideoListener(new VideoTrimListener() {
                        @Override
                        public void onCancel() {
                            trimmer_view.onDestroy();
                        }

                        @Override
                        public void onDestroyTrim() {
                            trimmer_view.onDestroy();
                        }

                        @Override
                        public void onPauseTrim() {
                            trimmer_view.onVideoPause();
                            trimmer_view.setRestoreState(true);
                        }

                        @Override
                        public void onStartTrim() {

                        }

                        @Override
                        public void onFinishTrim(String finalOutputFile) {

                            try {
                                ((ViewPagerActivity) getContext()).setTrimVideo(targetPosition, finalOutputFile);

                                Log.println(Log.ASSERT, "finalOutputFile---", finalOutputFile);

                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                if (Build.VERSION.SDK_INT >= 14) {
                                    retriever.setDataSource(finalOutputFile/*, new HashMap<String, String>()*/);
                                } else {
                                    retriever.setDataSource(finalOutputFile);
                                }

                                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                long timeInMillisec = Long.parseLong(time);
                                retriever.release();
                                String duration = new DateTimeUtils().convertMillieToHMmSs(timeInMillisec);
                                if (!duration.equals(""))
                                    ((ViewPagerActivity) getContext()).time_arr.set(targetPosition, duration);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                    if (Build.VERSION.SDK_INT >= 14) {
                        retriever.setDataSource(mediaUrl/*, new HashMap<String, String>()*/);
                    } else {
                        retriever.setDataSource(mediaUrl);
                    }

                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time);
                    retriever.release();
                    String duration = new DateTimeUtils().convertMillieToHMmSs(timeInMillisec);

//                    Log.println(Log.ASSERT, "duration---", duration);
//                    Log.println(Log.ASSERT, "timeInMillisec---", timeInMillisec + "");

//                    if (((ViewPagerActivity) getContext()).flagcclass.equals("info")) {
//
//                        Log.println(Log.ASSERT, "trimmer_view-----", "info");
//
//                        if (timeInMillisec < 30000) {
//                            trimmer_view.initVideoByURI(Uri.parse(mediaUrl), timeInMillisec, duration, 0);
//                        } else {
//                            String dur = convertMillieToHMmSs(30000);
//                            trimmer_view.initVideoByURI(Uri.parse(mediaUrl), 30000, dur, 1);
//                        }
//
//                    } else {
                    Log.println(Log.ASSERT, "trimmer_view-----", "chating");

                    trimmer_view.initVideoByURI(Uri.parse(mediaUrl), timeInMillisec, duration, 0);
//                    }

                }
            }
        } else {
            progressBar.setVisibility(View.GONE);
            trimmer_view.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        trimmer_view.onVideoReset();
        trimmer_view.setRestoreState(true);
        trimmer_view.pauseRedProgressAnimation();

    }

    public String getRealName(int i) {
        String path = imageurilist.get(i).getPath();
        if (count != 0) {
            count = 0;
        }
        if (path.contains("%03d")) {
            while (count != -1) {
                if (count < 10) {
                    File file = new File(path.replace("%03d", "00" + count));
                    if (file.exists()) {
                        count = -1;
                        path = file.getAbsolutePath();
                    } else {
                        count++;
                    }
                } else {
                    File file = new File(path.replace("%03d", "0" + count));
                    if (file.exists()) {
                        count = -1;
                        path = file.getAbsolutePath();
                    } else {
                        count++;
                    }
                }
            }
        } else {
            return path;
        }
        return path;
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(
                getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    private void addVideoView() {
        isVideoViewAdded = true;
    }

    //public void onRestartPlayer() {
    //  if (videoPlayer != null) {
    //   playVideo(true);
    //  }
    //}

    private void resetVideoView() {
        if (isVideoViewAdded) {
//            removeVideoView(videoSurfaceView);
            playPosition = -1;
//            videoSurfaceView.setVisibility(INVISIBLE);
//            mediaCoverImage.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {


        viewHolderParent = null;
    }

    public void onPausePlayer() {
//        if (videoPlayer != null) {
//            videoPlayer.setPlayWhenReady(false);
//        }
    }

    public void toggleVolume() {
        if (trimmer_view != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            trimmer_view.onVideoPause();
            trimmer_view.setRestoreState(true);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
//            trimmer_view.onVideoPlay();
            animateVolumeControl();
        }
    }

    public void animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl.bringToFront();
            if (volumeState == VolumeState.OFF) {
                requestManager.load(R.drawable.play)
                        .into(volumeControl);
            } else if (volumeState == VolumeState.ON) {
                requestManager.load(R.drawable.pause)
                        .into(volumeControl);
            }
            volumeControl.animate().cancel();

            volumeControl.setAlpha(1f);

            volumeControl.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }

    public void setMediaObjects(ArrayList<Uri> imageurilist, ArrayList<Integer> uritype, ArrayList<String> caption_arr, ArrayList<String> time_arr) {
        this.imageurilist = imageurilist;
        this.uritype = uritype;
        this.caption_arr = caption_arr;
        this.time_arr = time_arr;
    }

    private enum VolumeState {
        ON, OFF
    }

}

