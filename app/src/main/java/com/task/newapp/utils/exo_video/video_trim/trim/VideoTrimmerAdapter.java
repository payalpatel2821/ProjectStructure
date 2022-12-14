package com.task.newapp.utils.exo_video.video_trim.trim;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.task.newapp.R;

import java.util.ArrayList;
import java.util.List;


public class VideoTrimmerAdapter extends RecyclerView.Adapter {
    private final List<Bitmap> mBitmaps = new ArrayList<>();
    private final LayoutInflater mInflater;
    private final Context context;

    public VideoTrimmerAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrimmerViewHolder(mInflater.inflate(R.layout.video_thumb_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TrimmerViewHolder) holder).thumbImageView.setImageBitmap(mBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

    public void addBitmaps(Bitmap bitmap) {
        Log.e("bitmaps", mBitmaps.size() + ":");
        mBitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    public int getBitmap() {
        return mBitmaps.size();
    }

    public void resetBitmaps() {
        mBitmaps.clear();
        notifyDataSetChanged();
    }

    private final class TrimmerViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbImageView;

        TrimmerViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumb);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) thumbImageView.getLayoutParams();
            layoutParams.width = VideoTrimmerUtil.VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE;
            thumbImageView.setLayoutParams(layoutParams);
        }
    }
}
