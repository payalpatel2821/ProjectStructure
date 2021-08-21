package com.task.newapp.adapter.post

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnItemClickListener
import com.task.newapp.R
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.utils.dpToPx
import com.task.newapp.utils.isImageFile
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class CreatePostAdapter(
    private val applicationContext: Context,
//    private val arrayList: ArrayList<LocalMedia>
    mOnAddPicClickListener: onAddPicClickListener
) :
    RecyclerView.Adapter<CreatePostAdapter.ViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null
    var showItems = 5
    var mOnAddPicClickListener: onAddPicClickListener? = null
    private var arrayList: ArrayList<LocalMedia>? = ArrayList<LocalMedia>()
    private var selectMax = 9

    /**
     * Click to add picture jump
     */

    interface onAddPicClickListener {
        fun onAddPicClick()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPlay: ImageView = view.findViewById(R.id.imgPlay)
        val imgPost: ImageView = view.findViewById(R.id.imgPost)
        val imgBlur: ImageView = view.findViewById(R.id.imgBlur)
        val txtMore: TextView = view.findViewById(R.id.txtMore)
        val rlCreatePost: RelativeLayout = view.findViewById(R.id.rlCreatePost)
    }

    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    fun setList(list: ArrayList<LocalMedia>?) {
        arrayList?.clear()
        arrayList!!.addAll(list!!)
//        this.arrayList = list as ArrayList<LocalMedia>?
    }

    fun getData(): List<LocalMedia?>? {
        return if (arrayList == null) ArrayList() else arrayList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_create_post, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val media: LocalMedia = arrayList!![position]

        if (media == null || TextUtils.isEmpty(media.path)) {
            return
        }

        val chooseModel: Int = media.chooseModel
//        val path: String = if (media.isCut && !media.isCompressed) {
//            // Cropped
//            media.cutPath
//        } else if (media.isCompressed || media.isCut && media.isCompressed) {
//            // Compressed, or cropped and compressed at the same time, the final compressed image shall prevail
//            media.compressPath
//        } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(media.coverPath)) {
//            media.coverPath
//        } else {
//            // Original image
//            media.path
////            media.cutPath
//        }

        val path = media.path

        Log.i(GridImageAdapter.TAG, "Original image address::" + media.path)
        if (media.isCut) {
            Log.i(GridImageAdapter.TAG, "Crop address::" + media.cutPath)
        }

        if (media.isCompressed) {
            Log.i(GridImageAdapter.TAG, "Compressed address::" + media.compressPath)
            Log.i(GridImageAdapter.TAG, "File size after compression::" + File(media.compressPath).length() / 1024 + "k")
        }
        if (!TextUtils.isEmpty(media.androidQToPath)) {
            Log.i(GridImageAdapter.TAG, "Android Q unique address::" + media.androidQToPath)
        }
        if (media.isOriginal) {
            Log.i(GridImageAdapter.TAG, "Whether to open the original image function::" + true)
            Log.i(GridImageAdapter.TAG, "Address after enabling the original image function::" + media.originalPath)
        }

        Log.e("onBindViewHolder:Path ", path)
//        Log.e("onBindViewHolder:Glide ", (if (PictureMimeType.isContent(path) && !media.isCut && !media.isCompressed) Uri.parse(path) else path).toString())
        Log.e("onBindViewHolder:Glide ", (if (isImageFile(path)) "image" else "video").toString())

        Glide.with(applicationContext).asBitmap().load(
            // if (PictureMimeType.isContent(path) && !media.isCut && !media.isCompressed) Uri.parse(path) else path
            path
        ).into(viewHolder.imgPost)

//        if (PictureMimeType.isHasImage(media.mimeType)) viewHolder.imgPlay.visibility = View.GONE
//        else viewHolder.imgPlay.visibility = View.VISIBLE

        if (isImageFile(path)) viewHolder.imgPlay.visibility = View.GONE
        else viewHolder.imgPlay.visibility = View.VISIBLE

        viewHolder.rlCreatePost.layoutParams.height = dpToPx(applicationContext, 170f)

        if (arrayList!!.size > showItems) {
            val moreItems = arrayList!!.size - showItems
            viewHolder.txtMore.text = "$moreItems+"
        }

        if (arrayList!!.size > showItems) {
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

        //itemView 的点击事件
//        if (mItemClickListener != null) {
//            viewHolder.itemView.setOnClickListener { v: View? ->
//                val adapterPosition = viewHolder.adapterPosition
//                mItemClickListener!!.onItemClick(v, adapterPosition)
//            }
////        }
//        if (mItemLongClickListener != null) {
//            viewHolder.itemView.setOnLongClickListener { v: View? ->
//                val adapterPosition = viewHolder.adapterPosition
//                mItemLongClickListener!!.onItemLongClick(viewHolder, adapterPosition, v)
//                true
//            }
//        }
    }

    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    fun setData(data: ArrayList<LocalMedia>) {
        arrayList!!.clear()
        arrayList!!.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = if (arrayList!!.size > showItems) showItems else arrayList!!.size

    init {
        this.mOnAddPicClickListener = mOnAddPicClickListener
    }
}
