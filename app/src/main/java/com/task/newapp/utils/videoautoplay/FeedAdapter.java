package com.task.newapp.utils.videoautoplay;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hoanganhtuan95ptit.autoplayvideorecyclerview.VideoHolder;
import com.squareup.picasso.Picasso;
import com.task.newapp.R;
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HoangAnhTuan on 1/21/2018.
 */

public class FeedAdapter extends BaseAdapter<All_Post_Data.PostContent> {

    private static final int PHOTO_M1 = 0;
    //    private static final int PHOTO_M2 = 1;
    private static final int VIDEO_M1 = 2;
//    private static final int VIDEO_M2 = 3;

    public static int screenWight = 0;

    public FeedAdapter(Activity activity) {
        super(activity);
        screenWight = getScreenWight();
    }

    @Override
    public int getItemViewType(int position) {
        All_Post_Data.PostContent feed = list.get(position);

//        if (feed.getInfo() instanceof Photo) {
//            if (feed.getModel() == Feed.Model.M1) return PHOTO_M1;
//            return PHOTO_M2;
//        } else {
//            if (feed.getModel() == Feed.Model.M1) return VIDEO_M1;
//            return VIDEO_M2;
//        }

        if (feed.getType().equals("image")) {
            return PHOTO_M1;
        } else {
            return VIDEO_M1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PHOTO_M1:
                view = activity.getLayoutInflater().inflate(R.layout.item_post_photo_pager, parent, false);
                return new Photo11Holder(view);
//            case PHOTO_M2:
//                view = activity.getLayoutInflater().inflate(R.layout.item_post_photo_pager, parent, false);
//                return new Photo169Holder(view);
            case VIDEO_M1:
                view = activity.getLayoutInflater().inflate(R.layout.item_post_video_pager, parent, false);
                return new Video11Holder(view);
//            default:
//                view = activity.getLayoutInflater().inflate(R.layout.item_post_photo_pager, parent, false);
//                return new Photo11Holder(view);
        }
        view = activity.getLayoutInflater().inflate(R.layout.item_post_photo_pager, parent, false);
        return new Photo11Holder(view);
    }

    public static class Video11Holder extends DemoVideoHolder {

        public Video11Holder(View itemView) {
            super(itemView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vvInfo.getLayoutParams();
            layoutParams.width = FeedAdapter.screenWight;
            layoutParams.height = FeedAdapter.screenWight;
//
//            layoutParams.width = screenWight;
//            layoutParams.height = screenWight * 9 / 16;
//
            vvInfo.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e("FeedAdapter", "onBindViewHolder: " + position);

        All_Post_Data.PostContent feed = list.get(position);

        if (holder instanceof Video11Holder) {
            onBindVideo11Holder((Video11Holder) holder, feed);
        }
//        else if (holder instanceof Video169Holder) {
//            onBindVideo169Holder((Video169Holder) holder, feed);
//        }
        else if (holder instanceof Photo11Holder) {
            onBindPhoto11Holder((Photo11Holder) holder, feed);
        }
//        else if (holder instanceof Photo169Holder) {
//            onBindPhoto169Holder((Photo169Holder) holder, feed);
//        }
    }

    private void onBindPhoto11Holder(Photo11Holder holder, All_Post_Data.PostContent feed) {
        Picasso.with(activity)
                .load(feed.getThumb())
                .resize(screenWight, screenWight)
                .centerCrop()
                .into(holder.ivInfo);
    }

//    private void onBindPhoto169Holder(Photo169Holder holder, Feed feed) {
//        Picasso.with(activity)
//                .load(feed.getInfo().getUrlPhoto())
//                .resize(screenWight, screenWight * 9 / 16)
//                .centerCrop()
//                .into(holder.ivInfo);
//    }

    private void onBindVideo11Holder(final DemoVideoHolder holder, All_Post_Data.PostContent feed) {
//        holder.vvInfo.setVideo((Video) feed.getInfo());
        holder.vvInfo.setVideo(feed.getContent());

        Picasso.with(activity)
//                .load(feed.getInfo().getUrlPhoto())
                .load(feed.getThumb())
                .resize(screenWight, screenWight)
                .centerCrop()
                .into(holder.ivInfo);
    }

//    private void onBindVideo169Holder(final DemoVideoHolder holder, All_Post_Data.PostContent feed) {
//        holder.vvInfo.setVideo((Video) feed.getInfo());
//        Picasso.with(activity)
//                .load(feed.getInfo().getUrlPhoto())
//                .resize(screenWight, screenWight * 9 / 16)
//                .centerCrop()
//                .into(holder.ivInfo);
//    }

    private int getScreenWight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static class PhotoHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.ivInfo)
        ImageView ivInfo;

        public PhotoHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);

            ivInfo = itemView.findViewById(R.id.ivInfo);
        }

    }

    public static class Photo11Holder extends PhotoHolder {

        public Photo11Holder(View itemView) {
            super(itemView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivInfo.getLayoutParams();
            layoutParams.width = screenWight;
            layoutParams.height = screenWight;
            ivInfo.setLayoutParams(layoutParams);
        }
    }

//    public static class Photo169Holder extends PhotoHolder {
//
//
//        public Photo169Holder(View itemView) {
//            super(itemView);
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivInfo.getLayoutParams();
//            layoutParams.width = screenWight;
//            layoutParams.height = screenWight * 9 / 16;
//            ivInfo.setLayoutParams(layoutParams);
//        }

    public static class DemoVideoHolder extends VideoHolder {

        //        @BindView(R.id.vvInfo)
        VideoView vvInfo;
        //        @BindView(R.id.ivInfo)
        ImageView ivInfo;
        //        @BindView(R.id.ivCameraAnimation)
        CameraAnimation ivCameraAnimation;

        public DemoVideoHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);

            vvInfo = itemView.findViewById(R.id.vvInfo);
            ivInfo = itemView.findViewById(R.id.ivInfo);
            ivCameraAnimation = itemView.findViewById(R.id.ivCameraAnimation);
        }

        @Override
        public View getVideoLayout() {
            return vvInfo;
        }

        @Override
        public void playVideo() {
            ivInfo.setVisibility(View.VISIBLE);
            ivCameraAnimation.start();
            vvInfo.play(new VideoView.OnPreparedListener() {
                @Override
                public void onPrepared() {
                    ivInfo.setVisibility(View.GONE);
                    ivCameraAnimation.stop();
                }
            });
        }

        @Override
        public void stopVideo() {
            Log.e("FeedAdapter", "stopVideo: ");
            ivInfo.setVisibility(View.VISIBLE);
            ivCameraAnimation.stop();
            vvInfo.stop();
        }

//    public static class Video169Holder extends DemoVideoHolder {
//
//        public Video169Holder(View itemView) {
//            super(itemView);
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vvInfo.getLayoutParams();
//            layoutParams.width = screenWight;
//            layoutParams.height = screenWight * 9 / 16;
//            vvInfo.setLayoutParams(layoutParams);
//        }
//    }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.e("FeedAdapter", "onViewDetachedFromWindow: ");

        if (holder instanceof DemoVideoHolder) {
            DemoVideoHolder demoVideoHolder = (DemoVideoHolder) holder;
            demoVideoHolder.vvInfo.stop();
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Log.e("FeedAdapter", "onViewAttachedToWindow: ");

        if (holder instanceof DemoVideoHolder) {
            DemoVideoHolder demoVideoHolder = (DemoVideoHolder) holder;
            demoVideoHolder.vvInfo.play(new VideoView.OnPreparedListener() {
                @Override
                public void onPrepared() {
//                    ivInfo.setVisibility(View.GONE);
//                    ivCameraAnimation.stop();
                }
            });
        }
    }
}
