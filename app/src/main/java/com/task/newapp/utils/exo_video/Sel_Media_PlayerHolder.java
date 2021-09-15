package com.task.newapp.utils.exo_video;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.task.newapp.R;
import com.task.newapp.ui.activities.chat.ViewPagerActivity;
import com.task.newapp.utils.exo_video.video_trim.widget.VideoTrimmerView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;


public class Sel_Media_PlayerHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;
    public RequestManager requestManager;
    VideoTrimmerView trimmer_view;
    ImageView imageView;
    ImageView play_video, btn_emoji;
    TextView content_type;
    ImageView add_image;
    RelativeLayout rlMain;
    EmojiEditText caption_edt;
    private final View parent;
    //    EmojIconActions emojIcon;
    EmojiPopup emojiPopup;

    public Sel_Media_PlayerHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        progressBar = itemView.findViewById(R.id.progressBar);
        rlMain = itemView.findViewById(R.id.rlMain);
        imageView = itemView.findViewById(R.id.image);
        trimmer_view = itemView.findViewById(R.id.trimmer_view);
        content_type = itemView.findViewById(R.id.content_type);
        play_video = itemView.findViewById(R.id.play_video);
        add_image = itemView.findViewById(R.id.add_image);
        caption_edt = itemView.findViewById(R.id.caption_edt);
        btn_emoji = itemView.findViewById(R.id.btn_emoji);
    }


    void onBind(Context context, String imagesuri, String captionarr, int uritype, int postion, RequestManager requestManager, OnItemClickListener itemClickListener) {
        this.requestManager = requestManager;
        parent.setTag(this);
        this.requestManager
                .load(imagesuri)
                .into(imageView);

        content_type.setText(uritype + "");

        if (uritype != 1) {
//            progressBar.setVisibility(View.VISIBLE);
            trimmer_view.setVisibility(View.VISIBLE);
//            play_video.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
//            progressBar.setVisibility(View.GONE);
            trimmer_view.setVisibility(View.GONE);
//            play_video.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }


        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
//                emojIcon.ShowEmojIcon();
            }
        });
        if (getAdapterPosition() == 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((ViewPagerActivity) context).mRecyclerView.playVideo(false);
                }
            });
        }


        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, postion);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        trimmer_view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        caption_edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (emojiPopup.isShowing())
                    emojiPopup.toggle();
                return false;
            }
        });


    }

    private void hideSoftKeyboard(EmojiEditText ettext) {

        InputMethodManager inputMethodManager = (InputMethodManager) ettext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(ettext.getWindowToken(), 0);
    }

    public void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rlMain)
                .setOnEmojiBackspaceClickListener(ignore -> Log.d("TAG", "Clicked on Backspace"))
                .setOnEmojiClickListener((ignore, ignore2) -> Log.d("TAG", "Clicked on emoji"))
                .setOnEmojiPopupShownListener(() -> btn_emoji.setImageResource(R.drawable.ic_keyboard))
                .setOnSoftKeyboardOpenListener(ignore -> Log.d("TAG", "Opened soft keyboard"))
                .setOnEmojiPopupDismissListener(() -> btn_emoji.setImageResource(R.drawable.edit_smiley))
                .setOnSoftKeyboardCloseListener(() -> Log.d("TAG", "Closed soft keyboard"))
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(caption_edt);
    }
}

