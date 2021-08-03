package com.task.newapp.utils.exoplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.task.newapp.R
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.ui.activities.post.ShowPostActivity
import java.util.ArrayList

class MediaRecyclerAdapter(
    var context: ShowPostActivity, mediaObjects: List<ResponseGetAllPost.All_Post_Data.PostContent>?,
    requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mediaObjects: List<ResponseGetAllPost.All_Post_Data.PostContent>? = ArrayList<ResponseGetAllPost.All_Post_Data.PostContent>()
    private val requestManager: RequestManager

    fun setdata(mediaObjects: List<ResponseGetAllPost.All_Post_Data.PostContent>?) {
        this.mediaObjects = mediaObjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_media_list_post, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as PlayerViewHolder).onBind(context, mediaObjects!![i], requestManager)
    }

    override fun getItemCount(): Int {
        return if (mediaObjects != null) {
            mediaObjects!!.size
        } else {
            0
        }
    }

    init {
        this.mediaObjects = mediaObjects
        this.requestManager = requestManager
    }
}