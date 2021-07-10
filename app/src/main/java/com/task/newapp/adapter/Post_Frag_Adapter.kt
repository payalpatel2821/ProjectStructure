package com.task.newapp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.task.newapp.R
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.utils.showLog


class Post_Frag_Adapter(var context: Context, all_post: List<All_Post_Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    var HEIGHT = 0
    var WIDTH = 0
    var all_post: ArrayList<All_Post_Data> = all_post as ArrayList<All_Post_Data>
    var onItemClick: ((View, Int) -> Unit)? = null
    var displayMetrics: DisplayMetrics
    lateinit var holder: StatusHolder
    private val isCaching = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
            StatusHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (all_post!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is StatusHolder) {
            populateItemRows(viewHolder as StatusHolder, position, null)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder as LoadingViewHolder, position)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (viewHolder is StatusHolder) {
            populateItemRows(viewHolder as StatusHolder, position, payloads)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder as LoadingViewHolder, position)
        }
    }

    /**
     * populate rows
     *
     * @param holder
     * @param position
     */
    private fun populateItemRows(holder: StatusHolder, position: Int, listPayload: List<Any>?) {

        this.holder = holder
        val all_post_data: All_Post_Data? = all_post!![position]

        if (!(listPayload == null || listPayload.isEmpty())) {
            Log.e("populateItemRows: ", listPayload.toString())
            for (payload in listPayload) {
                when (payload) {
                    "likePayload" -> {
                        when (all_post_data!!.isLike) {
                            0 -> holder.imgLike.setImageResource(R.drawable.ic_unlike)
                            1 -> holder.imgLike.setImageResource(R.drawable.ic_like_fill)
                        }
                        holder.txtLikeCount.text = all_post_data.likesCount.toString()
                    }
                    "savePayload" -> {
                        when (all_post_data!!.isSave) {
                            0 -> holder.imgSave.setImageResource(R.drawable.ic_save)
                            1 -> holder.imgSave.setImageResource(R.drawable.ic_save_fill)
                        }
                    }
                    "commentPayload" -> {
                        //last Comment show
                        holder.edt_comment.setText("")

                        if (all_post_data!!.latest_comment?.commentText.isNullOrEmpty()) {
                            holder.txtComment.visibility = View.GONE
                        } else {
                            holder.txtComment.text = all_post_data.latest_comment.commentText
                            holder.txtComment.visibility = View.VISIBLE
                        }
                        holder.txtCommentCount.text = all_post_data.commentsCount.toString()
                    }
                }
            }

        } else {

            holder.post_img.visibility = View.VISIBLE
            if (all_post_data?.contents!!.isNotEmpty()) {

                holder.edt_comment.setText("")

                //last Comment show
                if (all_post_data.latest_comment?.commentText.isNullOrEmpty()) {
                    holder.txtComment.visibility = View.GONE
                } else {
                    holder.txtComment.text = all_post_data.latest_comment.commentText
                    holder.txtComment.visibility = View.VISIBLE
                }

                when (all_post_data!!.isLike) {
                    0 -> holder.imgLike.setImageResource(R.drawable.ic_unlike)
                    1 -> holder.imgLike.setImageResource(R.drawable.ic_like_fill)
                }

                when (all_post_data!!.isSave) {
                    0 -> holder.imgSave.setImageResource(R.drawable.ic_save)
                    1 -> holder.imgSave.setImageResource(R.drawable.ic_save_fill)
                }

                holder.txtLikeCount.text = all_post_data.likesCount.toString()
                holder.txtCommentCount.text = all_post_data.commentsCount.toString()

                Glide.with(context).asBitmap()
                    .load(all_post_data!!.contents[0].thumb)
//                .thumbnail(0.25f)
                    .centerCrop()
                    .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                    .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                    .error(R.drawable.gallery_post)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            holder.img_view.visibility = View.VISIBLE
                        }

                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                            try {
                                holder.img_default.visibility = View.GONE
                                holder.post_img.setImageBitmap(bitmap)

                                val pxl = bitmap.getPixel(500, 500)
                                holder.imgShadow.setColorFilter(pxl)
                            } catch (ignore: Exception) {
                            }
                        }
                    })

                //------------------------Profile Image-------------------
                Glide.with(context)
                    .load(all_post_data!!.user.profileImage)
                    .thumbnail(0.25f)
                    .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                    .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                    .error(R.drawable.gallery_post)
                    .into(holder.img_view)

                val requestOptions = RequestOptions()
                requestOptions.isMemoryCacheable

                holder.img_playpause.visibility = View.VISIBLE

                if (all_post_data!!.contents[0].type == "video") {
                    holder.img_playpause.visibility = View.VISIBLE
                } else {
                    holder.img_playpause.visibility = View.INVISIBLE
                }

//            Glide.with(context)
//                .load(all_post_data.contents[0].thumb)
//                .thumbnail(0.25f)
//                .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
//                .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
//                .error(R.drawable.gallery_post) //.skipMemoryCache(!isCaching)
//                // .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
//                .listener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
//                        holder.img_default.visibility = View.VISIBLE
//                        return false
//                    }
//
//                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                        holder.img_default.visibility = View.GONE
//                        return false
//                    }
//                })
//                .into(holder.post_img)
            }
            holder.itemView.post { holder.itemView.requestLayout() }
            if (all_post_data!!.contents.size > 1) {
                holder.img_multi.visibility = View.VISIBLE
            } else {
                holder.img_multi.visibility = View.INVISIBLE
            }

            holder.txtTime.text = all_post_data.createdAt

            holder.txtLocation.visibility = if (all_post_data.isLocation == 1) View.VISIBLE else View.GONE
            all_post_data.location?.let {
                holder.txtLocation.text = all_post_data.location
            }

            if (all_post_data.isPostForPage == 1) {
                if (all_post_data.page.isNotEmpty()) {

                    if (!all_post_data.title.isNullOrEmpty()) {
                        holder.title_txt.text = all_post_data.title
                        holder.title_txt.visibility = View.VISIBLE
                    } else {
                        holder.title_txt.visibility = View.GONE
                    }
                    holder.name_txt.text = all_post_data.page[0].name
                }
            } else {
                holder.name_txt.text = all_post_data.user.firstName + " " + all_post_data.user.lastName
            }

            if (!all_post_data.title.isNullOrEmpty()) {
                holder.title_txt.text = all_post_data.title
                holder.title_txt.visibility = View.VISIBLE
            } else {
                holder.title_txt.visibility = View.GONE
            }

        }

        //Click events
        holder.imgLike.setOnClickListener {
            if (onItemClick != null) {
                onItemClick?.invoke(it, position)
            }
        }
        holder.txtPost.setOnClickListener {
            if (onItemClick != null) {
                onItemClick?.invoke(it, position)
            }
        }
        holder.imgSave.setOnClickListener {
            if (onItemClick != null) {
                onItemClick?.invoke(it, position)
            }
        }
        holder.llComment.setOnClickListener {
            if (onItemClick != null) {
                onItemClick?.invoke(it, position)
            }
        }
        holder.more_iv.setOnClickListener {
            if (onItemClick != null) {
                onItemClick?.invoke(it, position)
            }
        }
    }

    fun getScreenSize(context: Context) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        HEIGHT = displayMetrics.heightPixels
        WIDTH = displayMetrics.widthPixels
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    fun setdata(all_post: ArrayList<All_Post_Data>) {
//        for (i in all_post.indices) {
//            this.all_post!!.add(all_post[i])
//            notifyItemInserted(this.all_post!!.size - 1)
//        }

        this.all_post.addAll(all_post)
        notifyItemInserted(this.all_post!!.size - 1)
        showLog("all_post", this.all_post.size.toString())
    }

    fun getdata(): ArrayList<All_Post_Data> {
        return all_post
    }

    fun updateLikesCount(count: Int, position: Int, isLike: Int) {
        Log.e("updateLikesCount: ", count.toString())

        this.all_post[position].likesCount = count
        this.all_post[position].isLike = isLike
//        notifyDataSetChanged()
        notifyItemChanged(position, "likePayload")
    }

    fun updateSave(isSave: Int, position: Int) {
        Log.e("updateSave: ", isSave.toString())

        this.all_post[position].isSave = isSave
//        notifyDataSetChanged()
        notifyItemChanged(position, "savePayload")
    }

    fun getAddedComment(): String = holder.edt_comment.text.toString()

    fun updateComment(data: ResponsePostComment.Data, position: Int) {
        this.all_post[position].latest_comment = data
//        notifyDataSetChanged()
        notifyItemChanged(position, "commentPayload")
    }

    fun postComment() {
        notifyDataSetChanged()
    }

    fun clear() {
        this.all_post.clear()
        notifyDataSetChanged()
    }

    fun removedata(position: Int) {
        all_post!!.removeAt(position)
        notifyItemRangeRemoved(position, all_post!!.size)
    }

    override fun getItemCount(): Int = all_post!!.size

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    inner class StatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_view: ImageView = itemView.findViewById(R.id.img_view)
        var img_multi: ImageView = itemView.findViewById(R.id.img_multi)
        var img_playpause: ImageView = itemView.findViewById(R.id.img_playpause)
        var img_default: ImageView = itemView.findViewById(R.id.img_default)
        var more_iv: ImageView = itemView.findViewById(R.id.more_iv)
        var txtTime: TextView = itemView.findViewById(R.id.txtTime)
        var title_txt: TextView = itemView.findViewById(R.id.title_txt)
        var name_txt: TextView = itemView.findViewById(R.id.name_txt)
        var post_img: ImageView = itemView.findViewById(R.id.post_img)
        var txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        var imgShadow: ImageView = itemView.findViewById(R.id.imgShadow)
        var imgLike: ImageView = itemView.findViewById(R.id.imgLike)
        var txtPost: TextView = itemView.findViewById(R.id.txtPost)
        var imgSave: ImageView = itemView.findViewById(R.id.imgSave)
        var txtLikeCount: TextView = itemView.findViewById(R.id.txtLikeCount)
        var txtCommentCount: TextView = itemView.findViewById(R.id.txtCommentCount)
        var edt_comment: EditText = itemView.findViewById(R.id.edt_comment)
        var llComment: LinearLayoutCompat = itemView.findViewById(R.id.llComment)
        var txtComment: TextView = itemView.findViewById(R.id.txtComment)
    }

    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        StrictMode.setThreadPolicy(policy)
        getScreenSize(context)
    }


}