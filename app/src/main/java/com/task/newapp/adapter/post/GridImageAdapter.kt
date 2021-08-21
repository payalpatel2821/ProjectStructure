package com.task.newapp.adapter.post

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnItemClickListener
import com.luck.picture.lib.tools.DateUtils
import com.task.newapp.R
import com.task.newapp.utils.instapicker.OnItemLongClickListener
import java.io.File
import java.lang.Exception
import java.util.ArrayList

/**
 * @author：luck
 * @date：2016-7-27 23:02
 * @describe：GridImageAdapter
 */
class GridImageAdapter(context: Context?, mOnAddPicClickListener: onAddPicClickListener) : RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var list: ArrayList<LocalMedia>? = ArrayList<LocalMedia>()
    private var selectMax = 9

    /**
     * 点击添加图片跳转
     */
    private val mOnAddPicClickListener: onAddPicClickListener

    interface onAddPicClickListener {
        fun onAddPicClick()
    }

    /**
     * 删除
     */
    fun delete(position: Int) {
        try {
            if (position != RecyclerView.NO_POSITION && list!!.size > position) {
                list!!.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, list!!.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    fun setList(list: List<LocalMedia>?) {
        this.list = list as ArrayList<LocalMedia>?
    }

    fun getData(): List<LocalMedia?>? {
        return if (list == null) ArrayList() else list
    }

    fun remove(position: Int) {
        if (list != null && position < list!!.size) {
            list!!.removeAt(position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mImg: ImageView = view.findViewById(R.id.fiv)
        var mIvDel: ImageView = view.findViewById(R.id.iv_del)
        var tvDuration: TextView = view.findViewById(R.id.tv_duration)
    }

    override fun getItemCount(): Int {
        return if (list!!.size < selectMax) {
            list!!.size + 1
        } else {
            list!!.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem(position)) {
            TYPE_CAMERA
        } else {
            TYPE_PICTURE
        }
    }

    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View = mInflater.inflate(
            R.layout.gv_filter_image,
            viewGroup, false
        )
        return ViewHolder(view)
    }

    private fun isShowAddItem(position: Int): Boolean {
        val size = if (list!!.size == 0) 0 else list!!.size
        return position == size
    }

    /**
     * 设置值
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.drawable.ic_add_image)
            viewHolder.mImg.setOnClickListener { v: View? -> mOnAddPicClickListener.onAddPicClick() }
            viewHolder.mIvDel.visibility = View.INVISIBLE
        } else {
            viewHolder.mIvDel.visibility = View.VISIBLE
            viewHolder.mIvDel.setOnClickListener { view: View? ->
                val index = viewHolder.adapterPosition
                // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                if (index != RecyclerView.NO_POSITION && list!!.size > index) {
                    list!!.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, list!!.size)
                }
            }
            val media: LocalMedia = list!![position]
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                return
            }
            val chooseModel: Int = media.getChooseModel()
            val path: String
            path = if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                media.getCutPath()
            } else if (media.isCompressed() || media.isCut() && media.isCompressed()) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                media.getCompressPath()
            } else if (PictureMimeType.isHasVideo(media.getMimeType()) && !TextUtils.isEmpty(media.getCoverPath())) {
                media.getCoverPath()
            } else {
                // 原图
                media.getPath()
            }
            Log.i(TAG, "原图地址::" + media.getPath())
            if (media.isCut()) {
                Log.i(TAG, "裁剪地址::" + media.getCutPath())
            }
            if (media.isCompressed()) {
                Log.i(TAG, "压缩地址::" + media.getCompressPath())
                Log.i(TAG, "压缩后文件大小::" + File(media.getCompressPath()).length() / 1024 + "k")
            }
            if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                Log.i(TAG, "Android Q特有地址::" + media.getAndroidQToPath())
            }
            if (media.isOriginal()) {
                Log.i(TAG, "是否开启原图功能::" + true)
                Log.i(TAG, "开启原图功能后地址::" + media.getOriginalPath())
            }
            val duration: Long = media.getDuration()
            viewHolder.tvDuration.visibility = if (PictureMimeType.isHasVideo(media.getMimeType())) View.VISIBLE else View.GONE
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.tvDuration.visibility = View.VISIBLE
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.picture_icon_audio, 0, 0, 0)
            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.picture_icon_video, 0, 0, 0)
            }
            viewHolder.tvDuration.text = DateUtils.formatDurationTime(duration)
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.picture_audio_placeholder)
            } else {
                Glide.with(viewHolder.itemView.context)
                    .load(if (PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed()) Uri.parse(path) else path)
                    .centerCrop()
                    .placeholder(R.color.app_color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg)
            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener { v: View? ->
                    val adapterPosition = viewHolder.adapterPosition
                    mItemClickListener!!.onItemClick(v, adapterPosition)
                }
            }
            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener { v: View? ->
                    val adapterPosition = viewHolder.adapterPosition
                    mItemLongClickListener!!.onItemLongClick(viewHolder, adapterPosition, v)
                    true
                }
            }
        }
    }

    private var mItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    private var mItemLongClickListener: OnItemLongClickListener? = null

    fun setItemLongClickListener(l: OnItemLongClickListener?) {
        mItemLongClickListener = l
    }

    companion object {
        const val TAG = "PictureSelector"
        const val TYPE_CAMERA = 1
        const val TYPE_PICTURE = 2
    }

    init {
        mInflater = LayoutInflater.from(context)
        this.mOnAddPicClickListener = mOnAddPicClickListener
    }
}