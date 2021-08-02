package com.task.newapp.adapter.post

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.task.newapp.R
import com.task.newapp.databinding.ItemPostBinding
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.utils.setUnderlineColor


class PostFragAdapter(var context: Context, all_post: List<All_Post_Data>) : RecyclerView.Adapter<PostFragAdapter.StatusHolder>() {
    private val VIEW_TYPE_LOCATION = 2

    //    private val VIEW_TYPE_THOUGHT = 1
    private val VIEW_TYPE_ITEM = 0

    //    private val VIEW_TYPE_LOADING = 1
    var HEIGHT = 0
    var WIDTH = 0
    var all_post: ArrayList<All_Post_Data> = all_post as ArrayList<All_Post_Data>
    var onItemClick: ((View, Int, String) -> Unit)? = null
    var displayMetrics: DisplayMetrics
    lateinit var fontArrayThoughts: Array<String>

    //    lateinit var postList: Array<String>
    private val isCaching = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusHolder {
//        return
//            VIEW_TYPE_ITEM -> {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
//        return StatusHolder(view, this)

        val layoutBinding: ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_post, parent, false)
        return StatusHolder(layoutBinding, this)

//            }
//            VIEW_TYPE_THOUGHT -> {
//                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_thought, parent, false)
//                ThoughtHolder(view)
//            }
//            else -> {
//                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item_loading, parent, false)
//                LoadingViewHolder(view)
//            }
//    }
    }

    override fun getItemViewType(position: Int): Int {
//        return when (all_post!![position].type) {
//            "thought" -> VIEW_TYPE_THOUGHT
//            else -> VIEW_TYPE_ITEM
//        }

        return VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: StatusHolder, position: Int) {
//        when (viewHolder) {
//            is StatusHolder -> {
        val allPostData: All_Post_Data = this.all_post[position]
        viewHolder.populateItemRows(allPostData, null)
//            }
//            is LoadingViewHolder -> {
//                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, null)
//            }
//            is LoadingViewHolder -> {
//                showLoadingView(viewHolder as LoadingViewHolder, position)
//            }
//        }
    }

    override fun onBindViewHolder(viewHolder: StatusHolder, position: Int, payloads: List<Any>) {
//        when (viewHolder) {
//            is StatusHolder -> {
        val allPostData: All_Post_Data = this.all_post[position]
        viewHolder.populateItemRows(allPostData, payloads)
//            }
//            is ThoughtHolder -> {
//                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, payloads)
//            }
//            is LoadingViewHolder -> {
//                showLoadingView(viewHolder as LoadingViewHolder, position)
//            }
//        }
    }


    fun getScreenSize(context: Context) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        HEIGHT = displayMetrics.heightPixels
        WIDTH = displayMetrics.widthPixels
    }

//    fun getCommentsList(): ArrayList<String> {
//        val pCommentList: ArrayList<String> = ArrayList()
//        for (i in postList.indices) {
//            pCommentList.add(postList[i])
//        }
//        return pCommentList
//    }

//    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
//        //ProgressBar would be displayed
//    }

    fun setdata(all_postNew: ArrayList<All_Post_Data>, isRefresh: Boolean) {
//        if (isRefresh) {
//            this.all_post.clear()
//            this.all_post = ArrayList()
//        }
//        this.all_post.addAll(all_postNew)
//        notifyItemInserted(0)
//        showLog("all_post", this.all_post.size.toString())

        //-----------------Add New-----------------------

        val diffCallback = PostDiffCallback(this.all_post, all_postNew)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.all_post.clear()
        this.all_post.addAll(all_postNew)
        diffResult.dispatchUpdatesTo(this)
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
        this.all_post[position].commentsCount = data.totalComment
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

//    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
//    }

    inner class StatusHolder(private val layoutBinding: ItemPostBinding, private val mAdapter: PostFragAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {

        fun populateItemRows(all_post_data: All_Post_Data, listPayload: List<Any>?) {

            if (!(listPayload == null || listPayload.isEmpty())) {
                Log.e("populateItemRows: ", listPayload.toString())

                for (payload in listPayload) {
                    when (payload) {
                        "likePayload" -> {
                            when (all_post_data!!.isLike) {
                                0 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_not_like)
                                1 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_like)
                            }
                            layoutBinding.txtLikeCount.text = all_post_data.likesCount.toString()
                        }
                        "savePayload" -> {
                            when (all_post_data!!.isSave) {
                                0 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_nonsave)
                                1 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_save)
                            }
                        }
                        "commentPayload" -> {
                            //last Comment show
                            layoutBinding.edtComment.setText("")
                            layoutBinding.edtComment.clearFocus()

                            if (all_post_data!!.latest_comment?.commentText.isNullOrEmpty()) {
                                layoutBinding.txtComment.visibility = View.GONE
                            } else {
                                layoutBinding.txtComment.text = all_post_data.latest_comment.commentText
                                layoutBinding.txtComment.visibility = View.VISIBLE
                            }
                            layoutBinding.txtCommentCount.text = all_post_data.commentsCount.toString()
                        }
                    }
                }

            } else {
//                var layoutBinding: StatusHolder = (holderCommon as StatusHolder)

                layoutBinding.postImg.visibility = View.VISIBLE
                if (all_post_data?.postContents!!.isNotEmpty()) {

                    layoutBinding.edtComment.setText("")

                    when (all_post_data!!.type) {
                        "thought", "article" -> {

//                            var layoutBinding: StatusHolder = (holderCommon as StatusHolder)

                            //set thought layout
                            layoutBinding.rlThought.visibility = View.VISIBLE
                            layoutBinding.txtLocation.visibility = View.GONE

                            setThought(all_post_data?.postContents!![0].thoughtType, all_post_data?.postContents!![0])

                        }
                        "location" -> {

                            //set location layout
                            layoutBinding.txtMore.visibility = View.GONE
                            layoutBinding.rlThought.visibility = View.GONE

                            //-----------------------Location-----------------------
                            layoutBinding.txtLocation.visibility = if (all_post_data.isLocation == 1) View.VISIBLE else View.GONE
                            all_post_data.location?.let {
                                layoutBinding.txtLocation.text = all_post_data.location
                            }
                        }
                        else -> {
                            //set normal layout
                            layoutBinding.txtMore.visibility = View.GONE
                            layoutBinding.txtLocation.visibility = View.GONE
                            layoutBinding.rlThought.visibility = View.GONE

                            Glide.with(context).asBitmap()
                                .load(all_post_data!!.postContents[0].thumb)
                                //                .thumbnail(0.25f)
                                .centerCrop()
                                .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                                .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                                .error(R.drawable.gallery_post)
                                .into(object : CustomTarget<Bitmap?>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        layoutBinding.imgView.visibility = View.VISIBLE
                                    }

                                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                                        try {
                                            layoutBinding.imgDefault.visibility = View.GONE
                                            layoutBinding.postImg.setImageBitmap(bitmap)

                                            val pxl = bitmap.getPixel(500, 500)
                                            layoutBinding.imgShadow.setColorFilter(pxl)
                                        } catch (ignore: Exception) {
                                        }
                                    }
                                })
                        }
                    }

                    //-----------------------------Common---------------------------------
                    //last Comment show
                    if (all_post_data.latest_comment?.commentText.isNullOrEmpty()) {
                        layoutBinding.txtComment.visibility = View.GONE
                    } else {
                        layoutBinding.txtComment.text = all_post_data.latest_comment.commentText
                        layoutBinding.txtComment.visibility = View.VISIBLE
                    }

                    when (all_post_data!!.isLike) {
                        0 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_not_like)
                        1 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_like)
                    }

                    when (all_post_data!!.isSave) {
                        0 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_nonsave)
                        1 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_save)
                    }

                    layoutBinding.txtLikeCount.text = all_post_data.likesCount.toString()
                    layoutBinding.txtCommentCount.text = all_post_data.commentsCount.toString()

                    if (all_post_data.isPostForPage == 1) {
                        if (all_post_data.page.isNotEmpty()) {

                            if (!all_post_data.title.isNullOrEmpty()) {
                                layoutBinding.titleTxt.text = all_post_data.title
                                layoutBinding.titleTxt.visibility = View.VISIBLE
                            } else {
                                layoutBinding.titleTxt.visibility = View.GONE
                            }
                            layoutBinding.nameTxt.text = all_post_data.page[0].name
                        }
                    } else {
                        var fullName = (all_post_data.user.firstName ?: "") + " " + (all_post_data.user.lastName ?: "")

                        //Check User Tag and add with name
                        if (all_post_data.tagged.isEmpty()) {
                            layoutBinding.nameTxt.text = fullName
                        } else {
                            val commaSeperatedTagsNames = all_post_data.tagged.joinToString { it ->
                                "${
                                    (it.first_name ?: "").plus(" ").plus(it.last_name ?: "")
                                }"
                            }

//                            val styledText = "This is <font color='red'>simple</font>."
                            val styledText = "$fullName <font color='#AAA1A1'> was with </font>$commaSeperatedTagsNames"

                            layoutBinding.nameTxt.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);


//                            var wasWithText = Html.fromHtml(
//                                "<font color=#cc0029>" + "<b>"
//                                        + "Hiiiiiiiiii" + "</b>" + "<br />" + "<small>" + "description"
//                                        + "</small>" + "<br />" + "<small>" + "DateAdded" + "</small>"
//                            )
//
//                            layoutBinding.nameTxt.text = Html.fromHtml(
//                                (all_post_data.user.firstName ?: "") + " " + (all_post_data.user.lastName ?: "")
//                                    .plus(wasWithText).plus(commaSeperatedTagsNames)
//                            )

//                            layoutBinding.nameTxt.text = Html.fromHtml(
//                                (all_post_data.user.firstName ?: "") + " " + (all_post_data.user.lastName ?: "")
//                                    .plus(wasWithText).plus(commaSeperatedTagsNames)
//                            )

                            Log.e("commaSeperatedTagsNames", "$commaSeperatedTagsNames,$adapterPosition")
                        }
                    }

                    if (!all_post_data.title.isNullOrEmpty()) {
                        layoutBinding.titleTxt.text = all_post_data.title
                        layoutBinding.titleTxt.visibility = View.VISIBLE
                    } else {
                        layoutBinding.titleTxt.visibility = View.GONE
                    }

                    Glide.with(context)
                        .load(all_post_data!!.user.profileImage)
                        .thumbnail(0.25f)
                        .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                        .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                        .error(R.drawable.gallery_post)
                        .into(layoutBinding.imgView)

                    val requestOptions = RequestOptions()
                    requestOptions.isMemoryCacheable

                    layoutBinding.imgPlaypause.visibility = View.VISIBLE

                    if (all_post_data!!.postContents[0].type == "video")
                        layoutBinding.imgPlaypause.visibility = View.VISIBLE
                    else
                        layoutBinding.imgPlaypause.visibility = View.INVISIBLE


                    layoutBinding.root.post { layoutBinding.root.requestLayout() }

                    if (all_post_data!!.postContents.size > 1) {
                        layoutBinding.imgMulti.visibility = View.VISIBLE
                    } else {
                        layoutBinding.imgMulti.visibility = View.INVISIBLE
                    }

                    layoutBinding.txtTime.text = all_post_data.createdAt
                }
            }
        }

        fun setThought(thoughtType: String, all_post_data: All_Post_Data.PostContent) {
            if (thoughtType == "text") {
                //normal text thought
                layoutBinding.txtMore.visibility = View.GONE
            } else {
                //Multiline text thought
                layoutBinding.txtMore.visibility = View.VISIBLE
            }
            val backgroundType: String? = all_post_data.backgroundType   //pattern
            val color: String? = all_post_data.color
            val fontColor: String? = all_post_data.fontColor
            val patternId: String = all_post_data.patternId
            val alignment: String? = all_post_data.alignment
            val fontStyle: String? = all_post_data.fontStyle
            val isBold: Int? = all_post_data.isBold
            val isItalic: Int? = all_post_data.isItalic
            val isUnderline: Int? = all_post_data.isUnderline
            val content: String? = all_post_data.content

            if (backgroundType == "pattern") {
                layoutBinding.postImg.setImageBitmap(null)
                layoutBinding.postImg.setBackgroundColor(0)

                Glide.with(context).asBitmap()
                    .load(patternId)
                    .centerCrop()
                    .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
                    .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
                    .error(R.drawable.gallery_post)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            layoutBinding.imgView.visibility = View.VISIBLE
                        }

                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                            try {
                                layoutBinding.imgDefault.visibility = View.GONE
                                layoutBinding.postImg.setImageBitmap(bitmap)

                                val pxl = bitmap.getPixel(500, 500)
                                layoutBinding.imgShadow.setColorFilter(pxl)
                            } catch (ignore: Exception) {
                            }
                        }
                    })
            } else {
                layoutBinding.postImg.setBackgroundColor(0)
                layoutBinding.postImg.setImageBitmap(null)

                if (color!!.startsWith("#"))
                    layoutBinding.postImg.setBackgroundColor(Color.parseColor(color))
                else
                    layoutBinding.postImg.setBackgroundColor(context.resources.getColor(R.color.white))
            }

            if (fontColor?.startsWith("#") == true) {
                layoutBinding.txtThought.setTextColor(Color.parseColor(fontColor))
            }

            content?.let {
                layoutBinding.txtThought.setText(content.toString())
            }


            Log.e("setThought: ", fontStyle.toString())
            fontStyle?.let {
//                Log.e("setThought:After Let ", Typeface.createFromAsset(context.assets, fontArrayThoughts[fontStyle.toInt()]).toString())
//                layoutBinding.txtThought.typeface = Typeface.createFromAsset(context.assets, fontArrayThoughts[fontStyle.toInt()])
            }

            alignment?.let {
                when (it) {
                    "center" -> layoutBinding.rlThought.gravity = Gravity.CENTER
                    "left" -> layoutBinding.rlThought.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                    "right" -> layoutBinding.rlThought.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                }
            }

            isBold?.let {
                if (it == 0) {
                    layoutBinding.txtThought.setTypeface(null, if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
                } else {
                    layoutBinding.txtThought.setTypeface(null, if (isItalic == 1) Typeface.BOLD_ITALIC else Typeface.BOLD)
                }
            }

            isUnderline?.let {
                if (it == 0) {
                    layoutBinding.txtThought.setUnderlineColor(Color.TRANSPARENT)
                } else {
                    layoutBinding.txtThought.setUnderlineColor(Color.BLACK)
                }
            }
        }

        init {
            layoutBinding.imgLike.setOnClickListener(this)
            layoutBinding.txtPost.setOnClickListener(this)
            layoutBinding.imgSave.setOnClickListener(this)
            layoutBinding.llComment.setOnClickListener(this)
            layoutBinding.moreIv.setOnClickListener(this)
            layoutBinding.rlMain.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.imgLike, R.id.imgSave, R.id.llComment, R.id.more_iv, R.id.rlMain -> {
                    if (onItemClick != null) {
                        onItemClick?.invoke(v, adapterPosition, "")
                    }
                }
                R.id.txtPost -> {
                    if (onItemClick != null) {
                        onItemClick?.invoke(v, adapterPosition, layoutBinding.edtComment.text.toString().trim())
                    }
                }
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
        fontArrayThoughts = context.resources.getStringArray(R.array.fontArrayThoughts)
        //postList = ArrayList<String>(this.all_post.size)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class PostDiffCallback(oldPostList: List<All_Post_Data>, newPostList: List<All_Post_Data>) : DiffUtil.Callback() {
        private val mOldPostList: List<All_Post_Data> = oldPostList
        private val mNewPostList: List<All_Post_Data> = newPostList
        override fun getOldListSize(): Int {
            return mOldPostList.size
        }

        override fun getNewListSize(): Int {
            return mNewPostList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldPostList[oldItemPosition].id === mNewPostList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPostData: All_Post_Data = mOldPostList[oldItemPosition]
            val newPostData: All_Post_Data = mNewPostList[newItemPosition]

//            return oldPostData.id == newPostData.id
            return oldPostData == newPostData

//            return oldPostData.id == newPostData.id &&
//                    oldPostData.commentsCount == newPostData.commentsCount &&
//                    oldPostData.isLike == newPostData.isLike &&
//                    oldPostData.isSave == newPostData.isSave
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }
}