package com.task.newapp.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.task.newapp.R
import lv.chi.photopicker.loader.ImageLoader
import java.io.File

class GlideImageLoader : ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Glide.with(context).load(uri).into(view)
    }
}