package com.task.newapp.adapter.chat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.task.newapp.R;
import com.task.newapp.ui.activities.chat.ViewPagerActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Image_rv_adapter extends RecyclerView.Adapter<Image_rv_adapter.MyViewHolder> {

    public static int selectedPosition = -1;
    Context mycontext;
    List<Uri> myimgurls;
    ArrayList<Integer> uritype;

    public Image_rv_adapter(Context context, List<Uri> imagesuri, ArrayList<Integer> uritype) {
        mycontext = context;
        myimgurls = imagesuri;
        this.uritype = uritype;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_rv_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (selectedPosition == position) {
            holder.img_iv_border.setVisibility(View.VISIBLE);
        } else {
            holder.img_iv_border.setVisibility(View.GONE);
        }

//        Log.println(Log.ASSERT, "--type---",uritype.get(position)+"");

        if (position < uritype.size()) {
            if (uritype.get(position) == 1) {
                holder.img_play.setVisibility(View.GONE);
            } else {
                holder.img_play.setVisibility(View.VISIBLE);
            }
        }

        Glide.with(mycontext)
                .load(myimgurls.get(position))
                .into(holder.img_iv);
    }

    public void setData(List<Uri> imguri, ArrayList<Integer> uritype) {
        this.myimgurls = imguri;
        this.uritype = uritype;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myimgurls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_iv_border, img_play;

        CircleImageView img_iv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_iv = itemView.findViewById(R.id.img_iv);
            img_play = itemView.findViewById(R.id.img_play);
            img_iv_border = itemView.findViewById(R.id.img_iv_border);

            itemView.setOnClickListener(view -> {
                selectedPosition = getLayoutPosition();
                Log.e("item position click", selectedPosition + "::");
                ((ViewPagerActivity) mycontext).mRecyclerView.smoothScrollToPosition(selectedPosition);
                notifyDataSetChanged();
            });
        }
    }

}
