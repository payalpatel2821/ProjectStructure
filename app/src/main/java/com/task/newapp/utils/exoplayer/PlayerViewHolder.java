package com.task.newapp.utils.exoplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.zoomhelper.ZoomHelper;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.task.newapp.App;
import com.task.newapp.R;
import com.task.newapp.models.post.ResponseGetAllPost;
import com.task.newapp.ui.activities.post.ShowPostActivity;

public class PlayerViewHolder extends RecyclerView.ViewHolder {

    public FrameLayout mediaContainer;
    public ImageView volumeControl;
    public ProgressBar progressBar;
    public RequestManager requestManager;
    public TextView content_type, loader_txt;
    public ZoomHelper zoomHelper;
    LottieAnimationView img_default;
    ImageView mediaCoverImage;
    private final View parent;
    private final boolean isCaching = true;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.ivVolumeControl);
        content_type = itemView.findViewById(R.id.content_type);
        img_default = itemView.findViewById(R.id.img_default);
        loader_txt = itemView.findViewById(R.id.loader_txt);
//        loader_txt.setTypeface(App.setFont(App.getAppInstance(), "bebaskai.otf"));
//        img_default.setImageAssetsFolder("images/");
//        img_default.setAnimation("loader1.json");
//        img_default.loop(true);
//        img_default.playAnimation();
        zoomHelper = new ZoomHelper();
    }

    void onBind(final Context context, ResponseGetAllPost.All_Post_Data.PostContent mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);

//        this.requestManager
//                .load(mediaObject.getContent())
//                .into(mediaCoverImage);

//        ViewGroup.LayoutParams params = mediaCoverImage.getLayoutParams();
//        params.height = context.getResources().getDisplayMetrics().widthPixels * 350 / 600;

//        zoomHelper.setShadowColor(Color.RED);

        zoomHelper.setMaxScale(4f);
        zoomHelper.setMinScale(1f);
        zoomHelper.setShadowAlphaFactory(4f);

//        ViewGroup.LayoutParams var10000 = mediaCoverImage.getLayoutParams();
//        var10000.height = context.getResources().getDisplayMetrics().widthPixels * 350 / 600;
//        Glide.with(context).load(R.drawable.progress_load).into(img_default);
//        img_default.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        img_default.setVisibility(View.VISIBLE);
        loader_txt.setVisibility(View.VISIBLE);

        Glide.with(context).load(mediaObject.getContent())
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        img_default.setVisibility(View.VISIBLE);
                        loader_txt.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        img_default.setVisibility(View.GONE);
                        loader_txt.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mediaCoverImage);

        content_type.setText(mediaObject.getType());

        if (mediaObject.getType().equals("video")) {
            volumeControl.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
        } else {
            volumeControl.setVisibility(View.GONE);
        }

        if (getAdapterPosition() == 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                     ((ShowPostActivity) context).playVideo(false);
                }
            });
        }
    }


}

