package com.task.newapp.adapter.post

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
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
import com.task.newapp.utils.setUnderlineColor
import com.task.newapp.utils.showLog


class Post_Frag_Adapter(var context: Context, all_post: List<All_Post_Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val VIEW_TYPE_LOCATION = 2

    //    private val VIEW_TYPE_THOUGHT = 1
    private val VIEW_TYPE_ITEM = 0

    //    private val VIEW_TYPE_LOADING = 1
    var HEIGHT = 0
    var WIDTH = 0
    var all_post: ArrayList<All_Post_Data> = all_post as ArrayList<All_Post_Data>
    var onItemClick: ((View, Int) -> Unit)? = null
    var displayMetrics: DisplayMetrics
    private val isCaching = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
                StatusHolder(view)
            }
//            VIEW_TYPE_THOUGHT -> {
//                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_thought, parent, false)
//                ThoughtHolder(view)
//            }
            else -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
//        return when (all_post!![position].type) {
//            "thought" -> VIEW_TYPE_THOUGHT
//            else -> VIEW_TYPE_ITEM
//        }

        return VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is StatusHolder -> {
                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, null)
            }
//            is LoadingViewHolder -> {
//                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, null)
//            }
            is LoadingViewHolder -> {
                showLoadingView(viewHolder as LoadingViewHolder, position)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        when (viewHolder) {
            is StatusHolder -> {
                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, payloads)
            }
//            is ThoughtHolder -> {
//                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, payloads)
//            }
            is LoadingViewHolder -> {
                showLoadingView(viewHolder as LoadingViewHolder, position)
            }
        }
    }

    /**
     * populate rows
     *
     * @param holder
     * @param position
     */
    private fun populateItemRows(holderCommon: RecyclerView.ViewHolder, position: Int, listPayload: List<Any>?) {
        val all_post_data: All_Post_Data? = all_post!![position]

        var holder: StatusHolder = (holderCommon as StatusHolder)

        if (!(listPayload == null || listPayload.isEmpty())) {
            Log.e("populateItemRows: ", listPayload.toString())

            for (payload in listPayload) {
                when (payload) {
                    "likePayload" -> {
                        when (all_post_data!!.isLike) {
                            0 -> holder.imgLike.setImageResource(R.drawable.ic_not_like)
                            1 -> holder.imgLike.setImageResource(R.drawable.ic_like)
                        }
                        holder.txtLikeCount.text = all_post_data.likesCount.toString()
                    }
                    "savePayload" -> {
                        when (all_post_data!!.isSave) {
                            0 -> holder.imgSave.setImageResource(R.drawable.ic_nonsave)
                            1 -> holder.imgSave.setImageResource(R.drawable.ic_save)
                        }
                    }
                    "commentPayload" -> {
                        //last Comment show
                        holder.edt_comment.setText("")
                        holder.edt_comment.isFocusable = false

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
            var holder: StatusHolder = (holderCommon as StatusHolder)

            holder.post_img.visibility = View.VISIBLE
            if (all_post_data?.contents!!.isNotEmpty()) {

                holder.edt_comment.setText("")

                when (all_post_data!!.type) {
                    "thought" -> {

                        var holder: StatusHolder = (holderCommon as StatusHolder)

                        //set thought layout
                        holder.rlThought.visibility = View.VISIBLE
                        holder.txtLocation.visibility = View.GONE

                        (holder as StatusHolder).setThought(all_post_data?.contents!![0].thoughtType, all_post_data?.contents!![0])

                    }
                    "location" -> {

                        //set location layout
                        holder.txtMore.visibility = View.GONE
                        holder.rlThought.visibility = View.GONE

                        //-----------------------Location-----------------------
                        holder.txtLocation.visibility = if (all_post_data.isLocation == 1) View.VISIBLE else View.GONE
                        all_post_data.location?.let {
                            holder.txtLocation.text = all_post_data.location
                        }
                    }
                    else -> {
                        //set normal layout
                        holder.txtMore.visibility = View.GONE
                        holder.txtLocation.visibility = View.GONE
                        holder.rlThought.visibility = View.GONE

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

                        //-----------------------------Common---------------------------------
                        //last Comment show
                        if (all_post_data.latest_comment?.commentText.isNullOrEmpty()) {
                            holder.txtComment.visibility = View.GONE
                        } else {
                            holder.txtComment.text = all_post_data.latest_comment.commentText
                            holder.txtComment.visibility = View.VISIBLE
                        }

                        when (all_post_data!!.isLike) {
                            0 -> holder.imgLike.setImageResource(R.drawable.ic_not_like)
                            1 -> holder.imgLike.setImageResource(R.drawable.ic_like)
                        }

                        when (all_post_data!!.isSave) {
                            0 -> holder.imgSave.setImageResource(R.drawable.ic_nonsave)
                            1 -> holder.imgSave.setImageResource(R.drawable.ic_save)
                        }

                        holder.txtLikeCount.text = all_post_data.likesCount.toString()
                        holder.txtCommentCount.text = all_post_data.commentsCount.toString()

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

                        if (all_post_data!!.contents[0].type == "video")
                            holder.img_playpause.visibility = View.VISIBLE
                        else
                            holder.img_playpause.visibility = View.INVISIBLE


                        holder.itemView.post { holder.itemView.requestLayout() }
                        if (all_post_data!!.contents.size > 1) {
                            holder.img_multi.visibility = View.VISIBLE
                        } else {
                            holder.img_multi.visibility = View.INVISIBLE
                        }

                        holder.txtTime.text = all_post_data.createdAt
                    }
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

    fun setdata(all_post: ArrayList<All_Post_Data>, isRefresh: Boolean) {
//        for (i in all_post.indices) {
//            this.all_post!!.add(all_post[i])
//            notifyItemInserted(this.all_post!!.size - 1)
//        }

        if (isRefresh) {
            this.all_post.clear()
            this.all_post = ArrayList()
        }
        this.all_post.addAll(all_post)

//        notifyItemInserted(this.all_post!!.size - 1)
        notifyItemInserted(0)
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

        var rlThought: RelativeLayout = itemView.findViewById(R.id.rlThought)
        var txtThought: AppCompatEditText = itemView.findViewById(R.id.txtThought)
        var txtMore: AppCompatTextView = itemView.findViewById(R.id.txtMore)

        fun setThought(thoughtType: String, all_post_data: All_Post_Data.Content) {
            if (thoughtType == "text") {
                //normal text thought
                txtMore.visibility = View.GONE

                val backgroundType: String? = all_post_data.backgroundType   //pattern
                val color: String? = all_post_data.color
                val patternId: String = all_post_data.patternId
                val alignment: String? = all_post_data.alignment
                val fontStyle: String? = all_post_data.fontStyle
                val isBold: Int? = all_post_data.isBold
                val isItalic: Int? = all_post_data.isItalic
                val isUnderline: Int? = all_post_data.isUnderline
                val content: String? = all_post_data.content

                if (backgroundType == "pattern") {
                    Glide.with(context).asBitmap()
                        .load(patternId)
                        .centerCrop()
                        .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                        .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                        .error(R.drawable.gallery_post)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                img_view.visibility = View.VISIBLE
                            }

                            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                                try {
                                    img_default.visibility = View.GONE
                                    post_img.setImageBitmap(bitmap)

                                    val pxl = bitmap.getPixel(500, 500)
                                    imgShadow.setColorFilter(pxl)
                                } catch (ignore: Exception) {
                                }
                            }
                        })
                } else {
                    post_img.setBackgroundColor(Color.parseColor(color))
                }

                content?.let {
                    txtThought.setText(content.toString())
                }

                fontStyle?.let {
                    txtThought.typeface = Typeface.createFromAsset(context.assets, "fonts/$it")
                }

                alignment?.let {
                    when (it) {
                        "center" -> rlThought.gravity = Gravity.CENTER
                        "left" -> rlThought.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                        "center" -> rlThought.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    }
                }

                isBold?.let {
                    if (it == 0) {
                        txtThought.setTypeface(null, if (isItalic == 1) Typeface.BOLD_ITALIC else Typeface.BOLD)
                    } else {
                        txtThought.setTypeface(null, if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
                    }
                }

                isUnderline?.let {
                    if (it == 0) {
                        txtThought.setUnderlineColor(Color.TRANSPARENT)
                    } else {
                        txtThought.setUnderlineColor(Color.BLACK)
                    }
                }

            } else {
                //Multiline text thought
                txtMore.visibility = View.VISIBLE
            }
        }
    }

//    inner class ThoughtHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var rlThought: RelativeLayout = itemView.findViewById(R.id.rlThought)
//        var txtThought: AppCompatEditText = itemView.findViewById(R.id.txtThought)
//        var imgBg: ImageView = itemView.findViewById(R.id.imgBg)
//    }

    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        StrictMode.setThreadPolicy(policy)
        getScreenSize(context)
    }

}