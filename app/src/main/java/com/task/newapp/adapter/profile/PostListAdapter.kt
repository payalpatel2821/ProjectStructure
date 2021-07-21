package com.task.newapp.adapter.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.models.ResponsePostList
import java.util.*

class PostListAdapter(
    private val applicationContext: Context,
    private val dataSet: ArrayList<ResponsePostList.Data>
) :
    RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    var onItemClick: ((ArrayList<ResponsePostList.Data>, Int) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCommentCount: TextView = view.findViewById(R.id.tv_comment_count)
        val tvLikeCount: TextView = view.findViewById(R.id.tv_like_count)
        val ivPostContent: ImageView = view.findViewById(R.id.iv_post_content)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_post_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvLikeCount.text = dataSet[position].getlikesCount.toString()
        viewHolder.tvCommentCount.text = dataSet[position].commentsCount.toString()
        Glide.with(applicationContext).asBitmap().load(dataSet[position].contents[0].content).into(viewHolder.ivPostContent)

        viewHolder.itemView.setOnClickListener {
            if (onItemClick != null) {
                onItemClick!!.invoke(dataSet, position)
            }
        }
    }

    fun setData(data: ArrayList<ResponsePostList.Data>) {
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

}
