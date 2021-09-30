package com.task.newapp.utils.exo_video.video_trim.widget;

import static com.task.newapp.ui.activities.chat.ViewPagerActivity.trim_time;
import static com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil.MAX_COUNT_RANGE;
import static com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil.MAX_SHOOT_DURATION;
import static com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil.RECYCLER_VIEW_PADDING;
import static com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil.THUMB_WIDTH;
import static com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil.VIDEO_FRAMES_WIDTH;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.task.newapp.R;
import com.task.newapp.utils.exo_video.video_trim.interfaces.IVideoTrimmerView;
import com.task.newapp.utils.exo_video.video_trim.interfaces.VideoTrimListener;
import com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerAdapter;
import com.task.newapp.utils.exo_video.video_trim.trim.VideoTrimmerUtil;
import com.task.newapp.utils.exo_video.video_trim.utils.StorageUtil;

import iknow.android.utils.callback.SingleCallback;
import iknow.android.utils.thread.BackgroundExecutor;
import iknow.android.utils.thread.UiThreadExecutor;


public class VideoTrimmerView extends FrameLayout implements IVideoTrimmerView {

    private static final String TAG = VideoTrimmerView.class.getSimpleName();
    int storyornot;
    private final int mMaxWidth = VIDEO_FRAMES_WIDTH;
    private Context mContext;
    private RelativeLayout mLinearVideo;
    private ZVideoView mVideoView;
    private ImageView mPlayView;
    private RecyclerView mVideoThumbRecyclerView;
    private RangeSeekBarView mRangeSeekBarView;
    private LinearLayout mSeekBarLayout;
    private ImageView mRedProgressIcon;
    private float mAverageMsPx;//每毫秒所占的px
    private float averagePxMs;//每px所占用的ms毫秒
    private Uri mSourceUri;
    private VideoTrimListener mOnTrimVideoListener;
    private int mDuration = 0;
    private VideoTrimmerAdapter mVideoThumbAdapter;
    private boolean isFromRestore = false;
    //new
    private long mLeftProgressPos, mRightProgressPos;
    private long mRedProgressBarPos = 0;
    private long scrollPos = 0;
    private int mScaledTouchSlop;
    private int lastScrollX;
    private boolean isSeeking;
    private final RangeSeekBarView.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBarView.OnRangeSeekBarChangeListener() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin,
                                                RangeSeekBarView.Thumb pressedThumb) {
            Log.d(TAG, "-----minValue----->>>>>>" + minValue);
            Log.d(TAG, "-----maxValue----->>>>>>" + maxValue);
            mLeftProgressPos = minValue + scrollPos;
            mRedProgressBarPos = mLeftProgressPos;
            mRightProgressPos = maxValue + scrollPos;
            Log.d(TAG, "-----mLeftProgressPos----->>>>>>" + mLeftProgressPos);
            Log.d(TAG, "-----mRightProgressPos----->>>>>>" + mRightProgressPos);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isSeeking = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    isSeeking = true;
                    seekTo((int) (pressedThumb == RangeSeekBarView.Thumb.MIN ? mLeftProgressPos : mRightProgressPos));
                    break;
                case MotionEvent.ACTION_UP:
                    isSeeking = false;
                    seekTo((int) mLeftProgressPos);
                    onSaveClicked();
                    break;
                default:
                    break;
            }

            mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        }
    };
    private boolean isOverScaledTouchSlop;
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.d(TAG, "newState = " + newState);
            if (newState == 0) {
                onSaveClicked();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isSeeking = false;
            int scrollX = calcScrollXDistance();
            //达不到滑动的距离
            if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                isOverScaledTouchSlop = false;
                return;
            }
            isOverScaledTouchSlop = true;
            //初始状态,why ? 因为默认的时候有35dp的空白！
            if (scrollX == -RECYCLER_VIEW_PADDING) {
                scrollPos = 0;
            } else {
                isSeeking = true;
                scrollPos = (long) (mAverageMsPx * (RECYCLER_VIEW_PADDING + scrollX) / THUMB_WIDTH);
                mLeftProgressPos = mRangeSeekBarView.getSelectedMinValue() + scrollPos;
                mRightProgressPos = mRangeSeekBarView.getSelectedMaxValue() + scrollPos;
                Log.d(TAG, "onScrolled >>>> mLeftProgressPos = " + mLeftProgressPos);
                mRedProgressBarPos = mLeftProgressPos;
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    setPlayPauseViewIcon(false);
                }
                mRedProgressIcon.setVisibility(GONE);
                seekTo(mLeftProgressPos);
                mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
                mRangeSeekBarView.invalidate();
            }
            lastScrollX = scrollX;
        }
    };
    private int mThumbsTotalCount;
    private ValueAnimator mRedProgressAnimator;
    private final Handler mAnimationHandler = new Handler();
    private final Runnable mAnimationRunnable = new Runnable() {

        @Override
        public void run() {
            updateVideoProgress();
        }
    };

//    public void initVideoByURI(final Uri videoURI) {
//        mSourceUri = videoURI;
//        mVideoView.setVideoURI(videoURI);
//        mVideoView.requestFocus();
//    }

    public VideoTrimmerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTrimmerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.video_trimmer_view, this, true);

        mLinearVideo = findViewById(R.id.layout_surface_view);
        mVideoView = findViewById(R.id.video_loader);
        mPlayView = findViewById(R.id.icon_video_play);
        mSeekBarLayout = findViewById(R.id.seekBarLayout);
        mRedProgressIcon = findViewById(R.id.positionIcon);
        mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView);
        mVideoThumbRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mVideoThumbAdapter = new VideoTrimmerAdapter(mContext);
        mVideoThumbRecyclerView.setAdapter(mVideoThumbAdapter);
        mVideoThumbRecyclerView.addOnScrollListener(mOnScrollListener);
        mVideoThumbRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                int action = e.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_MOVE:
                rv.getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        setUpListeners();
    }

    private void initRangeSeekBarView() {
        if (mRangeSeekBarView != null) return;
        mLeftProgressPos = 0;
        if (mDuration <= MAX_SHOOT_DURATION) {
            mThumbsTotalCount = MAX_COUNT_RANGE;
            mRightProgressPos = mDuration;
        } else {
            mThumbsTotalCount = (int) (mDuration * 1.0f / (MAX_SHOOT_DURATION * 1.0f) * MAX_COUNT_RANGE);
            mRightProgressPos = MAX_SHOOT_DURATION;
        }
        mVideoThumbRecyclerView.addItemDecoration(new SpacesItemDecoration2(RECYCLER_VIEW_PADDING, mThumbsTotalCount));
        mRangeSeekBarView = new RangeSeekBarView(mContext, mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.setSelectedMinValue(mLeftProgressPos);
        mRangeSeekBarView.setSelectedMaxValue(mRightProgressPos);
        mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.setMinShootTime(VideoTrimmerUtil.MIN_SHOOT_DURATION);
        mRangeSeekBarView.setNotifyWhileDragging(true);
        mRangeSeekBarView.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
        mSeekBarLayout.addView(mRangeSeekBarView);
        if (mThumbsTotalCount - MAX_COUNT_RANGE > 0) {
            mAverageMsPx = (mDuration - MAX_SHOOT_DURATION) / (float) (mThumbsTotalCount - MAX_COUNT_RANGE);
        } else {
            mAverageMsPx = 0f;
        }
        averagePxMs = (mMaxWidth * 1.0f / (mRightProgressPos - mLeftProgressPos));
    }

    public void initVideoByURI(final Uri videoURI, long timeInMillisec, String duration, int isstory) {
//        mSourceUri = videoURI;
//        mVideoView.setVideoURI(videoURI);
//        mVideoView.requestFocus();

        MAX_SHOOT_DURATION = timeInMillisec;

        storyornot = isstory;
        if (isstory == 1) {
            mSourceUri = videoURI;
            VideoTrimmerUtil.trim(mContext,
                    mSourceUri.getPath(),
                    "HOW",
                    mLeftProgressPos,
                    mRightProgressPos,
                    mOnTrimVideoListener, storyornot);
            mVideoView.setVideoURI(videoURI);
            mVideoView.requestFocus();
            trim_time.setText(duration);
        } else {
            try {
                mSourceUri = videoURI;
                mVideoView.setVideoURI(videoURI);
                mVideoView.requestFocus();
                trim_time.setText(duration);
            } catch (Exception e) {
//                FirebaseCrashlytics.getInstance().setCustomKey("InitVideoByUri", e.getMessage());
//                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }


    }

    private void startShootVideoThumbs(final Context context, final Uri videoUri, int totalThumbsCount, long startPosition, long endPosition) {


        VideoTrimmerUtil.shootVideoThumbInBackground(context, videoUri, totalThumbsCount, startPosition, endPosition,
                new SingleCallback<Bitmap, Integer>() {
                    @Override
                    public void onSingleCallback(final Bitmap bitmap, final Integer interval) {
                        if (bitmap != null) {
                            UiThreadExecutor.runTask("", new Runnable() {
                                @Override
                                public void run() {
                                    if (mVideoThumbAdapter.getBitmap() < totalThumbsCount)
                                        mVideoThumbAdapter.addBitmaps(bitmap);
                                }
                            }, 0L);
                        }
                    }
                });
    }

    private void onCancelClicked() {
        mOnTrimVideoListener.onCancel();
    }

    private void videoPrepared(MediaPlayer mp) {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mLinearVideo.getWidth();
        int screenHeight = mLinearVideo.getHeight();

        lp.width = screenWidth;
        if (videoHeight > videoWidth) {
            lp.height = screenHeight;
        } else {
            float r = videoHeight / (float) videoWidth;
            lp.height = (int) (lp.width * r);
        }
        mVideoView.setLayoutParams(lp);
        mDuration = mVideoView.getDuration();
        if (getRestoreState()) {
            setRestoreState(false);
        }
        seekTo((int) mRedProgressBarPos);
        initRangeSeekBarView();

//        mVideoThumbAdapter.resetBitmaps();  //Add New
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("Handler", "Running Handler " + mThumbsTotalCount);
                startShootVideoThumbs(mContext, mSourceUri, mThumbsTotalCount, 0, mDuration);
//            }
//        }, 2000);

    }

    private void videoCompleted() {
        seekTo(mLeftProgressPos);
        setPlayPauseViewIcon(false);
    }

    public void onVideoReset() {
        mVideoView.pause();
        setPlayPauseViewIcon(false);
    }

    private void playVideoOrPause() {
        mRedProgressBarPos = mVideoView.getCurrentPosition();
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            pauseRedProgressAnimation();
        } else {
            mVideoView.start();
            playingRedProgressAnimation();
        }
        setPlayPauseViewIcon(mVideoView.isPlaying());
    }

    public void onVideoPause() {
        if (mVideoView.isPlaying()) {
            seekTo(mLeftProgressPos);
            mVideoView.pause();
            setPlayPauseViewIcon(false);
            mRedProgressIcon.setVisibility(GONE);
        }
    }

    public void setOnTrimVideoListener(VideoTrimListener onTrimVideoListener) {
        mOnTrimVideoListener = onTrimVideoListener;
    }

    private void setUpListeners() {
        findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked();
            }
        });

        findViewById(R.id.finishBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                videoPrepared(mp);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoCompleted();
            }
        });
        mPlayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideoOrPause();
            }
        });
    }

    private void onSaveClicked() {
        if (mRightProgressPos - mLeftProgressPos < VideoTrimmerUtil.MIN_SHOOT_DURATION) {
            Toast.makeText(mContext, "视频长不足3秒,无法上传", Toast.LENGTH_SHORT).show();
        } else {
            mVideoView.pause();
            VideoTrimmerUtil.trim(mContext,
                    mSourceUri.getPath(),
                    StorageUtil.getCacheDir(),
                    mLeftProgressPos,
                    mRightProgressPos,
                    mOnTrimVideoListener, storyornot);
        }
    }

    private void seekTo(long msec) {
        mVideoView.seekTo((int) msec);

        Log.d(TAG, "seekTo = " + msec);
    }

    private boolean getRestoreState() {
        return isFromRestore;
    }

    public void setRestoreState(boolean fromRestore) {
        isFromRestore = fromRestore;
    }

    private void setPlayPauseViewIcon(boolean isPlaying) {
        mPlayView.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
    }

    /**
     * 水平滑动了多少px
     */
    private int calcScrollXDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mVideoThumbRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (position < 0)
            return 0;

        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemWidth = firstVisibleChildView.getWidth();
        return (position) * itemWidth - firstVisibleChildView.getLeft();
    }

    private void playingRedProgressAnimation() {
        pauseRedProgressAnimation();
        playingAnimation();
        mAnimationHandler.post(mAnimationRunnable);
    }

    public void playingAnimation() {
        if (mRedProgressIcon.getVisibility() == View.GONE) {
            mRedProgressIcon.setVisibility(View.VISIBLE);
        }
        final LayoutParams params = (LayoutParams) mRedProgressIcon.getLayoutParams();
        int start = (int) (RECYCLER_VIEW_PADDING + (mRedProgressBarPos - scrollPos) * averagePxMs);
        int end = (int) (RECYCLER_VIEW_PADDING + (mRightProgressPos - scrollPos) * averagePxMs);
        mRedProgressAnimator = ValueAnimator.ofInt(start, end).setDuration((mRightProgressPos - scrollPos) - (mRedProgressBarPos - scrollPos));
        mRedProgressAnimator.setInterpolator(new LinearInterpolator());
        mRedProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.leftMargin = (int) animation.getAnimatedValue();
                mRedProgressIcon.setLayoutParams(params);
                Log.d(TAG, "----onAnimationUpdate--->>>>>>>" + mRedProgressBarPos);
            }
        });
        mRedProgressAnimator.start();
    }

    public void pauseRedProgressAnimation() {
        mRedProgressIcon.clearAnimation();
        if (mRedProgressAnimator != null && mRedProgressAnimator.isRunning()) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            mRedProgressAnimator.cancel();
            Log.d(TAG, "----onAnimationUpdate--->>>>>>>" + mRedProgressBarPos);
        }
    }

    public void updateVideoProgress() {
        long currentPosition = mVideoView.getCurrentPosition();
//        Log.d(TAG, "updateVideoProgress currentPosition = " + currentPosition);
        if (currentPosition >= (mRightProgressPos)) {
            mRedProgressBarPos = mLeftProgressPos;
            pauseRedProgressAnimation();
            onVideoPause();
        } else {
            mAnimationHandler.post(mAnimationRunnable);
        }
    }

    /**
     * Cancel trim thread execut action when finish
     */
    @Override
    public void onDestroy() {
        BackgroundExecutor.cancelAll("", true);
        UiThreadExecutor.cancelAll("");
    }
}
