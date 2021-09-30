package com.task.newapp.utils.exo_video;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.task.newapp.R;

import java.util.ArrayList;
import java.util.List;


public class Sel_Media_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Integer> uritype;
    RequestManager requestManager;
    OnItemClickListener itemClickListener;
    private final Context mContext;
    private List<Uri> imguri;
    private ArrayList<String> captionarr;
    private ArrayList<String> timearr;

    public Sel_Media_RecyclerAdapter(Context context, List<Uri> imagesuri, ArrayList<Integer> uritype, ArrayList<String> captionarr, ArrayList<String> timearr, RequestManager requestManager) {
        mContext = context;
        imguri = imagesuri;
        this.captionarr = captionarr;
        this.timearr = timearr;
        this.uritype = uritype;
        this.requestManager = requestManager;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Sel_Media_PlayerHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.slidingimages_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((Sel_Media_PlayerHolder) viewHolder).setUpEmojiPopup();
        ((Sel_Media_PlayerHolder) viewHolder).onBind(mContext, imguri.get(i).getPath(), captionarr.get(i), uritype.get(i), i, requestManager, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return imguri.size();
    }

    public int getData() {
        return imguri.size();
    }


    public void setMediaObjects(ArrayList<Uri> imageurilist, ArrayList<Integer> uritype, ArrayList<String> caption_arr, ArrayList<String> time_arr) {
        this.imguri = imageurilist;
        this.uritype = uritype;
        this.captionarr = caption_arr;
        this.timearr = time_arr;

        notifyDataSetChanged();
    }




}
