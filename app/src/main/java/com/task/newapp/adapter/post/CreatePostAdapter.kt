package com.task.newapp.adapter.post

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.utils.dpToPx
import java.util.*
import kotlin.collections.ArrayList


class CreatePostAdapter(
    private val applicationContext: Context,
    private val arrayList: ArrayList<Post_Uri_Model>
) :
    RecyclerView.Adapter<CreatePostAdapter.ViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null
    var showItems = 5

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPlay: ImageView = view.findViewById(R.id.imgPlay)
        val imgPost: ImageView = view.findViewById(R.id.imgPost)
        val imgBlur: ImageView = view.findViewById(R.id.imgBlur)
        val txtMore: TextView = view.findViewById(R.id.txtMore)
        val rlCreatePost: RelativeLayout = view.findViewById(R.id.rlCreatePost)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_create_post, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(applicationContext).asBitmap().load(arrayList[position].file_path).into(viewHolder.imgPost)

        if (arrayList[position].type == "image") viewHolder.imgPlay.visibility = View.GONE
        else viewHolder.imgPlay.visibility = View.VISIBLE

        viewHolder.rlCreatePost.layoutParams.height = dpToPx(applicationContext, 170f)

        if (arrayList.size > showItems) {
            val moreItems = arrayList.size - showItems
            viewHolder.txtMore.text = "$moreItems+"
        }

        if (arrayList.size > showItems) {
            if (position == showItems - 1) {
                viewHolder.imgBlur.visibility = View.VISIBLE
                viewHolder.txtMore.visibility = View.VISIBLE
            } else {
                viewHolder.imgBlur.visibility = View.INVISIBLE
                viewHolder.txtMore.visibility = View.INVISIBLE
            }
        } else {
            viewHolder.imgBlur.visibility = View.INVISIBLE
            viewHolder.txtMore.visibility = View.INVISIBLE
        }

        viewHolder.itemView.setOnClickListener {
            if (onItemClick != null) {
                onItemClick!!.invoke(position)
            }
        }
    }

    fun setData(data: ArrayList<Post_Uri_Model>) {
        arrayList.clear()
        arrayList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = if (arrayList.size > showItems) showItems else arrayList.size

}
